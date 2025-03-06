package com.seob.systeminfra.file;

import com.seob.systemcore.error.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3FileUploadService implements FileUploadService {
    private final S3Client s3Client;
    private final String bucketName;

    public S3FileUploadService(S3Client s3Client, @Value("${cloud.aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));

            return getFileUrl(fileName);
        } catch (IOException e) {
            throw FileUploadException.FILE_UPLOAD_FAILED;
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw FileUploadException.FILE_DELETE_FAILED;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw FileUploadException.INVALID_FILE_FORMAT;
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw FileUploadException.INVALID_FILE_FORMAT;
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        return "rewards/" + UUID.randomUUID().toString() + "-" +
                (originalFilename != null ? originalFilename : "unknown"); //비어있으면 unknown
    }

    private String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
    }
}