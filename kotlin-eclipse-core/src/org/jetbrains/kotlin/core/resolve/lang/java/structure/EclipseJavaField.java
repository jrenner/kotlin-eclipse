package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaField;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.log.KotlinLogger;

public class EclipseJavaField extends EclipseJavaMember<IField> implements JavaField {

    protected EclipseJavaField(IField javaField) {
        super(javaField);
    }

    @Override
    public boolean isEnumEntry() {
        try {
            return getEclipseElement().isEnumConstant();
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    @NotNull
    public JavaType getType() {
//        TODO: Add getType() method
        throw new UnsupportedOperationException();
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
