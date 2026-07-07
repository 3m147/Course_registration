package com.coachlink.portfolio.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.coachlink.portfolio.util.LectureStatus;

import jakarta.persistence.*;
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
@ToString(exclude = { "member", "sportName", "lectureImg" })
@Table(name = "lectures")
public class Lecture extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_name")
    private Member member;

    @Column(length = 100, nullable = false)
    private String facilityName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private SportName sportName;

    @Column(length = 100, nullable = false)
    private String lectureName;

    private String lectureContent;

    @Column(nullable = false)
    private LocalDateTime lectureStartTime;

    @Column(nullable = false)
    private LocalDateTime lectureEndTime;

    private Long minPeople;
    private Long maxPeople;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LectureStatus lectureStatus = LectureStatus.OPN;

    @OneToMany(mappedBy = "lecture")
    private List<LectureImg> lectureImg;

    public void updateLectureName(String newLectureName) {
        this.lectureName = newLectureName;
    }

    public void updateLectureContent(String newLectureContent) {
        this.lectureContent = newLectureContent;
    }

    public void updateMinPeople(Long newMinPeople) {
        this.minPeople = newMinPeople;
    }

    public void updateMaxPeople(Long newMaxPeople) {
        this.maxPeople = newMaxPeople;
    }

    public void setSportName(SportName sportName) {
        this.sportName = sportName;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setLectureStatus(LectureStatus status) {
        this.lectureStatus = status;
    }

    private Double coordsX;
    private Double coordsY;

    // sqrt((l.coordsY - :userCoordsY)*(l.coordsY - :userCoordsY)*111.045*111.045 +
    // (l.coordsX - :userCoordsX)*(l.coordsX - :userCoordsX)*:factorX*:factorX)
    // public Double dist(Double userX, Double userY) {
    // Double degToKm = 111.045;
    // Double factorX = degToKm * Math.cos(Math.PI * userX / 180);
    // return Math.sqrt((coordsY - userY) * (coordsY - userY) * degToKm * degToKm
    // + (coordsX - userX) * (coordsX - userX) * factorX * factorX);
    // }

    // @OneToMany(mappedBy = "lectureId")
    // private List<LectureImg> lectureImg;
}
