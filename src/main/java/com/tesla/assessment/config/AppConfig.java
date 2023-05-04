package com.tesla.assessment.config;
import com.tesla.assessment.service.ApiService;
import com.tesla.assessment.service.ErrorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ErrorService errorService() {
        return new ErrorService();
    }
    @Bean
    public ApiService apiService(){
        return new ApiService();
    }
}
