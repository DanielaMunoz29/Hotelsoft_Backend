package com.proyectohotelsoft.backend.configs;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary Cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dvkvps1fy",
                "api_key", "933526346498972",
                "api_secret", "8gAzqQF8Gv9n6l1JD7W2_5kMeFQ"
        ));
    }

}