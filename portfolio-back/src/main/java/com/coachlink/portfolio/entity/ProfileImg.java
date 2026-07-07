package com.coachlink.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
@Table(name = "profile_imgs")
public class ProfileImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileImgId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, unique = true)
    private String imgName;
    private String originalName;
    private String contentType;
    private Long size;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String base64;
    private String thumbnailName;

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }
}

