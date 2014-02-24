package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaLiteralAnnotationArgument;
import org.jetbrains.jet.lang.resolve.name.Name;

public class EclipseJavaLiteralAnnotationArgument implements JavaLiteralAnnotationArgument {
    
    private final Name name;
    private final Object value;

    public EclipseJavaLiteralAnnotationArgument(@Nullable Name name, @Nullable Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    @Nullable
    public Name getName() {
        return name;
    }

    @Override
    @Nullable
    public Object getValue() {
        return value;
    }

}
