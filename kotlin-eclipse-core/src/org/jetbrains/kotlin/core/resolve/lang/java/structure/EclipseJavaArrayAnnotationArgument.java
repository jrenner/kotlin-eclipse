package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotationArgument;
import org.jetbrains.jet.lang.resolve.java.structure.JavaArrayAnnotationArgument;

public class EclipseJavaArrayAnnotationArgument extends EclipseJavaAnnotationArgument<IAnnotation> implements JavaArrayAnnotationArgument {

    protected EclipseJavaArrayAnnotationArgument(IAnnotation javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public List<JavaAnnotationArgument> getElements() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

}
