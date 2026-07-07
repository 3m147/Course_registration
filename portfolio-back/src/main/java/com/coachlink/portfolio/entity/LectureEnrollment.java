package com.coachlink.portfolio.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "lecture_enrollments", uniqueConstraints = @UniqueConstraint(columnNames = { "username", "lecture_id" }))
@EntityListeners(value = { AuditingEntityListener.class })
public class LectureEnrollment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lectureEnrollmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "username", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	@CreatedDate // data가 insert된 날짜와 시간을 저장
	@Column(updatable = false)
	private LocalDateTime createdAt;
}