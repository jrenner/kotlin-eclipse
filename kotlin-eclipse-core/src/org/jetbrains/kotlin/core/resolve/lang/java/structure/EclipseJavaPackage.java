package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaPackage;
import org.jetbrains.jet.lang.resolve.name.FqName;

public class EclipseJavaPackage extends EclipseJavaElement<IJavaElement> implements JavaPackage {

    public EclipseJavaPackage(IJavaElement javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public Collection<JavaClass> getClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Collection<JavaPackage> getSubPackages() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public FqName getFqName() {
        return new FqName(getEclipseElement().getElementName());
    }

}
