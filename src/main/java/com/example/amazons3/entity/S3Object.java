package com.example.amazons3.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class S3Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "object_key", length = 60)
    private String key;

    private String name;

    @Column(length = 30)
    private String type;

    private long size;

    private String path;

    @Builder
    public S3Object(String key, String name, String type, long size, String path) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.size = size;
        this.path = path;
    }
}
