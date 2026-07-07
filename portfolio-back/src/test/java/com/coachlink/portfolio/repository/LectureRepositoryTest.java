package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.SportName;
import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class LectureRepositoryTest {
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SportNameRepository sportNameRepository;

	@Test
	public void insertLecture() {
		Member member = userRepository.findByUsername("test000")
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		Set<Role> roles = member.getRoleSet();

		if (roles.contains(Role.MEMBER)) {
			System.out.println("일반회원은 강좌를 개설할 수 없습니다.");
			return;
		}

		if (member.getSportName() == null) {
			System.out.println("주종목 등록 후 강좌 개설이 가능합니다.");
			return;
		}

		Long sportId = member.getSportName().getSportId();
		Optional<SportName> isSportName = sportNameRepository.findById(sportId);

		if (isSportName.isPresent()) {
			SportName sportName = isSportName.get();
			lectureRepository.save(Lecture.builder()
					.facilityName("선납숲공원 농구장")
					.lectureContent("농구수업 진행합니다.")
					.lectureStartTime(LocalDateTime.parse("2025-11-12T16:00:00.000"))
					.lectureEndTime(LocalDateTime.parse("2025-11-12T18:00:00.000"))
					.lectureName("농구수업")
					.maxPeople(5L)
					.minPeople(10L)
					.sportName(sportName)
					.member(member)
					.build());
		} else {
			System.out.println("주종목 정보를 다시 확인해보세요.");
		}
	}

	@Test
	public void selectAllLecture() {
		lectureRepository.findAll(Sort.by("lectureId")).forEach(lecture -> {
			System.out.println(lecture.toString());
		});
	}

	@Test
	public void selectLectureByLectureId() {
		Optional<Lecture> lecture = lectureRepository.findByLectureId(1L);
		System.out.println(lecture.toString());
		System.out.println(lecture.get().getMember().getUsername()); // 강의를 등록한 username
	}

	@Test
	public void updateLectureByLectureId() {
		Optional<Lecture> lecture = lectureRepository.findById(3L);
		if (lecture.isPresent()) {
			Lecture tmpLecture = lecture.get();
			System.out.println(tmpLecture.toString());

			tmpLecture.updateLectureName("초보농구수업");
			tmpLecture.updateLectureContent("초보를 위한 농구수업 진행합니다.");
			tmpLecture.updateMinPeople(3L);
			tmpLecture.updateMaxPeople(8L);

			lectureRepository.save(tmpLecture);
		}
	}

	@Test
	public void deleteLectureByLectureId() {
		lectureRepository.deleteById(2L);
	}

}
