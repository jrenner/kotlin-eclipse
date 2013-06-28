package org.jetbrains.kotlin.ui.launch;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.jetbrains.kotlin.core.builder.KotlinManager;

public class KotlinMainLauncherTab extends JavaMainTab implements ILaunchConfigurationTab {
    
    @Override
    public void handleSearchButtonSelected() {
        ListDialog dialog = new ListDialog(getShell());
        dialog.setBlockOnOpen(true);
        dialog.setMessage("Select a Kotlin file to run");
        dialog.setTitle("Choose Kotlin Compilation Unit");
        dialog.setContentProvider(new ArrayContentProvider());
        dialog.setLabelProvider(new JavaUILabelProvider());

        List<IFile> projectFiles = KotlinManager.getFilesByProject(fProjText.getText());
        if (projectFiles == null) {
            dialog.setInput(KotlinManager.getAllFiles());
        }
        dialog.setInput(projectFiles);
        
        if (dialog.open() == Window.CANCEL) {
            return;
        }
        
        Object[] results = dialog.getResult();
        if (results == null || results.length == 0) {
            return;
        }
        
        if (results[0] instanceof IFile) {
            fMainText.setText(((IFile) results[0]).getName());
        }
    }
}