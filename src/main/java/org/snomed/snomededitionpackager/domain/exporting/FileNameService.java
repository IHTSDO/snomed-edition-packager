package org.snomed.snomededitionpackager.domain.exporting;

import org.snomed.snomededitionpackager.domain.datastore.DataStore;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FileNameService {
	public static final String RELEASE_PACKAGE_INFORMATION_JSON = "release_package_information.json";

	private final DataStore dataStore;

	public FileNameService(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public static String getAxiom(String rf2PackageName, String type, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_sRefset_OWLExpression%s_%s_%s.txt", rf2PackageName, type, type, shortName, effectiveTime);
	}

	public static String getAxiom(String type, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_sRefset_OWLExpression%s_%s_%s.txt", type, type, shortName, effectiveTime);
	}

	public static String getConcept(String rf2PackageName, String type, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_Concept_%s_%s_%s.txt", rf2PackageName, type, type, shortName, effectiveTime);
	}

	public static String getConcept(String type, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_Concept_%s_%s_%s.txt", type, type, shortName, effectiveTime);
	}

	public static String getConcreteRelationship(String rf2PackageName, String type, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_RelationshipConcreteValues_%s_%s_%s.txt", rf2PackageName, type, type, shortName, effectiveTime);
	}

	public static String getConcreteRelationship(String type, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_RelationshipConcreteValues_%s_%s_%s.txt", type, type, shortName, effectiveTime);
	}

	public static String getDescription(String rf2PackageName, String type, String languageCode, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_Description_%s-%s_%s_%s.txt", rf2PackageName, type, type, languageCode, shortName, effectiveTime);
	}

	public static String getDescription(String type, String languageCode, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_Description_%s-%s_%s_%s.txt", type, type, languageCode, shortName, effectiveTime);
	}

	public static String getIdentifier(String rf2PackageName, String type, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_Identifier_%s_%s_%s.txt", rf2PackageName, type, type, shortName, effectiveTime);
	}

	public static String getIdentifier(String type, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_Identifier_%s_%s_%s.txt", type, type, shortName, effectiveTime);
	}

	public static String getRelationship(String rf2PackageName, String type, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_Relationship_%s_%s_%s.txt", rf2PackageName, type, type, shortName, effectiveTime);
	}

	public static String getRelationship(String type, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_Relationship_%s_%s_%s.txt", type, type, shortName, effectiveTime);
	}

	public static String getStatedRelationship(String rf2PackageName, String type, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_StatedRelationship_%s_%s_%s.txt", rf2PackageName, type, type, shortName, effectiveTime);
	}

	public static String getStatedRelationship(String type, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_StatedRelationship_%s_%s_%s.txt", type, type, shortName, effectiveTime);
	}

	public static String getTextDefinition(String rf2PackageName, String type, String languageCode, String shortName, String effectiveTime) {
		return String.format("%s/%s/Terminology/sct2_TextDefinition_%s-%s_%s_%s.txt", rf2PackageName, type, type, languageCode, shortName, effectiveTime);
	}

	public static String getTextDefinition(String type, String languageCode, String shortName, String effectiveTime) {
		return String.format("%s/Terminology/sct2_TextDefinition_%s-%s_%s_%s.txt", type, type, languageCode, shortName, effectiveTime);
	}

	public static String getReadme(String rf2PackageName, String effectiveTime) {
		return rf2PackageName + "/" + "Readme_en_" + effectiveTime + ".txt";
	}

	public static String getReadme(String effectiveTime) {
		return "Readme_en_" + effectiveTime + ".txt";
	}

	public static String getReleasePackageInformation(String rf2PackageName) {
		return rf2PackageName + "/" + RELEASE_PACKAGE_INFORMATION_JSON;
	}

	public static String removeType(String input) {
		input = input.replace("Full", "");
		input = input.replace("Snapshot", "");

		return input;
	}

	public static String removeLeadingSlashes(String input, int numberToKeep) {
		if (input.lastIndexOf("/") == -1) {
			return input;
		}

		List<String> segments = Arrays.asList(input.split("/"));

		int total = segments.size();
		int start = Math.max(total - numberToKeep, 0);

		input = String.join("/", segments.subList(start, total));
		if (input.startsWith("/")) {
			input = input.replaceFirst("/", "");
		}

		return input;
	}

	public static String removeShortNameEffectiveTime(String input) {
		int lastUnderscore = input.lastIndexOf('_');
		if (lastUnderscore == -1) {
			return input;
		}

		int secondLastUnderscore = input.lastIndexOf('_', lastUnderscore - 1);
		if (secondLastUnderscore == -1) {
			return input;
		}

		return input.substring(0, secondLastUnderscore);
	}

	// Names are computed from input files and therefore cached
	public String getReferenceSet(String rf2PackageName, String type, String shortName, String effectiveTime, String refsetId) {
		String fileName = dataStore.getFileName(refsetId);
		boolean isLanguageReferenceSet = fileName.contains("-");
		if (isLanguageReferenceSet) {
			String[] split = fileName.split("-");
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(rf2PackageName).append("/").append(type).append("/").append(split[0]).append(type);
			for (int i = 1; i < split.length; i++) {
				stringBuilder.append("-").append(split[i]);
			}
			stringBuilder.append("_").append(shortName).append("_").append(effectiveTime).append(".txt");
			return stringBuilder.toString();
		}

		return rf2PackageName + "/" + type + "/" + dataStore.getFileName(refsetId) + type + "_" + shortName + "_" + effectiveTime + ".txt";
	}

	// Names are computed from input files and therefore cached
	public String getReferenceSet(String type, String shortName, String effectiveTime, String refsetId) {
		String fileName = dataStore.getFileName(refsetId);
		boolean isLanguageReferenceSet = fileName.contains("-");
		if (isLanguageReferenceSet) {
			String[] split = fileName.split("-");
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("/").append(type).append("/").append(split[0]).append(type);
			for (int i = 1; i < split.length; i++) {
				stringBuilder.append("-").append(split[i]);
			}
			stringBuilder.append("_").append(shortName).append("_").append(effectiveTime).append(".txt");
			return stringBuilder.toString();
		}

		return type + "/" + dataStore.getFileName(refsetId) + type + "_" + shortName + "_" + effectiveTime + ".txt";
	}

	// e.g. der2_sRefset_XXXXSimpleMapSnapshot_XX_20250930 => der2_sRefset_SimpleMapSnapshot_XX_20250930
	public static String removeDerivativePrefix(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}

		String[] inputBySlash = input.split("/");
		String[] inputByUnderscore = inputBySlash[inputBySlash.length - 1].split("_");
		if (inputByUnderscore.length == 1) {
			return input;
		}

		String thirdUnderscore = inputByUnderscore[2];
		int index = -1;
		for (int i = 0; i < thirdUnderscore.length(); i++) {
			if (Character.isLowerCase(thirdUnderscore.charAt(i))) {
				break;
			}

			if (mrcmIsNextWord(thirdUnderscore, i)) {
				index = index + 1;
				break;
			}

			index = i;
		}

		inputByUnderscore[2] = thirdUnderscore.substring(index);
		inputBySlash[inputBySlash.length - 1] = String.join("_", inputByUnderscore);

		return String.join("/", inputBySlash);
	}

	// e.g. Refset/Content/der2_cRefset_AssociationReference => Refset/Content/der2_cRefset_900000000000527005
	public static String replaceReferenceSetNameWithReferenceSetType(String referenceSetName, String referenceSetType) {
		if (referenceSetName == null || referenceSetName.isEmpty() || referenceSetType == null || referenceSetType.isEmpty()) {
			return null;
		}

		String[] split = referenceSetName.split("_");
		if (split.length == 1) {
			return referenceSetName;
		}

		split[split.length - 1] = referenceSetType;
		return String.join("_", split);
	}

	private static boolean mrcmIsNextWord(String thirdUnderscore, int startIndex) {
		return thirdUnderscore.startsWith("MRCM", startIndex);
	}
}
