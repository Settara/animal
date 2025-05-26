package ru.social.animal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Все URL вида /uploads/** будут обслуживаться из папки uploads/ на диске
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // uploads/ - это путь в файловой системе (относительный к корню проекта)
    }
}
