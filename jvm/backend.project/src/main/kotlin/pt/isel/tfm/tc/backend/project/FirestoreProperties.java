package pt.isel.tfm.tc.backend.project;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FirestoreProperties {

    public static FirebaseApp init() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("./tfm-2324-project-firebase-adminsdk-fognk-176b5a82d5.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
