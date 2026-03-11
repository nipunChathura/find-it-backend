package lk.icbt.findit.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("!test")
@EntityScan(basePackages = "lk.icbt.findit.entity")
@EnableJpaRepositories(basePackages = "lk.icbt.findit.repository")
public class JpaConfig {
}
