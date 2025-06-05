package org.snomed.snomededitionpackager.configuration;

import org.ihtsdo.otf.snomedboot.ReleaseImporter;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanRegistry {
	@Bean
	public DataStore dataStore() {
		return new DataStore();
	}

	@Bean
	public ReleaseImporter releaseImporter() {
		return new ReleaseImporter();
	}
}