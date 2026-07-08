package com.itc.linkedin.feedAndTimeline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MediaUrlService {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket:${AWS_S3_BUCKET:}}")
    private String bucket;

    public String presignedUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey) || !StringUtils.hasText(bucket)) {
            return null;
        }

        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(builder -> builder.bucket(bucket).key(objectKey))
                .build();

        return s3Presigner.presignGetObject(request).url().toString();
    }
}
