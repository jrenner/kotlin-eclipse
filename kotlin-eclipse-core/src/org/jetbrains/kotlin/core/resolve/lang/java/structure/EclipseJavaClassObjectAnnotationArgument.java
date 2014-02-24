package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassObjectAnnotationArgument;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;

public class EclipseJavaClassObjectAnnotationArgument extends EclipseJavaAnnotationArgument<IAnnotation>
    implements JavaClassObjectAnnotationArgument {

    protected EclipseJavaClassObjectAnnotationArgument(IAnnotation javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public JavaType getReferencedType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
