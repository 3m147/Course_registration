package com.coachlink.portfolio.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import com.coachlink.portfolio.util.Gender;
import com.coachlink.portfolio.util.Role;
import com.coachlink.portfolio.util.UserStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString(exclude = { "sportName" })
@Table(name = "members")
public class Member extends BaseEntity {

	// DB의 PK로 쓰일 컬럼
	// 로그인용 (Spring Security) 으로 쓰일 컬럼
	@Id
	@Column(length = 50, nullable = false)
	private String username;
	// 회원가입시, username이 userId에 복제되어 들어가야 한다
	// username은 바꿀수 없어야 한다

	// 현실 이름
	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 60)
	private String userPwd;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sport_id")
	private SportName sportName;

	private LocalDateTime dateOfBirth;

	// Y = 활성, N = 비활성
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UserStatus userStatus = UserStatus.Y;

	// 도로명주소
	private String address;

	// 좌표 API 상황에 따라 사용 여부 결정
	// juso.go.kr API에서 사용하는 GRS80 좌표계 사용
	// private Double coordX;
	// private Double coordY;
	// Geolocation으로 처리?

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String email;

	private boolean fromSocial;

	private String provider;

	@Builder.Default
	@ElementCollection(fetch = FetchType.LAZY)
	private Set<Role> roleSet = new HashSet<>();

	public void addMemberRole(Role role) {
		this.roleSet.add(role);
	}


}
