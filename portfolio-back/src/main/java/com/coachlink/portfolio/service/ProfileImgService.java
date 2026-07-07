package com.coachlink.portfolio.service;

import com.coachlink.portfolio.entity.ProfileImg;

public interface ProfileImgService {

	ProfileImg registerProfileImg(String username, ProfileImg imgData);

	ProfileImg getProfileImg(String username);

	ProfileImg updateProfileImg(String username, ProfileImg updatedData);

	boolean deleteProfileImg(String username);

    ProfileImg registerOrUpdate(String username, String fileName, String base64);
}
