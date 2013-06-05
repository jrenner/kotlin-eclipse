package org.jetbrains.kotlin.ui.editors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jetbrains.kotlin.core.builder.KotlinManager;
import org.jetbrains.kotlin.ui.formatter.AlignmentStrategy;

public class KotlinCodeFormatter extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        KotlinEditor editor = (KotlinEditor) HandlerUtil.getActiveEditor(event);
        IFile iFile = ((IFileEditorInput) editor.getEditorInput()).getFile();
        
        editor.getDocumentProvider().getDocument(HandlerUtil.getActiveEditorInput(event)).set(
                AlignmentStrategy.alignCode(KotlinManager.getParsedFile(iFile)));
        
        return null;
    }

}
