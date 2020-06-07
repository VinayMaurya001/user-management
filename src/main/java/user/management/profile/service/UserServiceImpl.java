package user.management.profile.service;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import user.management.profile.jwt.JwtTokenProvider;
import user.management.profile.model.ProfileImage;
import user.management.profile.model.Role;
import user.management.profile.model.User;
import user.management.profile.repository.UserRepository;
import user.management.profile.util.FileUploadUtil;

@Service
//@Transactional
public class UserServiceImpl implements UserService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FileUploadUtil fileUploadUtil;

	@Autowired
	private ProfileImageService profileImageService;

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	@Override
	@Transactional
	public User registerUser(String userJson, MultipartFile image) {
		User user = null;
		try {
			user = new ObjectMapper().readValue(userJson, User.class);
			logger.info("Registering user with username {}.", user.getUsername());
			if (userRepository.findByUsername(user.getUsername()).isPresent()) {
				throw new RuntimeException("Username Already exist!");
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setRole(Role.USER);
			user = userRepository.save(user);
			if (image != null && user != null && user.getId() != null) {
				String imageName = fileUploadUtil.storeFile(image, user.getId());
				logger.info("Image stored: {}", imageName);
				if (imageName != null) {
					ProfileImage profileImage = new ProfileImage();
					profileImage.setUserId(user.getId());
					profileImage.setImageName(imageName);
					profileImage.setDeleted(false);
					profileImageService.saveProfileImage(profileImage);
				}
			}
			logger.info("User registered successfully with username {}.", user.getUsername());
		} catch (Exception e) {
			logger.error("Exception occured!", e);
			throw new RuntimeException(e.getMessage(), e);
		}
		return user;
	}

	@Override
	@Transactional
	public User updateUser(Long id, String userJson, MultipartFile image, Principal principal) {
		User dbUser = null;
		try {
			User user = new ObjectMapper().readValue(userJson, User.class);
			logger.info("Updating user with username {}.", user.getUsername());

			dbUser = userRepository.findById(id).get();
			if (dbUser == null || principal == null) {
				throw new RuntimeException("Invalid request!");
			}

			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
			String loggedUserName = usernamePasswordAuthenticationToken.getName();
			if (!dbUser.getUsername().equalsIgnoreCase(loggedUserName)) {
				throw new RuntimeException("Logged user and user details mismatch occured.");
			}

			if (user.getUsername() != null)
				dbUser.setUsername(user.getUsername());
			if (user.getPassword() != null)
				dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
			if (user.getName() != null)
				dbUser.setName(user.getName());
			if (user.getMobile() != null)
				dbUser.setMobile(user.getMobile());
			if (user.getAddress() != null)
				dbUser.setAddress(user.getAddress());
			if (user.getDob() != null)
				dbUser.setDob(user.getDob());
			dbUser = userRepository.save(dbUser);

			if (image != null && dbUser != null && dbUser.getId() != null) {
				String imageName = fileUploadUtil.storeFile(image, dbUser.getId());
				if (imageName != null) {
					ProfileImage profileImage = new ProfileImage();
					profileImage.setUserId(dbUser.getId());
					profileImage.setImageName(imageName);
					profileImage.setDeleted(false);
					profileImageService.updateProfileImage(profileImage);
				}
			}
			logger.info("User updated successfully with username {}.", dbUser.getUsername());
		} catch (Exception e) {
			logger.error("Exception occured!", e);
			throw new RuntimeException(e.getMessage(), e);
		}
		return dbUser;
	}

	@Override
	public User getUserDetails(Long id, Principal principal) {

		User user = null;
		try {
			user = userRepository.findById(id).get();
			String loggedUserName = null;
			if (principal != null) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
				loggedUserName = usernamePasswordAuthenticationToken.getName();
			}
			if (user == null || !loggedUserName.equalsIgnoreCase(user.getUsername())) {
				throw new RuntimeException("Logged user and user details mismatch occured.");
			}
			user = profileImageService.addImageDetails(user);

		} catch (Exception e) {
			logger.error("Exception occured!", e);
			throw new RuntimeException(e.getMessage(), e);
		}
		logger.info("User details fetched. {}", id);
		return user;
	}

	@Override
	public User login(Principal principal) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
		User user = userRepository.findByUsername(usernamePasswordAuthenticationToken.getName()).get();
		user.setToken(jwtTokenProvider.generateToken(usernamePasswordAuthenticationToken));
		JwtTokenProvider.loggedTokenList.add(user.getToken());
		logger.info("User logged in successfully: {}", user.getUsername());
		return user;
	}

}
