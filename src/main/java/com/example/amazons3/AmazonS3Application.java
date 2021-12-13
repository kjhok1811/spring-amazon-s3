package com.example.amazons3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AmazonS3Application {
    public static void main(String[] args) {
        SpringApplication springApplication =
                new SpringApplicationBuilder(AmazonS3Application.class)
                    .initializers(new SystemInitializer())
                    .build();
        springApplication.run(args);
    }
}

class SystemInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
        System.setProperty("bucket", "your aws s3 bucket name");
        System.setProperty("access-key", "your access-key");
        System.setProperty("secret-key", "your secret-key");
    }
}