package pt.isel.tfm.tc.backend.project

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@ConfigurationPropertiesScan
@SpringBootApplication
@Configuration
class Application() : WebMvcConfigurer {
	/**
	 * Applying the configuration of the db connection
	 */
	@Bean
	fun dataSource() = FirestoreProperties.init()
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
