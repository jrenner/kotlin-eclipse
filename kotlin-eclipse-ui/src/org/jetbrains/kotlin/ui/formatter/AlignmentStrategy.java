package org.jetbrains.kotlin.ui.formatter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.jetbrains.jet.lexer.JetTokens;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;

public class AlignmentStrategy {
    
    private final int defaultIndent = 4;
    
    private final ASTNode parsedFile;
    private MultiTextEdit edit;
    
    private static final Set<String> blockElementTypes;
    private static final String lineSeparator = "\n";
    private static final char spaceSeparator = ' ';
    
    static {
        blockElementTypes = new HashSet<String>(Arrays.asList("IF", "FOR", "WHILE", "FUN", "CLASS", "FUNCTION_LITERAL_EXPRESSION", "PROPERTY"));
    }
    
    public AlignmentStrategy(ASTNode parsedFile) {
        this.parsedFile = parsedFile;
    }
    
    public IDocument placeSpaces() {
        IDocument document = new Document();
        edit = new MultiTextEdit();
        buildFormattedCode(parsedFile, 0);
        try {
            edit.apply(document);
        } catch (MalformedTreeException | BadLocationException e) {
            e.printStackTrace();
        }
        
        return document;
    }
    
    private void buildFormattedCode(ASTNode node, int indent) {
        indent = updateIndent(node, indent);
        for (ASTNode child : node.getChildren(null)) {
            PsiElement psiElement = child.getPsi();
            
            if (psiElement instanceof LeafPsiElement) {
                if (isNewLine((LeafPsiElement) psiElement)) {
                    int occur = getLineSeparatorsOccurences(psiElement.getText());
                    
                    int shift = indent;
                    if (isBrace(psiElement.getNextSibling())) {
                        shift -= defaultIndent;
                    }
                    
                    edit.addChild(new InsertEdit(0, createLevelingString(shift, occur)));
                } else {
                    edit.addChild(new InsertEdit(0, psiElement.getText()));
                }
            }
            buildFormattedCode(child, indent);
        }
    }
    
    private boolean isNewLine(LeafPsiElement psiElement) {
        return psiElement.getElementType() == JetTokens.WHITE_SPACE && psiElement.getText().contains(lineSeparator);
    }
    
    private int getLineSeparatorsOccurences(String text) {
        return text.length() - text.replace(lineSeparator, "").length();
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
    
    private String createLevelingString(int curIndent, int countBreakLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < countBreakLines; ++i) {
            stringBuilder.append(System.lineSeparator());
        }
        for (int i = 0; i < curIndent; ++i) {
            stringBuilder.append(spaceSeparator);
        }

        return stringBuilder.toString();
    }
    
    private int updateIndent(ASTNode node, int curIndent) {
        if (blockElementTypes.contains(node.getElementType().toString())) {
            return curIndent + defaultIndent;
        } 
        
        return curIndent;
    }
}