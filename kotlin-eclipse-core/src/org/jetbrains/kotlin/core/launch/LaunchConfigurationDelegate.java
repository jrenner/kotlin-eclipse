package org.jetbrains.kotlin.core.launch;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.jetbrains.jet.cli.jvm.K2JVMCompiler;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.builder.KotlinManager;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.core.utils.ProjectUtils;
import org.osgi.framework.Bundle;

public class LaunchConfigurationDelegate extends JavaLaunchDelegate {

    private final String ktCompiler = "kotlin-compiler-0.5.162.jar";
    private final static String ktHome = getKtHome();
    
    private static String getKtHome() {
        Bundle bundle = Platform.getBundle("org.jetbrains.kotlin.ui");
        Path uiPluginPath = new Path("plugin.xml");
        try {
            Path path = new Path(FileLocator.resolve(FileLocator.find(bundle, uiPluginPath, null)).getPath());
            
            return path.removeLastSegments(2).toPortableString() + "/kotlin-bundled-compiler/";
        } catch (IOException e) {
            KotlinLogger.logError("Could not find path to ui plugin.xml", e);
        }
        
        return null;
    }
    
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
        
        List<IFile> projectFiles = KotlinManager.getFilesByProject(projectName);
        if (projectFiles == null) {
            abort("Project name is invalid: " + projectName, null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_PROJECT);
            
            return;
        }
        
        compileKotlinFiles(projectFiles, configuration);
        
        super.launch(configuration, mode, launch, monitor);
    }
    
    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        try {
            return getPackageClassName(configuration).toString();
        } catch (IllegalArgumentException e) {
            abort("File with main method not defined", null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE);
        }
        
        return null;
    }
    
    private FqName getPackageClassName(ILaunchConfiguration configuration) {
        String mainClass = "";
        try {
            mainClass = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null);
        } catch (CoreException e) {
            KotlinLogger.logError("Unspecified name of main class in configuration", e);
        }
        
        try {
            String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
            for (IFile file : KotlinManager.getFilesByProject(projectName)) {
                if (file.getName().equals(mainClass)) {
                    if (ProjectUtils.hasMain(file)) {
                        return ProjectUtils.createPackageClassName(file);
                    }
                }
            }
        } catch (CoreException e) {
            KotlinLogger.logError(e);
        }
        
        throw new IllegalArgumentException();
    }
    
    private void compileKotlinFiles(List<IFile> files, ILaunchConfiguration configuration) throws CoreException {
        StringBuilder command = new StringBuilder();
        command.append("java -cp " + ktHome + "lib/" + ktCompiler);
        command.append(" " + K2JVMCompiler.class.getCanonicalName());
        command.append(" -kotlinHome " + ktHome);

        command.append(" -src");
        for (IFile file : files) {
            command.append(" " + file.getRawLocation().toOSString());
        }
        
        command.append(" -output " + getOutputDir(configuration));
        try {
            Runtime.getRuntime().exec(command.toString()).waitFor();
        } catch (IOException | InterruptedException e) {
            KotlinLogger.logError("Could not execute kotlin compiler", e);
            
            abort("Build error", null, 0);
        }
    }
    
    private String getOutputDir(ILaunchConfiguration configuration) {
        try {
            String[] cp = getClasspath(configuration);
            if (cp.length > 0)
                return cp[0];
        } catch (CoreException e) {
            KotlinLogger.logError(e);
        }
        
        return ".";
    }
}