package org.jetbrains.kotlin.ui.launch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.jetbrains.jet.plugin.JetFileType;
import org.jetbrains.kotlin.core.utils.ProjectUtils;
import org.jetbrains.kotlin.ui.editors.KotlinEditor;

public class KotlinLaunchShortcut implements ILaunchShortcut {

    @Override
    public void launch(ISelection selection, String mode) {
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        
        List<IFile> files = new LinkedList<IFile>();
        for (Object object : structuredSelection.toList()) {
            if (object instanceof IAdaptable) {
                IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
                addFiles(files, resource);
            }
        }
        
        IFile mainClass = ProjectUtils.getMainClass(files);
        
        if (mainClass == null) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Wrong configuration", "Chosen files does not consist main class");
            
            return;
        }
        
        launchWithMainClass(mainClass, mode);
    }
    
    private void launchWithMainClass(IFile mainClass, String mode) {
        ILaunchConfiguration configuration = findLaunchConfiguration(DebugPlugin.getDefault().getLaunchManager()
                .getLaunchConfigurationType("org.jetbrains.kotlin.core.launch.launchConfigurationType"), mainClass);
        
        if (configuration == null) {
            configuration = createConfiguration(mainClass);
        } 
        
        if (configuration == null) {
            return; // MessageDialog.
        } 
        
        try {
            configuration.launch(mode, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    private ILaunchConfiguration createConfiguration(IFile file) {
        ILaunchConfiguration configuration = null;
        ILaunchConfigurationWorkingCopy configWC = null;
        ILaunchConfigurationType configurationType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.jetbrains.kotlin.core.launch.launchConfigurationType");
        
        try {
            configWC = configurationType.newInstance(null, "New configuration - " + file.getName()); // change configuration name?
            configWC.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, file.getName());
            configWC.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
            
            configuration = configWC.doSave();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        return configuration;
    }
    
    private ILaunchConfiguration findLaunchConfiguration(ILaunchConfigurationType configurationType, IFile mainClass) {
        try {
            ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configurationType);
            for (ILaunchConfiguration config : configs) {
                if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null).equals(mainClass.getName())) {
                    return config;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void addFiles(List<IFile> files, IResource resource) {
        switch (resource.getType()) {
            case IResource.FILE:
                IFile file = (IFile) resource;
                if (resource.getFileExtension().equals(JetFileType.INSTANCE.getDefaultExtension())) {
                    files.add(file);
                }
                
                break;
            case IResource.FOLDER:
            case IResource.PROJECT:
                IContainer container = (IContainer) resource;
                try {
                    for (IResource child : container.members()) {
                        addFiles(files, child);
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
                
                break;
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        if (editor instanceof KotlinEditor) {
            IFile file = null;
            
            IEditorInput editorInput = editor.getEditorInput();
            if (editorInput instanceof IFileEditorInput) {
                file = ((IFileEditorInput) editorInput).getFile();
            } else {
                return;
            }
            
            IFile mainClass = ProjectUtils.getMainClass(Arrays.asList(file));
            if (mainClass == null) {
                MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Wrong configuration", "This file does not consist main method");
                
                return;
            }
            
            launchWithMainClass(file, mode);
        }
    }

}
