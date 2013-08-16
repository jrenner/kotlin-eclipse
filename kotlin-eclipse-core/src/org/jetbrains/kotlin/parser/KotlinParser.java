package org.jetbrains.kotlin.parser;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.parsing.JetParser;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.plugin.JetLanguage;
import org.jetbrains.kotlin.core.utils.KotlinEnvironment;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.psi.PsiFile;

public class KotlinParser {

    private final File file;
    
    private ASTNode tree;
    private final KotlinEnvironment kotlinEnvironment;
    
    public KotlinParser(File file, IJavaProject javaProject) {
        this.file = file;
        this.tree = null;
        kotlinEnvironment = KotlinEnvironment.getEnvironmentForTempFile(javaProject);
    }
    
    public KotlinParser(@NotNull IFile iFile) {
        this(new File(iFile.getRawLocation().toOSString()), JavaCore.create(iFile.getProject()));
    }
    
    @NotNull
    public static ASTNode parse(@NotNull IFile iFile) {
        return new KotlinParser(iFile).parse();
    }
    
    public static PsiFile getPsiFile(@NotNull IFile file) {
        return new KotlinParser(file).getPsiFile();
    }
    
    @NotNull
    public ASTNode parse() {
        JetParser jetParser = new JetParser(kotlinEnvironment.getProject());
        tree = jetParser.parse(null, createPsiBuilder(getNode()), getPsiFile());
        
        return tree;
    }
    
    @NotNull
    private PsiBuilder createPsiBuilder(ASTNode chameleon) {
        return PsiBuilderFactory.getInstance().createBuilder(kotlinEnvironment.getProject(), chameleon, null, 
                JetLanguage.INSTANCE, chameleon.getChars());
    }
    
    @Nullable
    private PsiFile getPsiFile() {
        return kotlinEnvironment.getJetFile(file);
    }
    
    @Nullable
    private ASTNode getNode() {
        JetFile jetFile = (JetFile) getPsiFile();
        if (jetFile != null) {
            return jetFile.getNode();
        }
        
        return null;
    }
}
