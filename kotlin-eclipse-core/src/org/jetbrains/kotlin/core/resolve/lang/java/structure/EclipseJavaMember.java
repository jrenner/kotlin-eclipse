package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.descriptors.Visibility;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaMember;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.kotlin.core.log.KotlinLogger;

public abstract class EclipseJavaMember<T extends IMember> extends EclipseJavaElement<T> implements JavaMember {

    protected EclipseJavaMember(@NotNull T javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public JavaClass getContainingClass() {
        return new EclipseJavaClass(getEclipseElement().getTypeRoot().findPrimaryType());
    }

    @Override
    public boolean isAbstract() {
        try {
            return Flags.isAbstract(getEclipseElement().getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    public boolean isStatic() {
        try {
            return Flags.isStatic(getEclipseElement().getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    public boolean isFinal() {
        try {
            return Flags.isFinal(getEclipseElement().getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    @NotNull
    public Visibility getVisibility() {
        return EclipseJavaElementUtil.getVisibility(getEclipseElement());
    }

    @Override
    @NotNull
    public Name getName() {
        return Name.identifier(getEclipseElement().getElementName());
    }
}
