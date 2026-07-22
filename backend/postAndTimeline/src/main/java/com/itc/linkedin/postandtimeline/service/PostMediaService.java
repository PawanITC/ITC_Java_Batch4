package com.itc.linkedin.postandtimeline.service;

import com.itc.linkedin.postandtimeline.dto.response.MediaUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class PostMediaService {

    private static final long MAX_FILE_BYTES = 50L * 1024L * 1024L;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4", "video/webm", "video/quicktime");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket:${AWS_S3_BUCKET:}}")
    private String bucket;

    public MediaUploadResponse upload(String userId, MultipartFile file) {
        if (!StringUtils.hasText(bucket)) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "S3 upload bucket is not configured.");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Media file is required.");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new ResponseStatusException(BAD_REQUEST, "Media file must be 50 MB or smaller.");
        }

        String contentType = file.getContentType();
        String mediaType = mediaTypeFor(contentType);
        String extension = extensionFor(contentType);
        String objectKey = "posts/%s/%s%s".formatted(sanitizeUserId(userId), UUID.randomUUID(), extension);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(contentType)
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return new MediaUploadResponse(presignedUrl(objectKey), mediaType, objectKey, objectKey);
        } catch (IOException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Could not read uploaded media.", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Could not upload media to S3.", ex);
        }
    }

    private String mediaTypeFor(String contentType) {
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return "IMAGE";
        }
        if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            return "VIDEO";
        }

        throw new ResponseStatusException(BAD_REQUEST, "Only image and video uploads are supported.");
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            case "video/quicktime" -> ".mov";
            default -> "";
        };
    }

    private String sanitizeUserId(String userId) {
        return userId.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    public String presignedUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        if (!StringUtils.hasText(bucket)) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "S3 upload bucket is not configured.");
        }

        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(builder -> builder.bucket(bucket).key(objectKey))
                .build();

        return s3Presigner.presignGetObject(request).url().toString();
    }
}
