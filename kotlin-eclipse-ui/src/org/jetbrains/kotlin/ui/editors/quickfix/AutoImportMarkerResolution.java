package org.jetbrains.kotlin.ui.editors.quickfix;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.ui.editors.KotlinEditor;
import org.jetbrains.kotlin.utils.EditorUtil;
import org.jetbrains.kotlin.utils.IndenterUtil;
import org.jetbrains.kotlin.utils.LineEndUtil;

import com.intellij.lang.ASTNode;

public class AutoImportMarkerResolution implements IMarkerResolution2 {

    private static final String NAMESPACE_HEADER = "NAMESPACE_HEADER";
    private static final String IMPORT_DIRECTIVE = "IMPORT_DIRECTIVE";
    
    private final IType type;
    
    public AutoImportMarkerResolution(IType type) {
        this.type = type;
    }

    @Override
    public String getLabel() {
        return "Import '" + type.getElementName() + 
                "' (" + type.getPackageFragment().getElementName() + ")";
    }

    @Override
    public void run(IMarker marker) {
        KotlinEditor editor = getActiveEditor();
        if (editor == null) {
            return;
        }
        
        IDocument document = editor.getViewer().getDocument();
        
        String breakLineBefore = "";
        String breakLineAfter = "";
        ASTNode node = findNodeToNewImport(EditorUtil.getFile(editor));
        if (node != null) {
            if (NAMESPACE_HEADER.equals(node.getElementType().toString())) {
                if (!node.getText().isEmpty()) {
                    breakLineBefore = System.lineSeparator() + System.lineSeparator();
                } 
                ASTNode nextNode = node.getTreeNext();
                int countLineBreaks = IndenterUtil.getLineSeparatorsOccurences(nextNode.getText());
                if (countLineBreaks == 0) {
                    breakLineAfter = System.lineSeparator() + System.lineSeparator();
                }
                if (countLineBreaks == 1) {
                    breakLineAfter = System.lineSeparator();
                }
            } else if (IMPORT_DIRECTIVE.equals(node.getElementType().toString())) {
                breakLineBefore = System.lineSeparator();
            }
        } else {
            breakLineAfter = System.lineSeparator() + System.lineSeparator();
        }
        
        int offset = 0;
        if (node != null) {
            ASTNode parsedFile = KotlinPsiManager.INSTANCE.getParsedFile(EditorUtil.getFile(editor));
            offset = LineEndUtil.convertLfToOsOffset(parsedFile.getText(), node.getTextRange().getEndOffset());
        }
        
        String newImport = "import " + type.getFullyQualifiedName('.');
        newImport = breakLineBefore + newImport + breakLineAfter;
        try {
            document.replace(offset, 0, newImport);
            if (marker != null) {
                marker.delete();
            }
        } catch (BadLocationException e) {
            KotlinLogger.logError(e);
        } catch (CoreException e) {
            KotlinLogger.logError(e);
        }
    }
    
    @Override
    public String getDescription() {
        return "Some description";
    }

    @Override
    public Image getImage() {
        return null;
    }
    
    private ASTNode findNodeToNewImport(IFile file) {
        ASTNode node = findChildNode(file, IMPORT_DIRECTIVE);
        if (node == null) {
            node = findChildNode(file, NAMESPACE_HEADER);
        }
        
        return node;
    }
    
    @Nullable
    private ASTNode findChildNode(IFile file, @NotNull String elementType) {
        ASTNode parsedFile = KotlinPsiManager.INSTANCE.getParsedFile(file);
        ASTNode node = null;
        for (ASTNode child : parsedFile.getChildren(null)) {
            if (elementType.equals(child.getElementType().toString())) {
                node = child;
            }
        }
        
        return node;
    }
    
    private KotlinEditor getActiveEditor() {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        
        if (workbenchWindow == null) {
            return null;
        }
        
        return (KotlinEditor) workbenchWindow.getActivePage().getActiveEditor();
    }
}
