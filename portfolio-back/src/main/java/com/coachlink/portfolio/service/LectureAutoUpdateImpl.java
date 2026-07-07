package com.coachlink.portfolio.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.repository.LectureRepository;
import com.coachlink.portfolio.util.LectureStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureAutoUpdateImpl {

	private final LectureRepository lecRepo;

	// 매 1분마다
	// 10분마다라면, 0 */10 * * * *
	@Scheduled(cron = "0 * * * * *") // 이 문장을 uncomment할것
	@Transactional
	public void updateLectures() {

		// System.out.println(LocalDateTime.now());

		List<Object[]> listOld = lecRepo.getUpdatedLectures();

		List<Lecture> listNew = new ArrayList<>();
		int len = listOld.size();

		// System.out.println("listOld.size() = " + len);

		for (int i = 0; i < len; i++) {
			Lecture lec = (Lecture) listOld.get(i)[0];
			long enrollCnt = (Long) listOld.get(i)[1];
			LectureStatus status = null;
			if (LocalDateTime.now().isAfter(lec.getLectureEndTime())) {
				status = LectureStatus.END; // ENDED
				lec.setLectureStatus(status);
				listNew.add(lec);
			} else if (LocalDateTime.now().isAfter(lec.getLectureStartTime().minusDays(3))) {
				if (enrollCnt >= lec.getMinPeople()) {
					status = LectureStatus.ACT; // ACTIVE
				} else {
					status = LectureStatus.CAN; // CANCELLED
				}
				lec.setLectureStatus(status);
				listNew.add(lec);
			}
		}

		// listNew.forEach(
		// l -> System.out.println(l.getLectureId() + " " + l.getLectureName() + " " +
		// l.getLectureStatus()));
		lecRepo.saveAll(listNew);
	}
}
