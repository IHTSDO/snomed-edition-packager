package org.snomed.snomededitionpackager.rf2;

import org.ihtsdo.otf.rest.exception.BadConfigurationException;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.snomed.util.rf2.schema.FileRecognitionException;
import org.ihtsdo.snomed.util.rf2.schema.TableSchema;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public interface RF2TableExportDAO extends AutoCloseable {

	TableSchema createTable(String filename, InputStream rf2InputStream) throws IOException, FileRecognitionException, ParseException, BusinessServiceException;

	void appendData(TableSchema tableSchema, InputStream rf2InputStream) throws IOException, ParseException, BadConfigurationException;

	RF2TableResults selectAllOrdered(TableSchema tableSchema);

}

