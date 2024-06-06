package org.snomed.snomededitionpackager.util;

import org.snomed.snomededitionpackager.rf2.RF2Constants;

public class ReleasePackageUtils {

	
	/**Release package name is updated e.g SnomedCT_InternationalRF2_Production_20170131T120000.zip
	 * instead of SnomedCT_Release_INT_20170131.zip
	 * @param releasePackage
	 * @return
	 */
	public  static String getReleaseDateFromReleasePackage(String releasePackage) {
		if (releasePackage != null && releasePackage.endsWith(RF2Constants.ZIP_FILE_EXTENSION)) {
			String [] splits = releasePackage.split(RF2Constants.FILE_NAME_SEPARATOR);
			String releaseDate = splits[splits.length - 1];
			if (releaseDate.length() > 8) {
				releaseDate = releaseDate.substring(0, 8);
			}
			return releaseDate;
		}
		return null;
	}

	public static String getNamespaceFromExtensionRf2File(String filename) {
		if (filename != null && filename.endsWith(RF2Constants.TXT_FILE_EXTENSION)) {
			String [] splits = filename.split(RF2Constants.FILE_NAME_SEPARATOR);
			return splits[splits.length - 2];
		}
		return null;
	}
}
