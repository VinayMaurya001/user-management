package user.management.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import user.management.profile.model.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

	ProfileImage findByUserId(long userId);

}
