package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.entity.ProfileImg;
import com.coachlink.portfolio.service.ProfileImgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("/profile-img")
public class ProfileImgController {

    @Autowired
    private ProfileImgService profileImgService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadProfileImg(
            @RequestParam("username") String username,
            @RequestParam("file")MultipartFile file){

        try{
            // Base64 변환
            String base64Content = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            String base64String = "data:" + file.getContentType() + ";base64," + base64Content;
            
            String fileName = username + "_" + file.getOriginalFilename();

            // DB 저장 (파일 시스템 저장 로직 제거)
            ProfileImg savedImg = profileImgService.registerOrUpdate(username, fileName, base64String);

            if (savedImg == null){
                return new ResponseEntity<>("DB 저장 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return  new ResponseEntity<>("업로드 성공", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("업로드 실패: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    //등록
    @PostMapping("/{username}")
    public ResponseEntity<ProfileImg> registerProfileImg(
            @PathVariable String username,
            @RequestBody ProfileImg imgData) {

        ProfileImg savedImg = profileImgService.registerProfileImg(username, imgData);

        if (savedImg == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(savedImg, HttpStatus.CREATED);
    }


    //조회

    @GetMapping("/{username}")
    public ResponseEntity<ProfileImg> getProfileImg(@PathVariable String username) {

        ProfileImg profileImg = profileImgService.getProfileImg(username);

        if (profileImg == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(profileImg, HttpStatus.OK);
    }


    // 수정
    @PatchMapping("/{username}")
    public ResponseEntity<ProfileImg> updateProfileImg(
            @PathVariable String username,
            @RequestBody ProfileImg updatedData) {

        ProfileImg updatedImg = profileImgService.updateProfileImg(username, updatedData);

        if (updatedImg == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedImg, HttpStatus.OK);
    }
    //삭제
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteProfileImg(@PathVariable String username) {

        boolean deleted = profileImgService.deleteProfileImg(username);

        if (!deleted) {
            return new ResponseEntity<>("삭제할 이미지를 찾을 수 없습니다", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("프로필 이미지 삭제 완료", HttpStatus.OK);
    }
}
