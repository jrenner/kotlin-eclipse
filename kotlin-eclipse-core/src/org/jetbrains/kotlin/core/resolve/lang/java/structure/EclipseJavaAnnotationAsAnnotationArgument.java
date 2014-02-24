package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotationAsAnnotationArgument;

public class EclipseJavaAnnotationAsAnnotationArgument extends EclipseJavaAnnotationArgument<IAnnotation> implements JavaAnnotationAsAnnotationArgument {

    protected EclipseJavaAnnotationAsAnnotationArgument(IAnnotation javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public JavaAnnotation getAnnotation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
