package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.LectureImg;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class LectureImgRepositoryTest {
    @Autowired
    LectureImgRepository lectureImgRepository;

    @Test
    public void updateMainImg() {
        Optional<LectureImg> result = lectureImgRepository.findById(81L);

        if (result.isPresent()) {
            // 81번 LectureImg 객체 가져오기
            LectureImg tmpLectureImg = result.get();
            tmpLectureImg.updateIsMainImg(true);

            // 같은 lectureId를 가진 mainImg가 true인 LectureImg이 있는지 확인
            Long lectureId = tmpLectureImg.getLecture().getLectureId(); // lectureId 추출
            Optional<LectureImg> existingMainImg = lectureImgRepository.findByLectureIdAndIsMainImgTrue(lectureId);

            // 만약 기존에 mainImg가 true인 이미지가 있다면, 그 값을 false로 변경
            existingMainImg.ifPresent(img -> {
                img.updateIsMainImg(false); // 기존 이미지의 mainImg를 false로 변경
                lectureImgRepository.save(img); // 변경된 이미지 저장
            });

            // 81번 LectureImg의 mainImg를 true로 설정
            tmpLectureImg.updateIsMainImg(true);
            lectureImgRepository.save(tmpLectureImg); // 변경된 81번 LectureImg 저장
        }
    }
}
