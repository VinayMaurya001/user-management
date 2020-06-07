package user.management.profile.controller;

import java.security.Principal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import user.management.profile.model.User;
import user.management.profile.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/user/registration")
	public ResponseEntity<?> register(@RequestParam(required = true) String userJson,
			@RequestParam(required = false) MultipartFile image) {
		User user = userService.registerUser(userJson, image);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	//@CacheEvict(value = "users", key = "#id")
	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestParam(required = true) String userJson,
			@RequestParam(required = false) MultipartFile image, Principal principal) {
		User user = userService.updateUser(id, userJson, image, principal);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	//@Cacheable(value = "users", key = "#id")
	@GetMapping("/users/{id}")
	public User getUserDetails(@PathVariable @NotNull Long id, Principal principal) {
		User user = userService.getUserDetails(id, principal);
		user.setRequestTimeString(new Date().toGMTString());
		return user;
	}

	@GetMapping("/user/login")
	public ResponseEntity<?> login(Principal principal) {
		User user = userService.login(principal);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PostMapping(user.management.profile.constant.Constants.LOGOUT_API)
	public ResponseEntity<?> logout(Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
