package org.jetbrains.kotlin.core.utils;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.resolve.java.PackageClassUtils;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.plugin.JetMainDetector;
import org.jetbrains.kotlin.core.builder.KotlinManager;

import com.intellij.core.JavaCoreApplicationEnvironment;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;

public class ProjectUtils {
    
    public static IFile getMainClass() {
        getMainClass(KotlinManager.getAllFiles());
        
        return null;
    }
    
    public static IFile getMainClass(Collection<IFile> files) {
        KotlinEnvironment kotlinEnvironment = new KotlinEnvironment();
        JavaCoreApplicationEnvironment applicationEnvironment = kotlinEnvironment.getApplicationEnvironment();
        Project ideaProject = kotlinEnvironment.getProject();
        
        for (IFile file : files) {
            VirtualFile fileByPath = applicationEnvironment.getLocalFileSystem().findFileByPath(file.getRawLocation().toOSString());
            JetFile jetFile = (JetFile) PsiManager.getInstance(ideaProject).findFile(fileByPath);
            if (JetMainDetector.hasMain(jetFile.getDeclarations())) {
                return file;
            }
        }
                
        return null;
    }
    
    public static String getPackageByFile(IFile file) {
        ASTNode parsedFile = KotlinManager.getParsedFile(file);
        
        ASTNode namespaceHeader = null;
        for (ASTNode child : parsedFile.getChildren(null)) {
            if (child.getElementType().toString() == "NAMESPACE_HEADER") {
                namespaceHeader = child;
            }
        }
        
        StringBuilder packageName = new StringBuilder("");
        for (ASTNode child : namespaceHeader.getChildren(null)) {
            if (child.getElementType().toString() == "REFERENCE_EXPRESSION") {
                if (packageName.length() > 0) {
                    packageName.append(".");
                }
                packageName.append(child.getText());
            }
        }
        
        return packageName.toString();
    }
    
    public static String createPackageClassName(IFile file) {
        String packageName = ProjectUtils.getPackageByFile(file);
        
        String packageClassName = PackageClassUtils.getPackageClassName(new FqName(packageName)); 
        if (!packageName.equals("")) {
            packageClassName = packageName + "." + packageClassName;
        }
        
        return packageClassName;
    }
    
    public static String getMainPackage() {
        IFile mainClass = getMainClass();
        if (mainClass != null) {
            return getPackageByFile(mainClass);
        }
        
        return "";
    }
}
