package com.coachlink.portfolio.repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.entity.SportName;
import com.coachlink.portfolio.repository.LectureRepository;
import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.repository.SportNameRepository;
import com.coachlink.portfolio.util.Gender;
import com.coachlink.portfolio.util.Role;

import jakarta.transaction.Transactional;

@SpringBootTest
public class DummyData_1 {

	@Autowired
	SportNameRepository snRepo;

	@Test
	@Transactional
	@Commit
	public void insertSportNames() {
		try (BufferedReader br = new BufferedReader(new FileReader("./data_files/sport_names_202512031133.csv"))) {
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				String[] line1 = line.split(",");
				snRepo.save(SportName.builder().mainName(line1[3]).subName(line1[4]).build());
			}

		} catch (IOException e) {
			e.printStackTrace();
			assert false;
		}
	}

	@Autowired
	UserRepository mRepo;

	@Test
	@Transactional
	@Commit
	public void insertUsers() {
		String[] familyName = new String[20];
		String[] givenName = new String[200];
		String[] addresses = new String[300];

		try (BufferedReader br = new BufferedReader(new FileReader("./data_files/given_names.csv"))) {
			int i = 0;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				String[] names = line.replace("\"", "").replace(" ", "").split(",");
				for (int j = 0; j < names.length; j++) {
					givenName[i] = names[j];
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}

		try (BufferedReader br = new BufferedReader(new FileReader("./data_files/family_names.csv"))) {
			familyName = br.readLine().replace("\"", "").replace(" ", "").split(",");
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}

		try (BufferedReader br = new BufferedReader(new FileReader("./data_files/addresses.csv"))) {
			int i = 0;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				addresses[i] = line.replace("\"", "");
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}

		for (int i = 0; i < 300; i++) {
			int gender_num = (int) (Math.random() * 2); // 0 = 남성형, 1 = 여성형
			String name = familyName[(int) (Math.random() * 20)]
					+ givenName[(int) (Math.random() * 100) + gender_num * 100];
			Gender gender = (gender_num == 0 ? Gender.M : Gender.F);

			Role role = (Math.random() > 0.8 ? Role.PLAYER : Role.MEMBER);
			Set<Role> roleSet = new HashSet<>();
			roleSet.add(Role.MEMBER);
			roleSet.add(role);

			LocalDateTime dateOfBirth = LocalDateTime.now().minusSeconds(473040000L)
					.minusSeconds((long) (Math.random() * 473040000L));

			Member member = Member.builder().name(name).gender(gender).dateOfBirth(dateOfBirth).address(
					addresses[i])
					.roleSet(roleSet)
					.username(String.format("test%03d", i)).userPwd("1234")
					.build();

			System.out.println(member.toString());

			mRepo.save(member);
		}
	}

	@Autowired
	LectureRepository lRepo;

	@Autowired
	SportNameRepository srRepo;

	@Test
	@Transactional
	@Commit
	public void insertLectures() {
		List<Member> listPlayers = mRepo.findAll();

		listPlayers.removeIf(member -> !member.getRoleSet().contains(Role.PLAYER));

		int len = listPlayers.size();

		JSONArray obj = null;

		try (BufferedReader br = new BufferedReader(new FileReader("./data_files/facility.txt"))) {

			obj = new JSONObject(br.readLine())
					.getJSONObject("response")
					.getJSONObject("body")
					.getJSONObject("items")
					.getJSONArray("item");

		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}

		try {
			for (int i = 0; i < len; i++) {

				SportName sn = srRepo.findById((long) (Math.random() * 51) + 1).get();

				JSONObject facility = (JSONObject) obj.get((int) (Math.random() * 1000));

				String facilityName = facility.getString("faci_nm");
				Double xCoord = facility.getDouble("faci_lot");
				Double yCoord = facility.getDouble("faci_lat");

				lRepo.save(Lecture.builder()
						.maxPeople((int) (Math.random() * 10) + 10L).minPeople((long) (Math.random()
								* 10))
						.lectureName("test" + i).member(listPlayers.get(i)).sportName(sn)
						.lectureStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
								.plusHours(8 + (int) (Math.random() * 4)).plusDays((int) (Math.random() *
										30)))
						.lectureEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
								.plusHours(8 + (int) (Math.random() * 4)).plusDays((int) (Math.random() * 30)
										+ 40))
						.coordsX(xCoord).coordsY(yCoord).facilityName(facilityName)
						.build());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
