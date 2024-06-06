package org.snomed.snomededitionpackager.rf2;

import org.ihtsdo.snomed.util.rf2.schema.ComponentType;
import org.ihtsdo.snomed.util.rf2.schema.Field;
import org.ihtsdo.snomed.util.rf2.schema.TableSchema;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.regex.Pattern;

public class Rf2FileWriter {

	public void exportFullAndSnapshot(RF2TableResults tableResults, TableSchema schema, String targetEffectiveTime, OutputStream fullOutputStream, OutputStream snapshotOutputStream) throws IOException {

		try (BufferedWriter fullWriter = new BufferedWriter(new OutputStreamWriter(fullOutputStream, RF2Constants.UTF_8));
			 BufferedWriter snapshotWriter = new BufferedWriter(new OutputStreamWriter(snapshotOutputStream, RF2Constants.UTF_8))) {

			// Declare a few objects to reuse over and over.
			final List<Field> fields = schema.getFields();

			// Product header
			String header = generateHeader(fields);
			fullWriter.write(header);
			fullWriter.append(RF2Constants.LINE_ENDING);
			snapshotWriter.write(header);
			snapshotWriter.append(RF2Constants.LINE_ENDING);

			// Variables for snapshot resolution
			String currentLine;
			String currentId;
			int currentEffectiveTimeInt;
			int targetEffectiveTimeInt = Integer.parseInt(targetEffectiveTime);
			String lastId = null;
			String validLine = null;
			boolean movedToNewMember;
			boolean passedTargetEffectiveTime;

			// Iterate through data
			while ((currentLine = tableResults.nextLine()) != null) {
				// Parse out id and effectiveTime
				String[] lineParts;
				if (ComponentType.IDENTIFIER.equals(schema.getComponentType())) {
					lineParts = currentLine.split(RF2Constants.COLUMN_SEPARATOR, 6);
					currentId = lineParts[0] + RF2Constants.COLUMN_SEPARATOR + lineParts[1];
					// effective time is on the third column
					currentEffectiveTimeInt = Integer.parseInt(lineParts[2]);
					// Replace the composite key by identifierSchemeId
					currentLine = currentLine.replace(currentId, lineParts[0]);
				} else {
					if (Pattern.compile(RF2Constants.LANGUAGE_FILE_PATTERN).matcher(schema.getFilename()).matches()) {
						lineParts = currentLine.split(RF2Constants.COLUMN_SEPARATOR, 6);
					} else {
						lineParts = currentLine.split(RF2Constants.COLUMN_SEPARATOR, 3);
					}

					currentId = lineParts[0];
					// effective time is on the second column
					currentEffectiveTimeInt = Integer.parseInt(lineParts[1]);
				}

				// Write to Full file
				fullWriter.append(currentLine);
				fullWriter.append(RF2Constants.LINE_ENDING);

				// If moved to new member or passed target effectiveTime write any previous valid line
				movedToNewMember = lastId != null && !lastId.equals(currentId);
				passedTargetEffectiveTime = currentEffectiveTimeInt > targetEffectiveTimeInt;
				if ((movedToNewMember || passedTargetEffectiveTime) && validLine != null) {
					snapshotWriter.append(validLine);
					snapshotWriter.append(RF2Constants.LINE_ENDING);
					validLine = null;
				}

				// Store valid line if effectiveTime not exceeded
				if (!passedTargetEffectiveTime) {
					validLine = currentLine;
				}

				// Record last id
				lastId = currentId;
			}

			// Write out any valid line not yet written
			if (validLine != null) {
				snapshotWriter.append(validLine);
				snapshotWriter.append(RF2Constants.LINE_ENDING);
			}
		}
	}

	private String generateHeader(List<Field> fields) {
		StringBuilder builder = new StringBuilder();
		boolean firstField = true;
		for (Field field : fields) {
			if (firstField) {
				firstField = false;
			} else {
				builder.append(RF2Constants.COLUMN_SEPARATOR);
			}
			builder.append(field.getName());
		}
		return builder.toString();
	}
}
