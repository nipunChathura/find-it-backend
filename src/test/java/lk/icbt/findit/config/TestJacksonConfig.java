package lk.icbt.findit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides ObjectMapper for @WebMvcTest contexts that do not load full Jackson auto-configuration.
 */
@Configuration
public class TestJacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
