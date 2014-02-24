package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.IType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeProvider;
import org.jetbrains.jet.lang.resolve.java.structure.JavaWildcardType;

public class EclipseJavaWildcardType extends EclipseJavaType<IType> implements JavaWildcardType {

    public EclipseJavaWildcardType(IType javaType) {
        super(javaType);
    }

    @Override
    @Nullable
    public JavaType getBound() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isExtends() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @NotNull
    public JavaTypeProvider getTypeProvider() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
