package com.coachlink.portfolio.repository;


import com.coachlink.portfolio.entity.LectureReview;
import com.coachlink.portfolio.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class LectureReviewRepositoryTest {

    @Autowired
    private LectureReviewRepository lectureReviewRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void insertReviews() {

        List<Member> usersList = userRepository.findAll();


        usersList.forEach(user -> {

            Long lectureId = (long) ((Math.random() * 5) + 1);

            lectureRepository.findById(lectureId).ifPresent(lecture -> {

                LectureReview review = LectureReview.builder()
                        .lecture(lecture)
                        .member(user)
                        .content(user.getUsername())
                        .rating((int) (Math.random() * 3) + 3) 
                        .build();

                lectureReviewRepository.save(review);
            });
        });
    }
}
