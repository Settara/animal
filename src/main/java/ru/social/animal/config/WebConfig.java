package ru.social.animal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Доступ к файлам объявлений: /uploads/** → папка uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Доступ к фото пользователей: /user-images/** → папка user-images/
        registry.addResourceHandler("/user-images/**")
                .addResourceLocations("file:user-images/");

        // Доступ к фото постов пользователей: /user-images/** → папка user-images/
        registry.addResourceHandler("/user-posts/**")
                .addResourceLocations("file:user-posts/");
    }
}
