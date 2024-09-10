package pt.isel.tfm.tc.backend.project

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.tfm.tc.backend.project.common.authorization.AccessControlInterceptor
import pt.isel.tfm.tc.backend.project.common.data.PromptData
import java.io.FileInputStream

@ConfigurationPropertiesScan
@SpringBootApplication
@Configuration
class Application(
		private val configProperties: FirebaseConfigProperties
) : WebMvcConfigurer {

	@Bean
	fun initFirestore(): Firestore {
		val serviceAccount = FileInputStream(configProperties.credentials)

		val options = FirestoreOptions.getDefaultInstance().toBuilder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build()

		return options.service
	}

	@Bean
	fun initFirebase(): FirebaseApp{
		// Initialize Firebase Admin SDK
		val serviceAccount = FileInputStream(configProperties.credentials)
		val options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build()
		return FirebaseApp.initializeApp(options)
	}

	@Bean
	fun firebaseAuth(): FirebaseAuth {
		return FirebaseAuth.getInstance(initFirebase())
	}

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(AccessControlInterceptor(PromptData(initFirestore())))
	}

	@Bean
	fun byteArrayHttpMessageConverter(): ByteArrayHttpMessageConverter {
		return ByteArrayHttpMessageConverter()
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
