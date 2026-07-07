package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.LectureReviewDTO;
import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.LectureReview;
import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.repository.LectureRepository;
import com.coachlink.portfolio.repository.LectureReviewRepository;
import com.coachlink.portfolio.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.coachlink.portfolio.dto.LectureReviewImgDTO;
import com.coachlink.portfolio.entity.LectureReviewImg;
import com.coachlink.portfolio.service.LectureReviewImgService;
import com.coachlink.portfolio.repository.LectureReviewImgRepository;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class LectureReviewServiceImpl implements LectureReviewService {

    @Autowired
    private LectureReviewRepository lectureReviewRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureReviewImgService lectureReviewImgService;
    
    @Autowired
    private LectureReviewImgRepository lectureReviewImgRepository;

    // 리뷰 등록
    @Override
    public LectureReview writeReview(String username, Long lectureId, LectureReview reviewData, List<MultipartFile> files) {

        Member member = null;
        if (userRepository.findById(username).isPresent()) {
            member = userRepository.findById(username).get();
        } else {
            System.out.println(" 회원을 찾을 수 없습니다.");
            return null;
        }

        Lecture lecture = null;
        if (lectureRepository.findById(lectureId).isPresent()) {
            lecture = lectureRepository.findById(lectureId).get();
        } else {
            System.out.println(" 강좌를 찾을 수 없습니다.");
            return null;
        }

        LectureReview review = LectureReview.builder()
                .member(member)
                .lecture(lecture)
                .rating(reviewData.getRating())
                .content(reviewData.getContent())
                .build();

        LectureReview savedReview = lectureReviewRepository.save(review);
        
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                    String originalName = file.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String imgName = uuid + "_" + originalName;
                    
                    LectureReviewImg img = LectureReviewImg.builder()
                            .lectureReview(savedReview)
                            .imgName(imgName)
                            .originalName(originalName)
                            .contentType(file.getContentType())
                            .size(file.getSize())
                            .base64(base64)
                            .createdAt(LocalDateTime.now())
                            .build();
                            
                    lectureReviewImgService.registerReviewImg(savedReview.getReviewId(), img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("리뷰 등록 완료");
        return savedReview;
    }
    // 특정 강좌의 리뷰 조회
    @Override
    public List<LectureReview> getReviewsByLecture(Long lectureId) {

        Lecture lecture = null;
        if (lectureRepository.findById(lectureId).isPresent()) {
            lecture = lectureRepository.findById(lectureId).get();
        } else {
            System.out.println("강좌를 찾을 수 없습니다.");
            return null;
        }

        List<LectureReview> reviewList = lectureReviewRepository.findByLectureId(lectureId);
        System.out.println("리뷰 목록 불러오기 완료 (" + reviewList.size() + "개)");
        return reviewList;
    }

    // 리뷰 상세 조회
    @Override
    public LectureReview getReviewById(Long reviewId) {
        if (lectureReviewRepository.findById(reviewId).isPresent()) {
            return lectureReviewRepository.findById(reviewId).get();
        } else {
            System.out.println("리뷰를 찾을 수 없습니다.");
            return null;
        }
    }
    // 리뷰 수정
    @Override
    public LectureReview updateReview(Long reviewId, LectureReview updatedData, List<MultipartFile> files, List<Long> deletedImgIds) {

        LectureReview existingReview = null;
        if (lectureReviewRepository.findById(reviewId).isPresent()) {
            existingReview = lectureReviewRepository.findById(reviewId).get();
        } else {
            System.out.println("수정할 리뷰를 찾을 수 없습니다.");
            return null;
        }

        LectureReview updatedReview = LectureReview.builder()
                .reviewId(existingReview.getReviewId())
                .member(existingReview.getMember())
                .lecture(existingReview.getLecture())
                .rating(updatedData.getRating())
                .content(updatedData.getContent())
                .build();

        LectureReview savedReview = lectureReviewRepository.save(updatedReview);

        // 1. 삭제할 이미지 처리
        if (deletedImgIds != null && !deletedImgIds.isEmpty()) {
            for (Long imgId : deletedImgIds) {
                lectureReviewImgService.deleteReviewImg(imgId);
            }
        }

        // 2. 추가할 이미지 처리
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                    String originalName = file.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String imgName = uuid + "_" + originalName;

                    LectureReviewImg img = LectureReviewImg.builder()
                            .lectureReview(savedReview)
                            .imgName(imgName)
                            .originalName(originalName)
                            .contentType(file.getContentType())
                            .size(file.getSize())
                            .base64(base64)
                            .createdAt(LocalDateTime.now())
                            .build();

                    lectureReviewImgService.registerReviewImg(savedReview.getReviewId(), img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("리뷰 수정 완료");
        return savedReview;
    }

    //리뷰 삭제
    @Override
    public boolean deleteReview(Long reviewId) {

        if (lectureReviewRepository.findById(reviewId).isPresent()) {
            // 1. 연관된 이미지 삭제
            List<LectureReviewImg> imgs = lectureReviewImgRepository.findByReviewId(reviewId);
            if (imgs != null && !imgs.isEmpty()) {
                lectureReviewImgRepository.deleteAll(imgs);
            }

            // 2. 리뷰 삭제
            lectureReviewRepository.deleteById(reviewId);
            System.out.println("리뷰 삭제 완료");
            return true;
        } else {
            System.out.println("삭제할 리뷰를 찾을 수 없습니다.");
            return false;
        }
    }
    public LectureReviewDTO toDTO(LectureReview r) {
        List<LectureReviewImg> imgs = lectureReviewImgRepository.findByReviewId(r.getReviewId());
        List<LectureReviewImgDTO> imgDTOs = new ArrayList<>();
        
        if (imgs != null) {
            imgDTOs = imgs.stream().map(img -> LectureReviewImgDTO.builder()
                    .lectureReviewImgId(img.getLectureReviewImgId())
                    .reviewId(img.getLectureReview().getReviewId())
                    .imgName(img.getImgName())
                    .originalName(img.getOriginalName())
                    .contentType(img.getContentType())
                    .size(img.getSize())
                    .base64(img.getBase64())
                    .createdAt(img.getCreatedAt())
                    .build()).toList();
        }

        return LectureReviewDTO.builder()
                .reviewId(r.getReviewId())
                .username(r.getMember().getUsername())
                .lectureId(r.getLecture().getLectureId())
                .rating(r.getRating())
                .content(r.getContent())
                .reviewStatus(r.getReviewStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .reviewImages(imgDTOs)
                .build();
    }

}
