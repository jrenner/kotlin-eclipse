package org.jetbrains.kotlin.core.launch;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.jetbrains.jet.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.core.builder.KotlinManager;
import org.jetbrains.kotlin.core.utils.ProjectUtils;
import org.osgi.framework.Bundle;

public class LaunchConfigurationDelegate extends JavaLaunchDelegate {

    private static String ktHome = "";
    
    static {
        Bundle bundle = Platform.getBundle("org.jetbrains.kotlin.ui");
        Path iconPath = new Path("icons/sample.gif");
        try {
            Path path = new Path(FileLocator.resolve(FileLocator.find(bundle, iconPath, null)).getPath());
            
            ktHome = path.removeLastSegments(3).toPortableString() + "/kotlin-bundled-compiler/";
        } catch (IOException e) {
        }
    }
    private final String ktCompiler = "kotlin-compiler-0.5.162.jar";
     
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        List<IFile> projectFiles = KotlinManager.getFilesByProject(configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null));
        if (projectFiles == null) {
            System.out.println("Wrong configuration"); // TODO: ask for better solution
            
            return;
        }
        
        compileKotlinFiles(projectFiles, configuration);
        
        super.launch(configuration, mode, launch, monitor);
    }
    
    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        String packageClassName = null;
        try {
            packageClassName = getPackageClassName(configuration);
        } catch (IllegalArgumentException e) {
            abort(LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_Main_type_not_specified_11, null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE);
        }
        
        return packageClassName;
    }
    
    private String getPackageClassName(ILaunchConfiguration configuration) {
        String mainClass = "";
        try {
            mainClass = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        try {
            for (IFile file : KotlinManager.getFilesByProject((configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null)))) {
                if (file.getName().equals(mainClass)) {
                    if (ProjectUtils.getMainClass(Arrays.asList(file)) != null) {
                        return ProjectUtils.createPackageClassName(file);
                    }
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
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
            System.out.println("Exception: " + e.getMessage());
            
            abort("Build error", null, 0);
        }
    }
    
    private String getOutputDir(ILaunchConfiguration configuration) {
        try {
            String[] cp = getClasspath(configuration);
            if (cp.length > 0)
                return cp[0];
        } catch (CoreException e) {
            //swallow
        }
        
        return ".";
    }
}