package com.coachlink.portfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "sport_names")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SportName extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sport_id")
	private Long sportId;

	private String mainName;

	private String subName;

	public void updateMainName(String newMainName) {
		this.mainName = newMainName;
	}

	public void updateSubName(String newSubName) {
		this.subName = newSubName;
	}

}
