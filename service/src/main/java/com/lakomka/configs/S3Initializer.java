package com.lakomka.configs;

import com.lakomka.services.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Initializer {
    private final S3Service s3Service;

    @PostConstruct
    public void init() {
        s3Service.createBucketIfNotExists();
    }
}
