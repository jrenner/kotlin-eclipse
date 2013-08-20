package org.jetbrains.kotlin.ui.formatter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.jet.lexer.JetTokens;
import org.jetbrains.kotlin.utils.IndenterUtil;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;

public class AlignmentStrategy {
    
    private final PsiFile parsedFile;
    private StringBuilder edit;
    
    public static final Set<String> blockElementTypes;
    
    static {
        blockElementTypes = new HashSet<String>(Arrays.asList("IF", "FOR", "WHILE", "FUN", "CLASS", "FUNCTION_LITERAL_EXPRESSION", "PROPERTY", "WHEN"));
    }
    
    public AlignmentStrategy(PsiFile parsedFile) {
        this.parsedFile = parsedFile;
    }
    
    public String placeSpaces() {
        edit = new StringBuilder();
        buildFormattedCode(parsedFile, 0);
        
        return edit.toString();
    }
    
    public static String alignCode(PsiFile parsedFile) {
        return new AlignmentStrategy(parsedFile).placeSpaces();
    }
    
    private void buildFormattedCode(PsiElement node, int indent) {
        indent = updateIndent(node, indent);
        for (PsiElement child : node.getChildren()) {
            if (child instanceof LeafPsiElement) {
                if (IndenterUtil.isNewLine((LeafPsiElement) child)) {
                    int shift = indent;
                    if (isBrace(child.getNextSibling())) {
                        shift--;
                    }
                    
                    edit.append(IndenterUtil.createWhiteSpace(shift, IndenterUtil.getLineSeparatorsOccurences(child.getText())));
                } else {
                    edit.append(child.getText());
                }
            }
            buildFormattedCode(child, indent);
        }
    }
    
    private boolean isBrace(PsiElement psiElement) {
        LeafPsiElement leafPsiElement = getFirstLeaf(psiElement);
        if (leafPsiElement != null) {
            IElementType elementType = leafPsiElement.getElementType();
            if (elementType == JetTokens.LBRACE || elementType == JetTokens.RBRACE) {
                return true;
            }
        }
        
        return false;   
    }
    
    private LeafPsiElement getFirstLeaf(PsiElement psiElement) {
        PsiElement child = psiElement;
        while (true) {
            if (child instanceof LeafPsiElement || child == null) {
                return (LeafPsiElement) child;
            }
            child = child.getFirstChild();
        }
    }
    
    private int updateIndent(PsiElement node, int curIndent) {
        if (blockElementTypes.contains(node.getNode().getElementType().toString())) {
            return curIndent + 1;
        } 
        
        return curIndent;
    }
}