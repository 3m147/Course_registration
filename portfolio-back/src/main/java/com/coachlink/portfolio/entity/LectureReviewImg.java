package com.coachlink.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "lecture_reviews_imgs")
public class LectureReviewImg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lectureReviewImgId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	private LectureReview lectureReview;

	private String imgName;
	private String originalName;
	private String contentType;
	private Long size;
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String base64;
	private String thumbnailName;
	private LocalDateTime createdAt;
}
