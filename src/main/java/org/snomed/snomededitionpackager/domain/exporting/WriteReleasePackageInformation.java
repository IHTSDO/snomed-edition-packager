package org.snomed.snomededitionpackager.domain.exporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.snomed.snomededitionpackager.domain.rf2.LanguageReferenceSet;
import org.snomed.snomededitionpackager.domain.rf2.ReleasePackageInformation;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Component
public class WriteReleasePackageInformation implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteReleasePackageInformation.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final DataStore dataStore;

	public WriteReleasePackageInformation(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	public boolean write(ExportConfiguration exportConfiguration) {
		if (exportConfiguration == null) {
			return false;
		}

		// Prepare text file(s)
		String rf2Package = exportConfiguration.getRf2Package();
		Path singularPath = Paths.get(FileNameService.getReleasePackageInformation(rf2Package));
		String effectiveTime = exportConfiguration.getEffectiveTime();

		// Create text file(s)
		if (!createTextFiles(singularPath)) {
			return false;
		}

		// Either generate or load data
		ReleasePackageInformation releasePackageInformation;
		boolean writeGenerated = "*".equals(exportConfiguration.getReleasePackageInformation());
		if (writeGenerated) {
			releasePackageInformation = new ReleasePackageInformation();
			releasePackageInformation.setEffectiveTime(effectiveTime);
			releasePackageInformation.setPreviousPublishedPackage("NONE");
			releasePackageInformation.setLicenceStatement("NONE");

			for (ReleasePackageInformation rpi : dataStore.getReleasePackageInformations()) {
				Set<LanguageReferenceSet> languageRefsets = rpi.getLanguageRefsets();
				if (languageRefsets != null && !languageRefsets.isEmpty()) {
					releasePackageInformation.getLanguageRefsets().addAll(languageRefsets);
				}

				releasePackageInformation.addPackageComposition(rpi.getPackageComposition());
			}
		} else {
			try {
				releasePackageInformation = OBJECT_MAPPER.readValue(new File(exportConfiguration.getReleasePackageInformation()), ReleasePackageInformation.class);
			} catch (Exception e) {
				LOGGER.error("Failed to read local release package information file.", e);
				return false;
			}
		}

		try {
			OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(singularPath.toFile(), releasePackageInformation);
		} catch (Exception e) {
			LOGGER.error("Failed to write release package information file", e);
			return false;
		}

		return true;
	}
}