package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaArrayType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;

public class EclipseJavaArrayType extends EclipseJavaType<IType> implements JavaArrayType {

    public EclipseJavaArrayType(IType javaType) {
        super(javaType);
    }

    @Override
    @NotNull
    public JavaType getComponentType() {
        return EclipseJavaType.create(getEclipseElement());
    }

}
