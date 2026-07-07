package com.coachlink.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "lecture_imgs")
public class LectureImg extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lectureImgId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	@Column(length = 100)
	private String originalName;

	@Column(length = 100)
	private String contentType;

	private Long size;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String base64;

    private boolean isMainImg;

    public void updateIsMainImg(boolean newMainImg) {
        this.isMainImg = newMainImg;
    }
    public void setLecture(Lecture lecture) { this.lecture = lecture; }
}
