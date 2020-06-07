package user.management.profile.service;

import java.io.IOException;

import user.management.profile.model.ProfileImage;
import user.management.profile.model.User;

public interface ProfileImageService {

	ProfileImage saveProfileImage(ProfileImage profileImage);

	ProfileImage updateProfileImage(ProfileImage profileImage);

	ProfileImage getProfileImage(Long userId);

	String getProfileImageEncodedString(String imageName) throws IOException;

	User addImageDetails(User user) throws IOException;

}
