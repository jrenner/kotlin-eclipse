package org.jetbrains.kotlin.core.launch;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.jetbrains.jet.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.core.builder.KotlinManager;
import org.jetbrains.kotlin.core.utils.ProjectUtils;
import org.osgi.framework.Bundle;

public class LaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {    

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
        
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        
        compileKotlinFiles(KotlinManager.getAllFiles().toArray(new IFile[KotlinManager.getAllFiles().size()]), configuration);
        
        IVMRunner runner = getVMRunner(configuration, mode);
        
        try {
            String packageClassName = getPackageClassName(configuration);
            VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(packageClassName, new String[] {getOutputDir(configuration)});
            runner.run(vmConfig, launch, monitor);
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong configuration");
        }
        
        monitor.done();
    }
    
    private String getPackageClassName(ILaunchConfiguration configuration) {
        String mainClass = "";
        try {
            mainClass = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        for (IFile file : KotlinManager.getAllFiles()) {
            if (file.getName().equals(mainClass)) {
                if (ProjectUtils.getMainClass(Arrays.asList(file)) != null) {
                    return ProjectUtils.createPackageClassName(file);
                }
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    private void compileKotlinFiles(IFile[] files, ILaunchConfiguration configuration) {
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
        }        
        
        System.out.println(command);
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