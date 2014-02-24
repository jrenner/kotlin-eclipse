package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaMethod;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeParameter;
import org.jetbrains.jet.lang.resolve.java.structure.JavaValueParameter;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.log.KotlinLogger;

import com.google.common.collect.Lists;

public class EclipseJavaMethod extends EclipseJavaMember<IMethod> implements JavaMethod  {

    protected EclipseJavaMethod(IMethod psiMember) {
        super(psiMember);
    }

    @Override
    @NotNull
    public List<JavaTypeParameter> getTypeParameters() {
        try {
            ITypeParameter[] typeParameters = getEclipseElement().getTypeParameters();
            List<JavaTypeParameter> eclipseJavaTypeParameters = Lists.newArrayList();
            
            for (ITypeParameter typeParameter: typeParameters) {
                eclipseJavaTypeParameters.add(new EclipseJavaTypeParameter(typeParameter));
            }
            
            return eclipseJavaTypeParameters;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @NotNull
    public List<JavaValueParameter> getValueParameters() {
        try {
            ILocalVariable[] parameters = getEclipseElement().getParameters();
            List<JavaValueParameter> eclipseJavaParameters = Lists.newArrayList();
            
            for (ILocalVariable parameter : parameters) {
                eclipseJavaParameters.add(new EclipseJavaValueParameter(parameter));
            }
            
            return eclipseJavaParameters;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean hasAnnotationParameterDefaultValue() {
        try {
            return getEclipseElement().getDefaultValue() != null;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Nullable
    public JavaType getReturnType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isVararg() {
        try {
            ILocalVariable[] parameters = getEclipseElement().getParameters();
            if (parameters.length == 0) return false;
            
            return Flags.isVarargs(parameters[parameters.length - 1].getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isConstructor() {
        try {
            return getEclipseElement().isConstructor();
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
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

}
