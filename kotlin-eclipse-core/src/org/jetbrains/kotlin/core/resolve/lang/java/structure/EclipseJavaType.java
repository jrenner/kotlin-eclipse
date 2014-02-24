package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaTypeImpl;

public class EclipseJavaType<T extends IType> implements JavaType {
    
    private final T javaType;
    
    public EclipseJavaType(@NotNull T javaType) {
        this.javaType = javaType;
    }
    
    public static EclipseJavaType<?> create(@NotNull IType type) {
        return null;
    }
    
    @NotNull
    public T getEclipseElement() {
        return javaType;
    }
    
    @Override
    public int hashCode() {
        return getEclipseElement().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JavaTypeImpl && getEclipseElement().equals(((EclipseJavaType<?>) obj).getEclipseElement());
    }
}
