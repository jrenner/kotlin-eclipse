package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.Visibilities;
import org.jetbrains.jet.lang.descriptors.Visibility;
import org.jetbrains.jet.lang.resolve.java.JavaVisibilities;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.log.KotlinLogger;

import com.google.common.collect.Lists;

public class EclipseJavaElementUtil {
    
    @NotNull
    public static Visibility getVisibility(@NotNull IMember javaMember) {
        try {
            int flags = javaMember.getFlags();
            if (Flags.isPublic(flags)) {
                return Visibilities.PUBLIC;
            } else if (Flags.isPrivate(flags)) {
                return Visibilities.PRIVATE;
            } else if (Flags.isProtected(flags)) {
                return Flags.isStatic(flags) ? JavaVisibilities.PROTECTED_STATIC_VISIBILITY : JavaVisibilities.PROTECTED_AND_PACKAGE;
            }
            
            return JavaVisibilities.PACKAGE_VISIBILITY;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new RuntimeException(e);
        }
    }
    
    @NotNull
    public static Collection<JavaAnnotation> getAnnotations(@NotNull IAnnotatable javaAnnotatable) {
        try {
            IAnnotation[] annotations = javaAnnotatable.getAnnotations();
            
            List<JavaAnnotation> eclipseJavaAnnotations = Lists.newArrayList();
            for (IAnnotation annotation : annotations) {
                eclipseJavaAnnotations.add(new EclipseJavaAnnotation(annotation));
            }
            
            return eclipseJavaAnnotations;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }
    
    @Nullable
    public static JavaAnnotation findAnnotation(@NotNull IAnnotatable javaAnnotatable, @NotNull FqName fqName) {
        IAnnotation annotation = javaAnnotatable.getAnnotation(fqName.asString());
        return new EclipseJavaAnnotation(annotation);
    }
}
