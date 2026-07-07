package com.coachlink.portfolio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.LectureImg;
import com.coachlink.portfolio.repository.LectureRepository;
import com.coachlink.portfolio.repository.SportNameRepository;
import com.coachlink.portfolio.service.LectureService;

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

@SpringBootTest
public class SearchingTests {

	@Autowired
	LectureService lServ;

	@Autowired
	SportNameRepository snRepo;

	@Autowired
	LectureRepository lRepo;

	@PersistenceContext
	EntityManager em;

	@Test
	public void searchLecture() {
		String queryString = "농구";
		// 경도가 가로(x)니까 먼저
		// Double[] coords = { 126.99, 37.56 };
		Double[] coords = { null, null };

		Long[] sportIdsTemp = {};
		List<Long> sportIds = Arrays.asList(sportIdsTemp);

		PageRequestDTO pageDTO = new PageRequestDTO(1, 10);

//		PageResponseDTO<LectureDTO, Object[]> result = lServ.searchLecture(pageDTO, coords[0], coords[1],
//				sportIds,
//				queryString, null, null, "dist");
//
//		System.out.println(result.toString());
	}

	/*
	 * @Test
	 * public void searchQDSL() {
	 * 
	 * // PageRequestDTO pageRequestDTO,
	 * // Double coordsXUser, Double coordsYUser,
	 * // List<Long> sports,
	 * // String query,
	 * // LocalDateTime startTimeRange, LocalDateTime endTimeRange) {
	 * 
	 * Double userX = 126.99;
	 * Double userY = 37.56;
	 * Double factorX = 111.054 * Math.cos(userX * Math.PI / 180);
	 * 
	 * String sort = "lectureStartTime";
	 * // String sort = "dist";
	 * 
	 * int size = 10;
	 * 
	 * Pageable p = new PageRequestDTO(1, size).getPageable(
	 * sort,
	 * Sort.Direction.ASC);
	 * 
	 * QLecture q = QLecture.lecture;
	 * 
	 * BooleanBuilder bb = new BooleanBuilder();
	 * 
	 * List<Long> sports = new ArrayList<>();
	 * 
	 * String query = "농구";
	 * 
	 * LocalDateTime startTimeRange = null;
	 * LocalDateTime endTimeRange = null;
	 * 
	 * // 위도/경도 조건 만족
	 * if (userX != null && userY != null) {
	 * 
	 * double kmToDeg = 0.008983082;
	 * int range = 50; // 가로세로 50km 이내의 것만 조사
	 * double yFactor = kmToDeg * range; // (range)km는 위도 몇도?
	 * double xFactor = yFactor / Math.cos(userY * Math.PI / 180); // (range)km는 경도
	 * 몇도?
	 * BooleanExpression longitude = q.coordsX.between(userX - xFactor, userX +
	 * xFactor);
	 * BooleanExpression latitude = q.coordsY.between(userY - yFactor, userY +
	 * yFactor);
	 * 
	 * // System.out.println("l1_0.coordsx between " + (coordsXUser - xFactor) + "
	 * and
	 * // " + (coordsXUser + xFactor));
	 * // System.out.println("l1_0.coordsy between" + (coordsYUser - yFactor) + "
	 * and "
	 * // + (coordsYUser + yFactor));
	 * 
	 * bb.and(longitude).and(latitude);
	 * 
	 * }
	 * 
	 * // 선택한 스포츠 중 하나라도 걸리면 만족. 입력을 안하면 전체 검색
	 * if (sports != null && sports.size() > 0) {
	 * bb.and(q.sportName.sportId.in(sports));
	 * }
	 * 
	 * // 거리 계산.
	 * // x1, y1: 강좌 건물의 경도 / 위도
	 * // x2, y2: 사용자의 경도 / 위도
	 * // 위도 1도 = 111.045km
	 * // 경도 1도 @ 36N = (pi/180 * R) * cos(36/90 * pi/2)
	 * // = 90km
	 * // 36N으로 고정시키면 1km당 최대 10m 정도의 오차가 발생할 수 있음.
	 * // Haversine Great Circle Formula가 있으나 아주 느리다고 함.
	 * // 한국 범위내에서는 d = sqrt(x2 + y2) 를 써도 오차가 거의 없는 것 같음.
	 * 
	 * if (query != null && query.length() > 0) {
	 * // 검색어 처리
	 * // List로 자른다
	 * List<String> queries = Arrays.stream(query.split("\\s+"))
	 * .filter(str -> !str.contains(";") || !(str.length() < 2 && str.charAt(0) <
	 * 127)).toList();
	 * // ;가 포함된 검색어 걸러내기
	 * // 영어 / 숫자 1글자로 된 검색어 걸러내기
	 * 
	 * // System.out.println("queries: " + queries.toString());
	 * 
	 * if (queries.size() > 0) {
	 * 
	 * Predicate[] searchPredicates = new Predicate[queries.size() * 4];
	 * 
	 * for (int i = 0; i < queries.size(); i++) {
	 * String str = queries.get(i);
	 * searchPredicates[i * 4] = q.lectureName.like("%" + str + "%");
	 * searchPredicates[i * 4 + 1] = q.member.name.like("%" + str + "%");
	 * searchPredicates[i * 4 + 2] = q.lectureContent.like("%" + str + "%");
	 * searchPredicates[i * 4 + 3] = q.facilityName.like("%" + str + "%");
	 * }
	 * 
	 * bb.andAnyOf(searchPredicates);
	 * 
	 * }
	 * }
	 * 
	 * if (startTimeRange != null) {
	 * bb.and(q.lectureStartTime.after(startTimeRange));
	 * }
	 * if (endTimeRange != null) {
	 * bb.and(q.lectureStartTime.before(endTimeRange));
	 * }
	 * 
	 * // Page<Lecture> searchResult = lectureRepository.findAll(bb, p);
	 * Page<Object[]> searchResult = lRepo.searchLectureList2(bb, p, userX, userY,
	 * factorX);
	 * List<Object[]> list = searchResult.getContent();
	 * 
	 * for (int i = 0; i < size; i++) {
	 * System.out.println(Arrays.toString(list.get(i)));
	 * }
	 * 
	 * List<Lecture> list2 = lRepo.findAll(bb, p).getContent();
	 * for (int i = 0; i < list2.size(); i++) {
	 * System.out.println(list2.get(i));
	 * }
	 * 
	 * Pageable p2 = new PageRequestDTO(1, size).getPageable(
	 * "lecture_start_time",
	 * Sort.Direction.ASC);
	 * 
	 * List<Object[]> list3 = lRepo.searchLectureList3(p2, userX, userY,
	 * factorX).getContent();
	 * for (int i = 0; i < list3.size(); i++) {
	 * System.out.println(Arrays.toString(list3.get(i)));
	 * }
	 * }
	 */

	/*
	 * void searchTest4() {
	 * 
	 * QLecture q = QLecture.lecture;
	 * 
	 * Double userX = 126.99;
	 * Double userY = 37.56;
	 * Double distX = 111.054 * Math.cos(userX * Math.PI / 180);
	 * 
	 * List<Long> sports = new ArrayList<>();
	 * 
	 * String query = "농구";
	 * 
	 * LocalDateTime startTimeRange = null;
	 * LocalDateTime endTimeRange = null;
	 * 
	 * String sort = "lectureStartTime";
	 * // String sort = "dist";
	 * 
	 * int size = 10;
	 * 
	 * Pageable p = new PageRequestDTO(1, size)
	 * .getPageable(sort, Sort.Direction.ASC);
	 * 
	 * CriteriaBuilder cb = em.getCriteriaBuilder();
	 * CriteriaQuery<Lecture> searchQuery = cb.createQuery(Lecture.class);
	 * Root<Lecture> root = searchQuery.from(Lecture.class);
	 * 
	 * Specification<Lecture> spec = Specification.unrestricted();
	 * 
	 * // 위도/경도 조건 만족
	 * if (userX != null && userY != null) {
	 * spec.and(distanceWithin(userX, userY, 50));
	 * }
	 * 
	 * if (query != null && query.length() > 0) {
	 * // 검색어 처리
	 * // List로 자른다
	 * List<String> queries = Arrays.stream(query.split("\\s+"))
	 * .filter(str -> !str.contains(";") || !(str.length() < 2 && str.charAt(0) <
	 * 127)).toList();
	 * // ;가 포함된 검색어 걸러내기
	 * // 영어 / 숫자 1글자로 된 검색어 걸러내기
	 * 
	 * // System.out.println("queries: " + queries.toString());
	 * 
	 * if (queries.size() > 0) {
	 * spec.and(searchString(queries));
	 * }
	 * }
	 * 
	 * List<Lecture> list = lRepo.findAll(spec, p).getContent();
	 * 
	 * list.forEach(l -> System.out.println(l));
	 * 
	 * // 통합검색 이렇게 쉽게 될리가 없었다.
	 * // List<Object[]> list2 = lRepo.searchLectureList4(spec, p, userX, userY,
	 * // distX).getContent();
	 * // list2.forEach(l -> System.out.println(Arrays.toString(l)));
	 * 
	 * // CriteriaQuery<Lecture> searchQuery2 = cb.createQuery(Lecture.class);
	 * 
	 * // Root<Lecture> lectRoot = searchQuery2.from(Lecture.class);
	 * // Root<LectureImg> imgRoot = searchQuery2.from(LectureImg.class);
	 * 
	 * // searchQuery2.select(lectRoot)
	 * // .where(cb.equal(imgRoot.get("lecture").get("lectureId"),
	 * // lectRoot.get("lectureId")));
	 * 
	 * }
	 * 
	 * Specification<Lecture> distanceWithin(double userX, double userY, int km) {
	 * double kmToDeg = 0.008983082;
	 * double yFactor = kmToDeg * km; // (range)km는 위도 몇도?
	 * double xFactor = yFactor / Math.cos(userY * Math.PI / 180); // (range)km는 경도
	 * 몇도?
	 * 
	 * return (root, query, cb) -> {
	 * Predicate xDist = cb.between(root.get("coordsX"), userX - xFactor, userX +
	 * xFactor);
	 * Predicate yDist = cb.between(root.get("coordsY"), userY - yFactor, userY +
	 * yFactor);
	 * return cb.and(xDist, yDist);
	 * };
	 * }
	 * 
	 * Specification<Lecture> isSport(List<Long> sportsList) {
	 * return (root, query, cb) -> {
	 * Predicate[] list = new Predicate[sportsList.size()];
	 * for (int i = 0; i < list.length; i++) {
	 * list[i] = cb.equal(root.get("sportName").get("sportId"), sportsList.get(i));
	 * }
	 * return cb.or(list);
	 * };
	 * }
	 * 
	 * Specification<Lecture> searchString(List<String> searchTerms) {
	 * int len = searchTerms.size();
	 * return (root, query, cb) -> {
	 * Predicate[] temp = new Predicate[4];
	 * Predicate[] list = new Predicate[len];
	 * for (int i = 0; i < len; i++) {
	 * String str = searchTerms.get(i);
	 * temp[0] = cb.like(root.get("lectureName"), "%" + str + "%");
	 * temp[1] = cb.like(root.get("lectureContent"), "%" + str + "%");
	 * temp[2] = cb.like(root.get("member").get("username"), "%" + str + "%");
	 * temp[3] = cb.like(root.get("facilityName"), "%" + str + "%");
	 * list[i] = cb.or(temp); // 각각의 검색어는 4개의 필드 중 어디에 있어도 된다
	 * }
	 * return cb.and(list); // 그러나 각각의 검색어를 모두 포함해야 한다
	 * };
	 * }
	 */

	@Test
	void criteriaTest() {
		Double userX = 126.99;
		Double userY = 37.56;
		Double distX = 111.054 * Math.cos(userX * Math.PI / 180);

		List<Long> sports = new ArrayList<>();

		String query = "농구";

		LocalDateTime startTimeRange = LocalDateTime.of(2025, 11, 4, 0, 0);
		LocalDateTime endTimeRange = LocalDateTime.of(2025, 11, 22, 0, 0);

		String sort = "lectureStartTime";
		// String sort = "dist";

		int page = 1;
		int size = 10;

		// Pageable p = new PageRequestDTO(page, size)
		// .getPageable(sort, Sort.Direction.ASC);

		CriteriaBuilder cr = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> search = cr.createQuery(Object[].class);
		Root<Lecture> rootLect = search.from(Lecture.class);
		Join<Lecture, LectureImg> joinImg = rootLect.join("lectureImg", JoinType.LEFT);
		joinImg.on(cr.equal(joinImg.get("isMainImg"), Boolean.TRUE));

		List<Predicate> predicates = new ArrayList<>();

		Expression<Double> dist = cr.sqrt(cr.sum(
				cr.prod(cr.prod(cr.sum(rootLect.get("coordsX"), -userX), distX),
						cr.prod(cr.sum(rootLect.get("coordsX"), -userX), distX)),
				cr.prod(cr.prod(cr.sum(rootLect.get("coordsY"), -userY), 111.054),
						cr.prod(cr.sum(rootLect.get("coordsY"), -userY), 111.054))));

		if (userX != null && userY != null) {
			int range = 50;
			double kmToDeg = 0.008983082;
			double yFactor = kmToDeg * range; // (range)km는 위도 몇도?
			double xFactor = yFactor / Math.cos(userY * Math.PI / 180); // (range)km는 경도 몇도?
			Predicate xDist = cr.between(rootLect.get("coordsX"), userX - xFactor, userX + xFactor);
			Predicate yDist = cr.between(rootLect.get("coordsY"), userY - yFactor, userY + yFactor);
			predicates.add(cr.and(xDist, yDist));
		}

		if (!sports.isEmpty()) {
			predicates.add(rootLect.get("sportName").get("sportId").in(sports));
		}

		if (query != null && query.length() > 0) {
			// 검색어 처리
			// List로 자른다
			List<String> queries = Arrays.stream(query.split("\\s+"))
					.filter(str -> !str.contains(";") || !(str.length() < 2 && str.charAt(0) < 127)).toList();
			// ;가 포함된 검색어 걸러내기
			// 영어 / 숫자 1글자로 된 검색어 걸러내기

			int len = queries.size();
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
		}

		Order sortArr[] = new Order[2];
		if (sort.equals("dist")) {
			sortArr[0] = cr.asc(dist);
			sortArr[1] = cr.asc(rootLect.get("lectureStartTime"));
		} else {
			sortArr[0] = cr.asc(rootLect.get("lectureStartTime"));
			sortArr[1] = cr.asc(dist);
		}

		predicates.add(cr.greaterThanOrEqualTo(rootLect.get("lectureStartTime"), startTimeRange));
		predicates.add(cr.lessThanOrEqualTo(rootLect.get("lectureStartTime"), endTimeRange));

		search.multiselect(rootLect, joinImg, dist).where(predicates.toArray(new Predicate[0])).orderBy(sortArr);
		List<Object[]> list = em.createQuery(search).setFirstResult(page).setMaxResults(size).getResultList();
		list.forEach(arr -> {
			System.out.println(arr[0]);
		});
	}

}
