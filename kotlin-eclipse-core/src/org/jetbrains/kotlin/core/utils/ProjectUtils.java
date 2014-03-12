/*******************************************************************************
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************************/
package org.jetbrains.kotlin.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.java.PackageClassUtils;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.plugin.MainFunctionDetector;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;

public class ProjectUtils {
    
    @Nullable
    public static IFile getMainClass(@NotNull Collection<IFile> files, @NotNull BindingContext bindingContext) {
        MainFunctionDetector mainFunctionDetector = new MainFunctionDetector(bindingContext);
        for (IFile file : files) {
            JetFile jetFile = (JetFile) KotlinPsiManager.INSTANCE.getParsedFile(file);
            if (mainFunctionDetector.hasMain(jetFile.getDeclarations())) {
                return file;
            }
        }
                
        return null;
    }
    
    public static IJavaProject getJavaProjectFromCollection(Collection<IFile> files) {
        IJavaProject javaProject = null;
        for (IFile file : files) {
            javaProject = JavaCore.create(file.getProject());
            break;
        }
        
        return javaProject;
    }
    
    public static boolean hasMain(@NotNull IFile file, @NotNull BindingContext bindingContext) {
        return getMainClass(Collections.singleton(file), bindingContext) != null;
    }
    
    @Nullable
    public static String getPackageByFile(IFile file) {
        JetFile jetFile = (JetFile) KotlinPsiManager.INSTANCE.getParsedFile(file);
        
        assert jetFile != null;
        
        return jetFile.getPackageName();
    }
    
    public static FqName createPackageClassName(IFile file) {
        String filePackage = getPackageByFile(file);
        if (filePackage == null) {
            return null;
        }
        return PackageClassUtils.getPackageClassFqName(new FqName(filePackage));
    }
    
    @NotNull
    public static List<File> getSrcDirectories(@NotNull IJavaProject javaProject) throws JavaModelException {
        List<File> srcDirectories = new ArrayList<File>();

        IClasspathEntry[] classPathEntries = javaProject.getRawClasspath();
        IWorkspaceRoot root = javaProject.getProject().getWorkspace().getRoot();
        for (IClasspathEntry classPathEntry : classPathEntries) {
            if (classPathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                IPath classPathEntryPath = classPathEntry.getPath();
                IResource classPathResource = root.findMember(classPathEntryPath);
                String path;
                if (classPathResource == null) {
                    path = classPathEntryPath.toOSString();
                } else {
                    path = classPathResource.getLocation().toOSString();
                }
                
                if (!path.isEmpty()) {
                    srcDirectories.add(new File(path));
                }
            }
        }
        
        return srcDirectories;
    }
    
    @NotNull
    public static List<File> getLibDirectories(@NotNull IJavaProject javaProject) throws JavaModelException {
        List<File> libDirectories = new ArrayList<File>();
        
        IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(false);
        IPath rootDirectory = javaProject.getProject().getLocation();
        String projectName = rootDirectory.lastSegment();
        
        for (IClasspathEntry classpathEntry : classpathEntries) {
            if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                String classpath = classpathEntry.getPath().toPortableString();
                File file = new File(classpath);
                
                if (!file.isAbsolute()) {
                    if (classpathEntry.getPath().segment(0).equals(projectName)) {
                        file = new File(rootDirectory.removeLastSegments(1).toPortableString() + classpath);
                    } else {
                        file = new File(rootDirectory.toPortableString() + classpath);
                    }
                }
                
                libDirectories.add(file);
            }
        }
        
        return libDirectories;
    }
}