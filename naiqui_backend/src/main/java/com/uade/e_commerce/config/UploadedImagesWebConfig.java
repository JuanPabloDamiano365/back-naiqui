package com.uade.e_commerce.config;

import java.nio.file.Path;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.uade.e_commerce.service.ProductoImagenStorageService;

@Configuration
public class UploadedImagesWebConfig implements WebMvcConfigurer {

    private final ProductoImagenStorageService storageService;

    public UploadedImagesWebConfig(ProductoImagenStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = storageService.getUploadRoot();
        String location = root.toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
