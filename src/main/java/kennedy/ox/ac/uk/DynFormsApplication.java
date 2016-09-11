package kennedy.ox.ac.uk;

import kennedy.ox.ac.uk.Helpers.storage.StorageProperties;
import kennedy.ox.ac.uk.Helpers.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DynFormsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynFormsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}


