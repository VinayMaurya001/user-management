package user.management.profile.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${profile.image.location}")
	String profileImageLocation;

	public String storeFile(MultipartFile file, Long userId) {
		String fileName = "";
		try {
			Path fileStorageLocation = Paths.get(profileImageLocation).toAbsolutePath().normalize();
			Files.createDirectories(fileStorageLocation);
			String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
			logger.info("Directory where the uploaded file will be stored. {}", originalFileName);
			String fileExtension = "";
			int index = originalFileName.lastIndexOf(".");
			fileExtension = index > 0 ? originalFileName.substring(index) : "";
			fileName = userId + "_" + System.currentTimeMillis() + fileExtension;
			Path targetLocation = fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception ex) {
			logger.error("Could not store file " + fileName + ". Please try again!", ex);
			throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
		}
		return fileName;
	}

	public String getBase64EncodedStringOfFile(String fileName) throws IOException {
		byte[] fileContent = FileUtils.readFileToByteArray(new File(profileImageLocation + fileName));
		return Base64.getEncoder().encodeToString(fileContent);
	}

}
