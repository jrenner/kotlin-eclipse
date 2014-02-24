package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IJavaElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaElement;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaElementImpl;

public abstract class EclipseJavaElement<T extends IJavaElement> implements JavaElement {
    
    private final T javaElement;
    
    protected EclipseJavaElement(@NotNull T javaElement) {
        this.javaElement = javaElement;
    }
    
    @NotNull
    public T getEclipseElement() {
        return javaElement;
    }
    
    @Override
    public int hashCode() {
        return getEclipseElement().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JavaElementImpl && getEclipseElement().equals(((EclipseJavaElement<?>) obj).getEclipseElement());
    }
}
