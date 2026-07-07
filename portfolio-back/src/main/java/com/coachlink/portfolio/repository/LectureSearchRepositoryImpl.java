package com.coachlink.portfolio.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.coachlink.portfolio.entity.SportName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.LectureImg;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class LectureSearchRepositoryImpl {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public Page<Object[]> searchLecture(
            PageRequestDTO pageRequestDTO,
            Double userX, Double userY,
            List<Long> sports,
            List<String> queries,
            LocalDateTime startTimeRange, LocalDateTime endTimeRange,
            String sort) {

        CriteriaBuilder cr = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> search = cr.createQuery(Object[].class);
        CriteriaQuery<Long> countQuery = cr.createQuery(Long.class);

        Root<Lecture> rootLect = search.from(Lecture.class);
//        rootLect.fetch("sportName");
        Join<Lecture, SportName> joinSportName = rootLect.join("sportName", JoinType.LEFT);
        Join<Lecture, LectureImg> joinImg = rootLect.join("lectureImg", JoinType.LEFT);
        joinImg.on(cr.equal(joinImg.get("isMainImg"), Boolean.TRUE));

        List<Predicate> predicates =
                buildPredicates(cr, rootLect, userX, userY, sports, queries, startTimeRange, endTimeRange);


        Expression<Double> dist = cr.literal(0.0);
        if (userX != null && userY != null) {
            int range = 50;
            double kmToDeg = 0.008983082;
            double yRange = kmToDeg * range; // (range)km는 위도 몇도?
            double xRange = yRange / Math.cos(userY * Math.PI / 180); // (range)km는 경도 몇도?
            Predicate xDist = cr.between(rootLect.get("coordsX"), userX - xRange, userX + xRange);
            Predicate yDist = cr.between(rootLect.get("coordsY"), userY - yRange, userY + yRange);
            predicates.add(cr.and(xDist, yDist));

            Double distX = 111.054 * Math.cos(userX * Math.PI / 180);
            dist = cr.sqrt(cr.sum(
                    cr.prod(cr.prod(cr.sum(rootLect.get("coordsX"), -userX), distX),
                            cr.prod(cr.sum(rootLect.get("coordsX"), -userX), distX)),
                    cr.prod(cr.prod(cr.sum(rootLect.get("coordsY"), -userY), 111.054),
                            cr.prod(cr.sum(rootLect.get("coordsY"), -userY), 111.054))));
        }

        if (sports != null && !sports.isEmpty()) {
            predicates.add(rootLect.get("sportName").get("sportId").in(sports));
        }

        int len = queries.size(); // Service에서 무조건 ArrayList가 내려오게 되어있기 때문에 문제없다
        if (len > 0) {
            Predicate[] temp = new Predicate[4];
            Predicate[] list = new Predicate[len];
            for (int i = 0; i < len; i++) {
                String str = queries.get(i);
                temp[0] = cr.like(rootLect.get("lectureName"), "%" + str + "%");
                temp[1] = cr.like(rootLect.get("lectureContent"), "%" + str + "%");
                temp[2] = cr.like(rootLect.get("member").get("username"), "%" + str + "%");
                temp[3] = cr.like(rootLect.get("facilityName"), "%" + str + "%");
                list[i] = cr.or(temp); // 각각의 검색어는 4개의 필드 중 어디에 있어도 된다
            }
            predicates.add(cr.and(list)); // 그러나 각각의 검색어를 모두 포함해야 한다
        }

        Order sortArr[] = new Order[2];
        if (sort != null && sort.equals("dist")) {
            sortArr[0] = cr.asc(dist);
            sortArr[1] = cr.asc(rootLect.get("lectureStartTime"));
        } else {
            sortArr[0] = cr.asc(rootLect.get("lectureStartTime"));
            sortArr[1] = cr.asc(dist);
        }

        if (startTimeRange != null) {
            predicates.add(cr.greaterThanOrEqualTo(rootLect.get("lectureStartTime"), startTimeRange));
        }
        if (endTimeRange != null) {
            predicates.add(cr.lessThanOrEqualTo(rootLect.get("lectureStartTime"), endTimeRange));
        }

        search.multiselect(rootLect, joinImg, dist).where(predicates.toArray(new Predicate[0]))
                .orderBy(sortArr);

        int page = pageRequestDTO.getPage();
        int size = pageRequestDTO.getSize();
        int offset = (page - 1) * size;

        List<Object[]> list = em.createQuery(search)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

//        countQuery.select(cr.count(rootLect)).where(predicates.toArray(new Predicate[0]));
        Root<Lecture> countRoot = countQuery.from(Lecture.class);
        List<Predicate> countPredicates =
                buildPredicates(cr, countRoot, userX, userY, sports, queries, startTimeRange, endTimeRange);

        countQuery.select(cr.count(countRoot))
                .where(countPredicates.toArray(new Predicate[0]));
        long total = (Long) em.createQuery(countQuery).getSingleResult();

        // return list;

        return new PageImpl<Object[]>(list, pageRequestDTO.getPageable(sort, Direction.ASC), (int) total);
    }


    private List<Predicate> buildPredicates(
            CriteriaBuilder cr,
            Root<Lecture> rootLect,
            Double userX, Double userY,
            List<Long> sports,
            List<String> queries,
            LocalDateTime startTimeRange, LocalDateTime endTimeRange
    ) {

        List<Predicate> predicates = new ArrayList<>();

        if (userX != null && userY != null) {
            int range = 50;
            double kmToDeg = 0.008983082;
            double yRange = kmToDeg * range;
            double xRange = yRange / Math.cos(userY * Math.PI / 180);
            Predicate xDist = cr.between(rootLect.get("coordsX"), userX - xRange, userX + xRange);
            Predicate yDist = cr.between(rootLect.get("coordsY"), userY - yRange, userY + yRange);
            predicates.add(cr.and(xDist, yDist));
        }

        if (sports != null && !sports.isEmpty()) {
            predicates.add(rootLect.get("sportName").get("sportId").in(sports));
        }

        if (queries != null && !queries.isEmpty()) {
            int len = queries.size();
            Predicate[] temp = new Predicate[4];
            Predicate[] list = new Predicate[len];
            for (int i = 0; i < len; i++) {
                String str = queries.get(i);
                temp[0] = cr.like(rootLect.get("lectureName"), "%" + str + "%");
                temp[1] = cr.like(rootLect.get("lectureContent"), "%" + str + "%");
                temp[2] = cr.like(rootLect.get("member").get("username"), "%" + str + "%");
                temp[3] = cr.like(rootLect.get("facilityName"), "%" + str + "%");
                list[i] = cr.or(temp);
            }
            predicates.add(cr.and(list));
        }

        if (startTimeRange != null) {
            predicates.add(cr.greaterThanOrEqualTo(rootLect.get("lectureStartTime"), startTimeRange));
        }
        if (endTimeRange != null) {
            predicates.add(cr.lessThanOrEqualTo(rootLect.get("lectureStartTime"), endTimeRange));
        }

        return predicates;
    }

}
