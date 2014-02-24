package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IAnnotation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaElement;
import org.jetbrains.jet.lang.resolve.java.structure.JavaReferenceAnnotationArgument;

public class EclipseJavaReferenceAnnotationArgument extends EclipseJavaAnnotationArgument<IAnnotation>
        implements JavaReferenceAnnotationArgument {

    protected EclipseJavaReferenceAnnotationArgument(IAnnotation javaElement) {
        super(javaElement);
    }

    @Override
    @Nullable
    public JavaElement resolve() {
        // TODO Auto-generated method stub
        return null;
    }
}
