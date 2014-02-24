package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaPrimitiveType;

public class EclipseJavaPrimitiveType extends EclipseJavaType<IType> implements JavaPrimitiveType {

    public EclipseJavaPrimitiveType(IType javaType) {
        super(javaType);
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return getEclipseElement().getElementName();
    }
}
