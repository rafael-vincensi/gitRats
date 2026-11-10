package com.rafaelvincensi.gitrats.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rafaelvincensi.gitrats.exception.FileUploadException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UploadService {

    private final Cloudinary cloudinary;

    public UploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(byte[] imageBytes, String folderName){
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap("asset_folder", folderName)
            );

        return uploadResult.get("secure_url").toString();
    } catch (Exception e) {
            throw new FileUploadException("Failed to upload file to Cloudinary: " + e.getMessage());
        }
    }

}
