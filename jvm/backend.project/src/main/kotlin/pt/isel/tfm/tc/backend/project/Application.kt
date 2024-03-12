package pt.isel.tfm.tc.backend.project

import com.google.firebase.cloud.FirestoreClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource

@ConfigurationPropertiesScan
@SpringBootApplication
@Configuration
class Application() : WebMvcConfigurer {
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
