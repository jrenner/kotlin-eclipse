package org.jetbrains.kotlin.core.utils;

import org.eclipse.core.resources.IFile;
import org.jetbrains.jet.lang.psi.JetNamedFunction;
import org.jetbrains.jet.plugin.JetMainDetector;
import org.jetbrains.kotlin.core.builder.KotlinManager;

import com.intellij.lang.ASTNode;

public class ProjectUtils {
    
    public static IFile getMainClass() {
        for (IFile file : KotlinManager.getAllFiles()) {
            if (hasMain(KotlinManager.getParsedFile(file))) {
                System.out.println("Main class: " + file.getName());
                return file;
            }
        }
        
        return null;
    }
    
    private static boolean hasMain(ASTNode parsedFile) {
        for (ASTNode child : parsedFile.getChildren(null)) {
            if (child.getElementType().toString() == "FUN") {
                if (JetMainDetector.isMain(new JetNamedFunction(child))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static String getPackageByFile(IFile file) {
        ASTNode parsedFile = KotlinManager.getParsedFile(file);
        
        ASTNode namespaceHeader = null;
        for (ASTNode child : parsedFile.getChildren(null)) {
            System.out.println(child.getElementType());
            if (child.getElementType().toString() == "NAMESPACE_HEADER") {
                namespaceHeader = child;
            }
        }
        
        StringBuilder packageName = new StringBuilder();
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
    
    public static String getMainPackage() {
        IFile mainClass = getMainClass();
        if (mainClass != null) {
            return getPackageByFile(mainClass);
        }
        
        return "";
    }
}
