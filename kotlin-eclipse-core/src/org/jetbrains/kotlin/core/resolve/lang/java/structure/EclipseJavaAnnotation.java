package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotationArgument;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.kotlin.core.log.KotlinLogger;

import com.google.common.collect.Lists;

public class EclipseJavaAnnotation extends EclipseJavaElement<IType> implements JavaAnnotation {

    protected EclipseJavaAnnotation(IAnnotation javaAnnotation) {
        super((IType) javaAnnotation.getParent());
    }

    @Override
    @Nullable
    public JavaAnnotationArgument findArgument(@NotNull Name name) {
        try {
            IAnnotation[] annotations = getEclipseElement().getAnnotations();
            
            for (IAnnotation annotation : annotations) {
                if (name.asString().equals(annotation.getElementName())) {
                    return EclipseJavaAnnotationArgument.create(annotation);
                }
            }
            
            return null;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @NotNull
    public Collection<JavaAnnotationArgument> getArguments() {
        try {
            IAnnotation[] annotations = getEclipseElement().getAnnotations();
            List<JavaAnnotationArgument> javaAnnotationArguments = Lists.newArrayList();
            
            for (IAnnotation annotation : annotations) {
                javaAnnotationArguments.add(EclipseJavaAnnotationArgument.create(annotation));
            }
            
            return javaAnnotationArguments;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Nullable
    public FqName getFqName() {
        return new FqName(getEclipseElement().getElementName());
    }

}
