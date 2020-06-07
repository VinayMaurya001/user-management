package user.management.profile.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.management.profile.model.ProfileImage;
import user.management.profile.model.User;
import user.management.profile.repository.ProfileImageRepository;
import user.management.profile.util.FileUploadUtil;

@Service
// @Transactional
public class ProfileImageServiceImpl implements ProfileImageService {

	@Autowired
	private ProfileImageService profileImageService;

	@Autowired
	private ProfileImageRepository profileImageRepository;

	@Autowired
	private FileUploadUtil fileUploadUtil;

	@Override
	public ProfileImage saveProfileImage(ProfileImage profileImage) {
		return profileImageRepository.save(profileImage);
	}

	@Override
	@Transactional
	public ProfileImage updateProfileImage(ProfileImage profileImage) {
		ProfileImage oldProfileImage = profileImageRepository.findByUserId(profileImage.getUserId());
		if (oldProfileImage != null)
			profileImageRepository.delete(oldProfileImage);
		return profileImageRepository.save(profileImage);
	}

	@Override
	public ProfileImage getProfileImage(Long userId) {
		return profileImageRepository.findByUserId(userId);
	}

	@Override
	public String getProfileImageEncodedString(String imageName) throws IOException {

		return fileUploadUtil.getBase64EncodedStringOfFile(imageName);
	}

	@Override
	public User addImageDetails(User user) throws IOException {
		String imageName = profileImageService.getProfileImage(user.getId()).getImageName();
		if (imageName != null) {
			user.setImageName(imageName);
			user.setImage(profileImageService.getProfileImageEncodedString(imageName));
		}
		return user;
	}

}
