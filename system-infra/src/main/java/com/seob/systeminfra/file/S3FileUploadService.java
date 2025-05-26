package com.seob.systeminfra.file;

import com.seob.systemcore.error.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static software.amazon.awssdk.core.sync.RequestBody.fromInputStream;

@Slf4j
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
        String fileName = generateUniqueFileName(file.getOriginalFilename()); // 파일 이름 생성

        //파일이 있을 때만
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    fromInputStream(inputStream, file.getSize()));

            String fileUrl = getFileUrl(fileName);
            log.info("파일 업로드 성공: {}", fileUrl);
            return fileUrl;
            
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
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
            log.info("파일 삭제 성공: {}", fileUrl);
            
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage(), e);
            throw FileUploadException.FILE_DELETE_FAILED;
        }
    }

    //파일 이름 생성
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return "rewards/" + uuid + extension;
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    //파일 url 가져오기
    private String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }

    //url에서 키 추출
    private String extractKeyFromUrl(String fileUrl) {
        try {
            String bucketDomain = bucketName + ".s3.amazonaws.com/";
            int keyStartIndex = fileUrl.indexOf(bucketDomain);
            if (keyStartIndex == -1) {
                throw FileUploadException.INVALID_FILE_URL;
            }
            return fileUrl.substring(keyStartIndex + bucketDomain.length());
        } catch (FileUploadException e) {
            throw e;
        } catch (Exception e) {
            log.error("S3 URL에서 키 추출 실패: {}", fileUrl, e);
            throw FileUploadException.INVALID_FILE_URL;
        }
    }
}
