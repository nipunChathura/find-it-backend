package lk.icbt.findit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "lk.icbt.findit.entity")
@EnableJpaRepositories(basePackages = "lk.icbt.findit.repository")
public class FindItApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindItApplication.class, args);
	}

}
