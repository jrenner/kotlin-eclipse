package org.jetbrains.kotlin.testframework.utils;

import java.util.ArrayList;
import java.util.List;

public class ExpectedCompletionUtils {

	public static final String EXIST_LINE_PREFIX = "EXIST:";
	
	public static List<String> itemsShouldExist(String fileText, String prefix) {
		List<String> proposals = new ArrayList<String>();
		for (String proposalStr : InTextDirectiveUtils.findLinesWithPrefixesRemoved(fileText, prefix)) {
			if (!proposalStr.startsWith("{")) {
				for (String item : proposalStr.split(",")) {
					proposals.add(item);
				}
			}
		}
		
		return proposals;
	}
}
