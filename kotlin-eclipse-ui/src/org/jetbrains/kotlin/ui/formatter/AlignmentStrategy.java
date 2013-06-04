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
    private MultiTextEdit edit = new MultiTextEdit();
    
    private static final Set<String> blockElementTypes;
    
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
    
    private void buildFormattedCode(ASTNode node, int curIndent) {
        System.out.println("Indent: " + curIndent);
        System.out.println("Text: " + node.toString());
        for (ASTNode child : node.getChildren(null)) {
            PsiElement psiElement = child.getPsi();
            
            if (psiElement != null && psiElement instanceof LeafPsiElement) {
                IElementType elementType = ((LeafPsiElement) psiElement).getElementType();
                
                if (elementType == JetTokens.WHITE_SPACE && child.getText().contains("\n")) {
                    int occur = child.getText().length() - child.getText().replace("\n", "").length();
                    edit.addChild(new InsertEdit(0, createLevelingString(curIndent, occur)));
                } else {
                    edit.addChild(new InsertEdit(0, child.getText()));
                }
            }
            curIndent = updateIndent(node);
            buildFormattedCode(child, curIndent);
        }
    }
    
    private String createLevelingString(int curIndent, int countBreakLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < countBreakLines; ++i) {
            stringBuilder.append(System.lineSeparator());
        }
        for (int i = 0; i < curIndent; ++i) {
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }
    
    private int updateIndent(ASTNode node) {
        ASTNode parent = node.getTreeParent();
        int depth = 0;
        while (parent != null) {
            if (blockElementTypes.contains(parent.getElementType().toString())) {
                depth++;
            }
            parent = parent.getTreeParent();
        }
        
        return depth * defaultIndent;
    }
}
