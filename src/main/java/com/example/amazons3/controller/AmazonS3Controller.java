package com.example.amazons3.controller;

import com.example.amazons3.dto.S3ObjectDTO;
import com.example.amazons3.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/amazon/s3")
@RequiredArgsConstructor
public class AmazonS3Controller {
    private final AmazonS3Service amazonS3Service;

    @GetMapping("/objects")
    public List<S3ObjectDTO.Response> getS3Objects() {
        return amazonS3Service.getS3Objects();
    }

    @GetMapping("/objects/{id}/download")
    public void downloadS3Object(@PathVariable Long id, HttpServletResponse response) {
        amazonS3Service.downloadS3Object(id, response);
    }

    @PostMapping("/objects")
    public void uploadS3Object(S3ObjectDTO.Request request) {
        amazonS3Service.uploadS3Object(request.getMultipartFiles());
    }

    @DeleteMapping("/objects/{id}")
    public void deleteS3Object(@PathVariable Long id) {
        amazonS3Service.deleteS3Object(id);
    }
}
