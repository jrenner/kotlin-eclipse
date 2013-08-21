package org.jetbrains.kotlin.core.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.cli.jvm.JVMConfigurationKeys;
import org.jetbrains.jet.cli.jvm.compiler.JetCoreEnvironment;
import org.jetbrains.jet.config.CommonConfigurationKeys;
import org.jetbrains.jet.config.CompilerConfiguration;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.kotlin.core.launch.LaunchConfigurationDelegate;
import org.jetbrains.kotlin.core.log.KotlinLogger;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

public class KotlinEnvironment {
    
    
    private static final Disposable DISPOSABLE = new Disposable() {
        
        @Override
        public void dispose() {
        }
    };
    
    private static final Map<IJavaProject, KotlinEnvironment> cachedEnvironment = new HashMap<>();
    
    private final IJavaProject javaProject;
    
    private static final Object cacheEnvironmentLock = new Object();
    private final JetCoreEnvironment jetCoreEnvironment;
    
    private KotlinEnvironment(@NotNull IJavaProject javaProject) {
        this.javaProject = javaProject;
        
        CompilerConfiguration configuration = new CompilerConfiguration();
        
        addJreClasspath(configuration);
        addKotlinRuntime(configuration);
        addSourcesToClasspath(configuration);
        addLibsToClasspath(configuration);
        
        jetCoreEnvironment = new JetCoreEnvironment(DISPOSABLE, configuration);
        
        synchronized (cacheEnvironmentLock) {
            cachedEnvironment.put(javaProject, this);   
        }
    }
    
    public static void updateKotlinEnvironment(IJavaProject javaProject) {
        synchronized (cacheEnvironmentLock) {
            cachedEnvironment.put(javaProject, new KotlinEnvironment(javaProject));
        }
    }
    
    @NotNull
    public static KotlinEnvironment getEnvironmentLazy(IJavaProject javaProject) {
        synchronized (cacheEnvironmentLock) {
            if (!cachedEnvironment.containsKey(javaProject)) {
                cachedEnvironment.put(javaProject, new KotlinEnvironment(javaProject));
            }
            
            return cachedEnvironment.get(javaProject);
        }            
    }
    
    public static Project getCachedIdeaProject(IJavaProject javaProject) {
        synchronized (cacheEnvironmentLock) {
            return getEnvironmentLazy(javaProject).getProject();
        }
    }
    
    @Nullable
    public JetFile getJetFile(@NotNull IFile file) {
        List<JetFile> sourceFiles = jetCoreEnvironment.getSourceFiles();
        for (JetFile jetFile : sourceFiles) {
            if (jetFile.getName().equals(file.getName())) {
                return jetFile;
            }
        }
        
        return null;
    }
    
    private void addLibsToClasspath(CompilerConfiguration configuration) {
        try {
            List<File> libDirectories = ProjectUtils.getLibDirectories(javaProject);
            for (File libDirectory : libDirectories) {
//                addToClasspath(libDirectory);
                configuration.add(JVMConfigurationKeys.CLASSPATH_KEY, libDirectory);
            }
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
    }
    
    private void addSourcesToClasspath(CompilerConfiguration configuration) {
        try {
            List<File> srcDirectories = ProjectUtils.getSrcDirectories(javaProject);
            for (File srcDirectory : srcDirectories) {
//                addToClasspath(srcDirectory);
                configuration.add(CommonConfigurationKeys.SOURCE_ROOTS_KEY, srcDirectory.getAbsolutePath());
                configuration.add(JVMConfigurationKeys.CLASSPATH_KEY, srcDirectory);
            }
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        } 
    }
    
    private void addKotlinRuntime(CompilerConfiguration configuration) {
        //            addToClasspath(new File(LaunchConfigurationDelegate.KT_RUNTIME_PATH));
        configuration.add(JVMConfigurationKeys.CLASSPATH_KEY, new File(LaunchConfigurationDelegate.KT_RUNTIME_PATH));
    }

    private void addJreClasspath(CompilerConfiguration configuration) {
        try {
            IRuntimeClasspathEntry computeJREEntry = JavaRuntime.computeJREEntry(javaProject);
            if (computeJREEntry == null) {
                return;
            }
            
            IRuntimeClasspathEntry[] jreEntries = JavaRuntime.resolveRuntimeClasspathEntry(computeJREEntry,
                    javaProject);

            if (jreEntries.length != 0) {
                for (IRuntimeClasspathEntry jreEntry : jreEntries) {
//                    addToClasspath(jreEntry.getClasspathEntry().getPath().toFile());
                    configuration.add(JVMConfigurationKeys.CLASSPATH_KEY, jreEntry.getClasspathEntry().getPath().toFile());
                }
                
                return;
            }
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        } catch (CoreException e) {
            KotlinLogger.logAndThrow(e);
        }
    }
    
    @NotNull
    public Project getProject() {
        return jetCoreEnvironment.getProject();
    }
}