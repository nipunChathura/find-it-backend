package lk.icbt.findit.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;


@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.service-account-resource:find-it-8283f-firebase-adminsdk-fbsvc-44f6d583fc.json}")
    private String serviceAccountResource;

    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                Resource resource = new ClassPathResource(serviceAccountResource);
                if (!resource.exists()) {
                    log.warn("Firebase service account file not found: {}. Push notifications will be disabled.", serviceAccountResource);
                    return;
                }
                try (InputStream is = resource.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(is))
                            .build();
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase App initialized successfully for FCM.");
                }
            } catch (IOException e) {
                log.error("Failed to initialize Firebase App. Push notifications will be disabled.", e);
            }
        }
    }
}
