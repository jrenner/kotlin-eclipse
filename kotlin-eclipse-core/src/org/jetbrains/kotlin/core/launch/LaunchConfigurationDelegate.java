package org.jetbrains.kotlin.core.launch;

import java.io.IOException;
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
import org.jetbrains.jet.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.core.builder.KotlinManager;

public class LaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {    
    
    // TODO: Change to independent system
    private final String ktHome = "C:/Users/Mikhail.Zarechenskiy/projects/kotlin-eclipse/kotlin-bundled-compiler/"; 
    private final String ktCompiler = "kotlin-compiler-0.5.162.jar";
     
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        compileKotlinFiles(KotlinManager.getAllFiles().toArray(new IFile[KotlinManager.getAllFiles().size()]), configuration);

        // TODO: launch generated class file
            
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
            Runtime.getRuntime().exec(command.toString()).waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
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