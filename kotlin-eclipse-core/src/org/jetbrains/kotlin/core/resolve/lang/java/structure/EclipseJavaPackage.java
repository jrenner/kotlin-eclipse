package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaPackage;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.log.KotlinLogger;

import com.google.common.collect.Lists;

public class EclipseJavaPackage extends EclipseJavaElement<IPackageFragment> implements JavaPackage {

    public EclipseJavaPackage(IPackageFragment javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public Collection<JavaClass> getClasses() {
        try {
            List<JavaClass> javaClasses = Lists.newArrayList();
            IJavaElement[] children = getEclipseElement().getChildren();
            for (IJavaElement child : children) {
                if (child instanceof IType) {
                    javaClasses.add(new EclipseJavaClass((IType) child));
                }
            }
            
            return javaClasses;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @NotNull
    public Collection<JavaPackage> getSubPackages() {
        try {
            List<JavaPackage> javaClasses = Lists.newArrayList();
            IJavaElement[] children = getEclipseElement().getChildren();
            for (IJavaElement child : children) {
                if (child instanceof IPackageFragment) {
                    javaClasses.add(new EclipseJavaPackage((IPackageFragment) child));
                }
            }
            
            return javaClasses;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @NotNull
    public FqName getFqName() {
        return new FqName(getEclipseElement().getElementName());
    }

}
