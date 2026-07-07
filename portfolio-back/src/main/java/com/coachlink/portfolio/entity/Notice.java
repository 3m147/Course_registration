package com.coachlink.portfolio.entity;

import com.coachlink.portfolio.util.NoticeStatus;
import com.coachlink.portfolio.util.NoticeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "notices")
public class Notice extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long noticeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "username", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NoticeType noticeType;

	@Column(nullable = false)
	private String noticeContent;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(nullable = false)
	private NoticeStatus noticeStatus = NoticeStatus.NEW;
}
