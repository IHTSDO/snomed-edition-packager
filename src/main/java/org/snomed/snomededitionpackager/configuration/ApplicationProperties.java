package org.snomed.snomededitionpackager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {
	@Value("${spring.application.name}")
	private String appName;

	@Value("${app.environment}")
	private String appEnvironment;

	public String getAppName() {
		return appName;
	}

	public void setAppEnvironment(String appEnvironment) {
		this.appEnvironment = appEnvironment;
	}
}