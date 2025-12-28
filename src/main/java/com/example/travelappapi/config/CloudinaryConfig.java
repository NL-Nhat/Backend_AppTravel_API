package com.example.travelappapi.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", ""); // Thay bằng Cloud Name
        config.put("api_key", "");       // Thay bằng API Key
        config.put("api_secret", ""); // Thay bằng API Secret
        config.put("secure", "true");
        return new Cloudinary(config);
    }
}
