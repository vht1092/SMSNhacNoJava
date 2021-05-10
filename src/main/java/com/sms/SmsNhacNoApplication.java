package com.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
@EncryptablePropertySource(name = "EncryptedProperties", value = "classpath:encrypted.properties")
//// JAR file
//public class FraudDetectionSystemsApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(FraudDetectionSystemsApplication.class, args);
//	}
//
//	// @Bean
//	// public LocaleResolver localeResolver() {
//	// SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//	// localeResolver.setDefaultLocale(new Locale("vi", "VN"));
//	// return localeResolver;
//	// }
//}

// WAR
public class SmsNhacNoApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SmsNhacNoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SmsNhacNoApplication.class, args);
	}

}