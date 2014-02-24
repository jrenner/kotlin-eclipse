package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;

import org.eclipse.jdt.core.ITypeParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassifierType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeParameter;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeParameterListOwner;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeProvider;
import org.jetbrains.jet.lang.resolve.name.Name;

public class EclipseJavaTypeParameter extends EclipseJavaClassifier<ITypeParameter> implements JavaTypeParameter {

    protected EclipseJavaTypeParameter(ITypeParameter javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public Name getName() {
        return Name.identifier(getEclipseElement().getElementName());
    }

    @Override
    public int getIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @NotNull
    public Collection<JavaClassifierType> getUpperBounds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public JavaTypeParameterListOwner getOwner() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @NotNull
    public JavaType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public JavaTypeProvider getTypeProvider() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
