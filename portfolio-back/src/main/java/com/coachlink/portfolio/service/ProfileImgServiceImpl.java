package com.coachlink.portfolio.service;

import com.coachlink.portfolio.entity.ProfileImg;
import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.repository.ProfileImgRepository;
import com.coachlink.portfolio.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileImgServiceImpl implements ProfileImgService {

    @Autowired
    private ProfileImgRepository profileImgRepository;

    @Autowired
    private UserRepository userRepository;

    // 프로필 이미지 등록
    @Override
    public ProfileImg registerProfileImg(String username, ProfileImg imgData) {

        Member member = userRepository.findById(username).orElse(null);
        if (member == null) {
            System.out.println("해당 사용자를 찾을 수 없습니다: " + username);
            return null;
        }

        if (profileImgRepository.existsByUsername(username)) {
            System.out.println("이미 프로필 이미지가 존재합니다");
            return null;
        }

        ProfileImg profileImg = ProfileImg.builder()
                .member(member)
                .imgName(imgData.getImgName())
                .originalName(imgData.getOriginalName())
                .contentType(imgData.getContentType())
                .size(imgData.getSize())
                .base64(imgData.getBase64())
                .thumbnailName(imgData.getThumbnailName())
                .createdAt(LocalDate.now())
                .build();

        profileImgRepository.save(profileImg);
        System.out.println("프로필 이미지 등록 완료");
        return profileImg;
    }

    // 프로필 이미지 조회
    @Override
    public ProfileImg getProfileImg(String username) {

        ProfileImg profileImg = profileImgRepository.findByUsername(username);
        if (profileImg == null) {
            System.out.println("등록된 프로필 이미지를 찾을 수 없습니다: " + username);
            return null;
        }

        System.out.println("프로필 이미지 조회 완료");
        return profileImg;
    }

    // 프로필 이미지 수정
    @Override
    public ProfileImg updateProfileImg(String username, ProfileImg updatedData) {

        ProfileImg img = profileImgRepository.findByUsername(username);
        if (img == null) {
            System.out.println("수정할 프로필 이미지를 찾을 수 없습니다");
            return null;
        }

        if (updatedData.getImgName() != null) img.setImgName(updatedData.getImgName());
        if (updatedData.getOriginalName() != null) img.setOriginalName(updatedData.getOriginalName());
        if (updatedData.getContentType() != null) img.setContentType(updatedData.getContentType());
        if (updatedData.getSize() != null) img.setSize(updatedData.getSize());
        if (updatedData.getBase64() != null) img.setBase64(updatedData.getBase64());
        if (updatedData.getThumbnailName() != null) img.setThumbnailName(updatedData.getThumbnailName());

        profileImgRepository.save(img);
        System.out.println("프로필 이미지 수정 완료");
        return img;
    }

    // 프로필 이미지 삭제
    @Override
    public boolean deleteProfileImg(String username) {

        ProfileImg profileImg = profileImgRepository.findByUsername(username);
        if (profileImg == null) {
            System.out.println("삭제할 프로필 이미지를 찾을 수 없습니다");
            return false;
        }

        profileImgRepository.delete(profileImg);
        System.out.println("프로필 이미지 삭제 완료");
        return true;
    }
    
    // username으로 프로필 이미지 새로 등록 or 수정
    @Override
    public ProfileImg registerOrUpdate(String username, String fileName, String base64) {

        // 1) 회원 조회
        Member member = userRepository.findById(username).orElse(null);
        if (member == null) {
            System.out.println("존재하지 않는 사용자입니다: " + username);
            return null;
        }

        // 2) 기존 프로필 이미지 조회
        ProfileImg profileImg = profileImgRepository.findByUsername(username);

        // 3) 없으면 새로 등록
        if (profileImg == null) {
            profileImg = ProfileImg.builder()
                    .member(member)
                    .imgName(fileName)
                    .base64(base64)
                    .createdAt(LocalDate.now())
                    .build();
            profileImgRepository.save(profileImg);
            System.out.println("프로필 이미지 새로 등록");
            return profileImg;
        }

        // 4) 있으면 파일명과 Base64 수정
        profileImg.setImgName(fileName);
        profileImg.setBase64(base64);
        profileImgRepository.save(profileImg);

        System.out.println("프로필 이미지 업데이트 완료");
        return profileImg;
    }

}
