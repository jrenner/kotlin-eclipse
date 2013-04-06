package org.jetbrains.kotlin.utils;

public class LineEndUtil {
    public static int convertLfToCrLfOffset(String lfText, int lfOffset) {
        // In CrLf move to new line takes 2 char instead of 1 in Lf
        return lfOffset + offsetToLineNumber(lfText, lfOffset);
    }

    public static int offsetToLineNumber(String lfText, int offset) {
        int line = 0;
        int curOffset = 0;
        
        while (curOffset < offset) {
            if (curOffset == lfText.length()) {
                break;
            }
            
            char c = lfText.charAt(curOffset);
            if (c == '\n') {
                line++;
            } else if (c == '\r') {
                throw new IllegalArgumentException("Given text shouldn't contain \\r char");
            }
            
            curOffset++;
        }
        
        return line;
    }
}
