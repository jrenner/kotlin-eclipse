package org.jetbrains.kotlin.ui.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class WordDetector implements IWordDetector {
    
    private char previous = Character.SPACE_SEPARATOR;
    
    @Override
    public boolean isWordStart(char c) {
        if (Character.isLetter(previous) || Character.isDigit(previous)) {
            previous = c;
            
            return false;
        }
        previous = c;
        
        return isKotlinIdentifierStart(c);
    }

    @Override
    public boolean isWordPart(char c) {
        return Character.isJavaIdentifierPart(c);
    }
    
    private boolean isKotlinIdentifierStart(char c) {
        return Character.isLetter(c);
    }
}
