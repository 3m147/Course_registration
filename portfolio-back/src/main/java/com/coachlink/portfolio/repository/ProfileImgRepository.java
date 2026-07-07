package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {

    // 특정 유저의 프로필 이미지 조회
    @Query(value = "select * from profile_imgs where username = :username", nativeQuery = true)
    ProfileImg findByUsername(@Param("username") String username);

    // 이미지 이름으로 조회
    @Query(value = "select * from profile_imgs where img_name = :imgName", nativeQuery = true)
    ProfileImg findByImgName(@Param("imgName") String imgName);

    // 해당 유저가 프로필 이미지를 가지고 있는지 확인
    @Query(value = "select case when count(*) > 0 then true else false end from profile_imgs where username = :username", nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);
}
