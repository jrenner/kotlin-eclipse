package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IJavaElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassifier;

public abstract class EclipseJavaClassifier<T extends IJavaElement> extends EclipseJavaElement<T> implements JavaClassifier {

    protected EclipseJavaClassifier(@NotNull T javaElement) {
        super(javaElement);
    }
}
