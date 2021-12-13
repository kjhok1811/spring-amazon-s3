package com.example.amazons3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.amazons3.dto.S3ObjectDTO;
import com.example.amazons3.entity.S3Object;
import com.example.amazons3.repository.AmazonS3Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final AmazonS3Repository amazonS3Repository;

    @Transactional(readOnly = true)
    public List<S3ObjectDTO.Response> getS3Objects() {
        return amazonS3Repository.findAll().stream().map(S3ObjectDTO.Response::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void downloadS3Object(Long id, HttpServletResponse response) {
        S3Object s3Object = amazonS3Repository.findById(id).orElseThrow(EntityNotFoundException::new);
        doesObjectExist(s3Object.getKey());

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(s3Object.getPath());
            inputStream = url.openStream();
            outputStream = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int n = 0;

            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }
        } catch (MalformedURLException e) {
            log.error("url connection error ! {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("file I/O error ! {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            close(inputStream, outputStream);
        }
    }

    @Transactional
    public void uploadS3Object(List<MultipartFile> multipartFiles) {
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new RuntimeException("The File is empty !");
        }
        List<S3Object> s3Objects = new ArrayList<>(multipartFiles.size());

        multipartFiles.forEach(multipartFile -> {
            try (InputStream inputStream = multipartFile.getInputStream()){
                String objectKey = UUID.randomUUID().toString() + System.currentTimeMillis();
                ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);

                PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectKey, inputStream, objectMetadata);
                amazonS3.putObject(putObjectRequest);

                s3Objects.add(
                    S3Object.builder()
                        .key(objectKey)
                        .name(multipartFile.getOriginalFilename())
                        .type(objectMetadata.getContentType())
                        .size(objectMetadata.getContentLength())
                        .path(amazonS3.getUrl(bucket, objectKey).toString())
                        .build()
                );
            } catch (IOException e) {
                log.error("file I/O error ! {}", e.getMessage());
                throw new RuntimeException(e);
            } catch (AmazonServiceException e) {
                log.error("s3 upload error ! {}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
        amazonS3Repository.saveAll(s3Objects);
    }

    @Transactional
    public void deleteS3Object(Long id) {
        S3Object s3Object = amazonS3Repository.findById(id).orElseThrow(EntityNotFoundException::new);
        doesObjectExist(s3Object.getKey());

        amazonS3.deleteObject(bucket, s3Object.getKey());
        amazonS3Repository.deleteById(s3Object.getId());
    }

    private void doesObjectExist(String key) {
        if (!amazonS3.doesObjectExist(bucket, key)) {
            throw new AmazonServiceException("The S3 Object '" + key + "' does not exist !");
        }
    }

    private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(Mimetypes.getInstance().getMimetype(fileName));
        objectMetadata.setContentLength(size);

        return objectMetadata;
    }

    private void close(InputStream inputStream, OutputStream outputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("inputStream close error ! {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("outputStream close error ! {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
