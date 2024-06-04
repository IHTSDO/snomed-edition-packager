package org.snomed.snomededitionpackager.rf2;

import org.ihtsdo.otf.rest.exception.BadConfigurationException;
import org.ihtsdo.snomed.util.rf2.schema.FileRecognitionException;
import org.ihtsdo.snomed.util.rf2.schema.TableSchema;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;

public interface RF2TableExportDAO {

	TableSchema createTable(String filename, InputStream rf2InputStream) throws SQLException, IOException, FileRecognitionException, ParseException, BadConfigurationException;

	void appendData(TableSchema tableSchema, InputStream rf2InputStream) throws IOException, SQLException, ParseException, BadConfigurationException;

	RF2TableResults selectAllOrdered(TableSchema tableSchema) throws SQLException;

	void closeConnection() throws SQLException;

}

