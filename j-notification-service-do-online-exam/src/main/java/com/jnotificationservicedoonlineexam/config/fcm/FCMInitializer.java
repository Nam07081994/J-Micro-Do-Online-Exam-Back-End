package com.jnotificationservicedoonlineexam.config.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class FCMInitializer {

	@Value("${app.firebase.firebase-configuration-file}")
	private String firebaseConfigPath;

	@PostConstruct
	public void initializer() {
		try {
			FirebaseOptions opts =
					new FirebaseOptions.Builder()
							.setCredentials(
									GoogleCredentials.fromStream(
											new ClassPathResource(firebaseConfigPath).getInputStream()))
							.build();
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(opts);
				System.out.println("Firebase application has been initialized");
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
