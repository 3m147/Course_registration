package com.coachlink.portfolio.service;

import com.coachlink.portfolio.entity.LectureReview;
import com.coachlink.portfolio.entity.LectureReviewImg;
import com.coachlink.portfolio.repository.LectureReviewImgRepository;
import com.coachlink.portfolio.repository.LectureReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LectureReviewImgServiceImpl implements LectureReviewImgService {

	@Autowired
	private LectureReviewImgRepository lectureReviewImgRepository;

	@Autowired
	private LectureReviewRepository lectureReviewRepository;

	// 이미지 등록
	@Override
	public LectureReviewImg registerReviewImg(Long reviewId, LectureReviewImg imgData) {

		LectureReview review = null;
		if (lectureReviewRepository.findById(reviewId).isPresent()) {
			review = lectureReviewRepository.findById(reviewId).get();
		} else {
			System.out.println("리뷰를 찾을 수 없습니다.");
			return null;
		}

		if (lectureReviewImgRepository.existsByImgName(imgData.getImgName()) > 0) {
			System.out.println("동일한 파일명이 이미 존재합니다: " + imgData.getImgName());
			return null;
		}

		LectureReviewImg img = LectureReviewImg.builder()
				.lectureReview(review)
				.imgName(imgData.getImgName())
				.originalName(imgData.getOriginalName())
				.contentType(imgData.getContentType())
				.size(imgData.getSize())
				.base64(imgData.getBase64())
				.thumbnailName(imgData.getThumbnailName())
				.createdAt(LocalDateTime.now())
				.build();

		lectureReviewImgRepository.save(img);
		System.out.println("리뷰 이미지 등록 완료");
		return img;
	}

	// 특정 리뷰의 이미지 목록 조회
	@Override
	public List<LectureReviewImg> getImgsByReview(Long reviewId) {

		LectureReview review = null;
		if (lectureReviewRepository.findById(reviewId).isPresent()) {
			review = lectureReviewRepository.findById(reviewId).get();
		} else {
			System.out.println("리뷰를 찾을 수 없습니다.");
			return null;
		}

		List<LectureReviewImg> imgList = lectureReviewImgRepository.findByReviewId(reviewId);
		System.out.println("리뷰 이미지 목록 조회 완료 (" + imgList.size() + "개)");
		return imgList;
	}

	// 이미지 이름으로 단일 조회

	@Override
	public LectureReviewImg getImgByName(String imgName) {

		LectureReviewImg img = lectureReviewImgRepository.findByImgName(imgName);
		if (img == null) {
			System.out.println("해당 이름의 이미지를 찾을 수 없습니다: " + imgName);
			return null;
		}

		System.out.println("이미지 조회 완료: " + imgName);
		return img;
	}

	// 이미지 수정
	@Override
	public LectureReviewImg updateReviewImg(Long imgId, LectureReviewImg updatedData) {

		LectureReviewImg existingImg = null;
		if (lectureReviewImgRepository.findById(imgId).isPresent()) {
			existingImg = lectureReviewImgRepository.findById(imgId).get();
		} else {
			System.out.println("수정할 이미지를 찾을 수 없습니다.");
			return null;
		}

		LectureReviewImg updatedImg = LectureReviewImg.builder()
				.lectureReviewImgId(existingImg.getLectureReviewImgId())
				.lectureReview(existingImg.getLectureReview())
				.imgName(updatedData.getImgName() != null ? updatedData.getImgName() : existingImg.getImgName())
				.originalName(updatedData.getOriginalName() != null ? updatedData.getOriginalName()
						: existingImg.getOriginalName())
				.contentType(updatedData.getContentType() != null ? updatedData.getContentType()
						: existingImg.getContentType())
				.size(updatedData.getSize() != null ? updatedData.getSize() : existingImg.getSize())
				.base64(updatedData.getBase64() != null ? updatedData.getBase64() : existingImg.getBase64())
				.thumbnailName(updatedData.getThumbnailName() != null ? updatedData.getThumbnailName()
						: existingImg.getThumbnailName())
				.createdAt(existingImg.getCreatedAt())
				.build();

		lectureReviewImgRepository.save(updatedImg);
		System.out.println("리뷰 이미지 수정 완료");
		return updatedImg;
	}

	// 이미지 삭제

	@Override
	public boolean deleteReviewImg(Long imgId) {

		if (lectureReviewImgRepository.findById(imgId).isPresent()) {
			lectureReviewImgRepository.deleteById(imgId);
			System.out.println("리뷰 이미지 삭제 완료");
			return true;
		} else {
			System.out.println("삭제할 이미지를 찾을 수 없습니다.");
			return false;
		}
	}
}
