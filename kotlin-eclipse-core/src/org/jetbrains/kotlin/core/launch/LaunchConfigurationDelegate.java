package org.jetbrains.kotlin.core.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.jetbrains.jet.cli.jvm.K2JVMCompiler;
import org.jetbrains.jet.lang.resolve.java.PackageClassUtils;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.builder.KotlinManager;
import org.jetbrains.kotlin.core.utils.ProjectUtils;

public class LaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {    
    
    // TODO: Change to independent system
    private final String ktHome = "C:/Users/Mikhail.Zarechenskiy/projects/kotlin-eclipse/kotlin-bundled-compiler/"; 
    private final String ktCompiler = "kotlin-compiler-0.5.162.jar";
    private final String defaultPackage = "_DefaultPackage"; 
     
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        compileKotlinFiles(KotlinManager.getAllFiles().toArray(new IFile[KotlinManager.getAllFiles().size()]), configuration);
        
        IVMRunner runner = getVMRunner(configuration, mode);
        
        String packageClassName = ProjectUtils.getMainPackage();
        if (packageClassName.equals("")) {
            packageClassName = defaultPackage;
        } else {
            packageClassName += "." + PackageClassUtils.getPackageClassName(new FqName(packageClassName));
        }
        
        VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(packageClassName, new String[] {getOutputDir(configuration)});
        runner.run(vmConfig, launch, monitor);
        
        monitor.done();
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
            System.out.println("Building...");
            
            Process process = Runtime.getRuntime().exec(command.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            
            System.out.println("Done.");
            
            process.waitFor();
            //Runtime.getRuntime().exec(command.toString()).waitFor();
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
    
    private String getClassToLaunch(ILaunchConfiguration configuration) throws CoreException {
        return verifyMainTypeName(configuration);
    }
    
    private String[] getClassPath(ILaunchConfiguration configuration) throws CoreException {
        List<String> paths = new ArrayList<String>();
        
        for (String string : getClasspath(configuration)) {
            System.out.println("cp: " + string);
            paths.add(string);
        }
                
        String[] result = new String[paths.size()];
        return paths.toArray(result);
    }
    
}