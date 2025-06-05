package org.snomed.snomededitionpackager.domain.exporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WriteRoot implements ExportWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteRoot.class);

	@Override
	public boolean write(ExportConfiguration exportConfiguration) {
		if (exportConfiguration == null) {
			return false;
		}

		try {
			return new File(exportConfiguration.getRf2Package()).mkdir();
		} catch (Exception e) {
			LOGGER.error("Failed to create root directory.", e);
			return false;
		}
	}
}