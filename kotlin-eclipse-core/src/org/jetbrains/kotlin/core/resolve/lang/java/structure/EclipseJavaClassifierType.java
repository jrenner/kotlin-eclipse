package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassifier;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassifierType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeSubstitutor;

public class EclipseJavaClassifierType extends EclipseJavaType<IType> implements JavaClassifierType {

    public EclipseJavaClassifierType(IType javaType) {
        super(javaType);
    }

    @Override
    @Nullable
    public JavaClassifier getClassifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @NotNull
    public JavaTypeSubstitutor getSubstitutor() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Collection<JavaClassifierType> getSupertypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public String getPresentableText() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRaw() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @NotNull
    public List<JavaType> getTypeArguments() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
