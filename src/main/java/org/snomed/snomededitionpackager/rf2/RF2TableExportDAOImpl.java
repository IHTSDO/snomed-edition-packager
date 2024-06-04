package org.snomed.snomededitionpackager.rf2;

import org.ihtsdo.otf.rest.exception.BadConfigurationException;
import org.ihtsdo.snomed.util.rf2.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomededitionpackager.key.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class RF2TableExportDAOImpl implements RF2TableExportDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(RF2TableExportDAOImpl.class);

	private final SchemaFactory schemaFactory;

	private TableSchema tableSchema;

	private DataType idType;

	private Map<Key, String> table;

	public RF2TableExportDAOImpl() {
		schemaFactory = new SchemaFactory();
		table = new TreeMap<>();
	}

	@Override
	public TableSchema createTable(final String filename, final InputStream rf2InputStream) throws IOException, FileRecognitionException, BadConfigurationException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(rf2InputStream, RF2Constants.UTF_8))) {
			// Product Schema
			LOGGER.info("Creating table from {}", filename);
			final String headerLine = getHeader(filename, reader);
			tableSchema = schemaFactory.createSchemaBean(filename);
			if (tableSchema == null) {
				throw new FileRecognitionException("Failed to create a tableSchema using RF2 filename: " + filename);
			}
			idType = tableSchema.getFields().get(0).getType();
			schemaFactory.populateExtendedRefsetAdditionalFieldNames(tableSchema, headerLine);

			// Insert Data
			insertData(reader, tableSchema);

			return tableSchema;
		}
	}

	@Override
	public void appendData(final TableSchema tableSchema, final InputStream rf2InputStream) throws IOException, BadConfigurationException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(rf2InputStream, RF2Constants.UTF_8))) {
			reader.readLine(); // Discard header line
			insertData(reader, tableSchema);
		}
	}

	@Override
	public RF2TableResults selectAllOrdered(final TableSchema tableSchema) {
		return new RF2TableResultsMapImpl(table);
	}

	@Override
	public void closeConnection() {
		if ( table != null) {
			table.clear();
			table = null;
		}
	}
	private String getHeader(final String filename, final BufferedReader reader) throws IOException {
		final String headerLine = reader.readLine();
		if (headerLine == null) {
			throw new RuntimeException("RF2 file " + filename + " is empty.");
		}
		return headerLine;
	}

	private void insertData(final BufferedReader reader, final TableSchema tableSchema) throws IOException, BadConfigurationException {
		insertData(reader, tableSchema, null);
	}

	private void insertData(final BufferedReader reader, final TableSchema tableSchema, String previousEffectiveDate) throws IOException {
		// Declare variables at top to prevent constant memory reallocation during recursion
		String line;
		String[] parts;
		Key key;
		// date format is always in yyyyMMdd so it is faster to compare as integer
		Integer previousDate = previousEffectiveDate != null ? Integer.parseInt(previousEffectiveDate) : null;
		while ((line = reader.readLine()) != null) {
			parts = line.split(RF2Constants.COLUMN_SEPARATOR, 3);
			if (previousDate != null && Integer.parseInt(parts[1]) <= previousDate) {
				// skip data from previous release
				continue;
			}
			key = tableSchema.getComponentType() == ComponentType.IDENTIFIER ? getIdentifierCompositeKey(line) : getKey(parts[0], parts[1]);
			table.put(key, parts[2]);
		}
	}

	private Key getIdentifierCompositeKey(final String line) {
		String[] parts = line.split(RF2Constants.COLUMN_SEPARATOR, 6);
		return new StringKey(parts[0] + RF2Constants.COLUMN_SEPARATOR + parts[4], parts[1]);
	}

	private Key getKey(final String part0, final String part1) {
		Key key;
		if (idType == DataType.SCTID) {
			key = new SCTIDKey(part0, part1);
		} else {
			key = new UUIDKey(part0, part1);
		}
		return key;
	}
}
