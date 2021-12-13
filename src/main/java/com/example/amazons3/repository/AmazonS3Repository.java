package com.example.amazons3.repository;

import com.example.amazons3.entity.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmazonS3Repository extends JpaRepository<S3Object, Long> {
}
