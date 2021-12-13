package com.example.amazons3.dto;

import com.example.amazons3.entity.S3Object;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class S3ObjectDTO {
    @Getter
    @Setter
    public static class Request {
        private List<MultipartFile> multipartFiles;
    }

    @Getter
    public static class Response {
        private Long id;
        private String key;
        private String name;
        private String path;

        @Builder
        public Response(Long id, String key, String name, String path) {
            this.id = id;
            this.key = key;
            this.name = name;
            this.path = path;
        }

        public static Response toDTO(S3Object s3Object) {
            return Response.builder()
                        .id(s3Object.getId())
                        .key(s3Object.getKey())
                        .name(s3Object.getName())
                        .path(s3Object.getPath())
                        .build();
        }
    }
}
