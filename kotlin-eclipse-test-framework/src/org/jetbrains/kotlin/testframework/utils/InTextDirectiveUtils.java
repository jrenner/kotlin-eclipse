package org.jetbrains.kotlin.testframework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.intellij.openapi.util.text.StringUtil;

public class InTextDirectiveUtils {
	
    public static List<String> findLinesWithPrefixesRemoved(String fileText, String... prefixes) {
        List<String> result = new ArrayList<String>();
        List<String> cleanedPrefixes = cleanDirectivesFromComments(Arrays.asList(prefixes));

        for (String line : fileNonEmptyCommentedLines(fileText)) {
            for (String prefix : cleanedPrefixes) {
                if (!line.startsWith(prefix)) continue;
                
                String noPrefixLine = line.substring(prefix.length());

                if (noPrefixLine.isEmpty() || Character.isWhitespace(noPrefixLine.charAt(0)) ||
                		Character.isWhitespace(prefix.charAt(prefix.length() - 1))) {
                	result.add(noPrefixLine.trim());
                	
                	break;
                }
            }
        }

        return result;
    }

	private static List<String> cleanDirectivesFromComments(Collection<String> prefixes) {
        List<String> resultPrefixes = new ArrayList<String>();

        for (String prefix : prefixes) {
            if (prefix.startsWith("//")) {
                resultPrefixes.add(StringUtil.trimStart(prefix, prefix.substring(0, 2)));
            }
            else {
                resultPrefixes.add(prefix);
            }
        }

        return resultPrefixes;
    }
	
    private static List<String> fileNonEmptyCommentedLines(String fileText) {
        List<String> result = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new StringReader(fileText));
            try {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("//")) {
                        String uncommentedLine = line.substring(2).trim();
                        if (!uncommentedLine.isEmpty()) {
                            result.add(uncommentedLine);
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch(IOException e) {
            throw new AssertionError(e);
        }

        return result;
    }
}
