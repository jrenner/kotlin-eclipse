package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ILocalVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaValueParameter;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;

public class EclipseJavaValueParameter extends EclipseJavaElement<ILocalVariable> implements JavaValueParameter {

    protected EclipseJavaValueParameter(ILocalVariable javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public Collection<JavaAnnotation> getAnnotations() {
        return EclipseJavaElementUtil.getAnnotations(getEclipseElement());
    }

    @Override
    @Nullable
    public JavaAnnotation findAnnotation(@NotNull FqName fqName) {
        return EclipseJavaElementUtil.findAnnotation(getEclipseElement(), fqName);
    }

    @Override
    @Nullable
    public Name getName() {
        return Name.identifier(getEclipseElement().getElementName());
    }

    @Override
    @NotNull
    public JavaType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVararg() {
        return Flags.isVarargs(getEclipseElement().getFlags());
    }
}
