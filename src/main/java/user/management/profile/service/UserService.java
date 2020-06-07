package user.management.profile.service;

import java.security.Principal;

import org.springframework.web.multipart.MultipartFile;

import user.management.profile.model.User;

public interface UserService {

	User registerUser(String userJson, MultipartFile image);
	User updateUser(Long id, String userJson, MultipartFile image, Principal principal);
	User getUserDetails(Long id, Principal principal);

	User findByUsername(String username);
	User login(Principal principal);

}
