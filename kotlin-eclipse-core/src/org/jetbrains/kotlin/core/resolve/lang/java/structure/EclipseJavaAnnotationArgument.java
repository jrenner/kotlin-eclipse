package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotationArgument;
import org.jetbrains.jet.lang.resolve.name.Name;

public abstract class EclipseJavaAnnotationArgument<T extends IAnnotation> extends EclipseJavaElement<T> implements JavaAnnotationArgument {

    protected EclipseJavaAnnotationArgument(T javaElement) {
        super(javaElement);
    }
    
    @NotNull
    static EclipseJavaAnnotationArgument<?> create(@NotNull IAnnotation javaAnnotation) {
        // TODO: add implementation
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Nullable
    public Name getName() {
        return Name.identifier(getEclipseElement().getElementName());
    }
}
