package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import java.nio.channels.IllegalSelectorException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.BinaryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.Visibility;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassifierType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaField;
import org.jetbrains.jet.lang.resolve.java.structure.JavaMethod;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeParameter;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.kotlin.core.log.KotlinLogger;

import com.google.common.collect.Lists;

public class EclipseJavaClass extends EclipseJavaClassifier<IType> implements JavaClass {

    public EclipseJavaClass(IType javaElement) {
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
    @NotNull
    public Name getName() {
        return Name.identifier(getEclipseElement().getElementName());
    }

    @Override
    public boolean isAbstract() {
        try {
            return Flags.isAbstract(getEclipseElement().getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isStatic() {
        try {
            return Flags.isStatic(getEclipseElement().getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isFinal() {
        try {
            return Flags.isFinal(getEclipseElement().getFlags());
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @NotNull
    public Visibility getVisibility() {
        return EclipseJavaElementUtil.getVisibility(getEclipseElement());
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
    public Collection<JavaClass> getInnerClasses() {
        try {
            IType[] innerTypes = getEclipseElement().getTypes();
            List<JavaClass> eclipseJavaTypes = Lists.newArrayList();
            
            for (IType type : innerTypes) {
                eclipseJavaTypes.add(new EclipseJavaClass(type));
            }
            
            return eclipseJavaTypes;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Nullable
    public FqName getFqName() {
        return new FqName(getEclipseElement().getFullyQualifiedName());
    }

    @Override
    public boolean isInterface() {
        try {
            return getEclipseElement().isInterface();
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    public boolean isAnnotationType() {
        try {
            return getEclipseElement().isAnnotation();
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    public boolean isEnum() {
        try {
            return getEclipseElement().isEnum();
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return false;
    }

    @Override
    @Nullable
    public JavaClass getOuterClass() {
        IType outerClass = getEclipseElement().getDeclaringType();
        if (outerClass == null) return null;
        
        return new EclipseJavaClass(outerClass);
    }

    @Override
    @NotNull
    public Collection<JavaClassifierType> getSupertypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Collection<JavaMethod> getMethods() {
        try {
            IMethod[] methods = getEclipseElement().getMethods();
            List<JavaMethod> eclipseJavaMethods = Lists.newArrayList();
            
            for (IMethod method : methods) {
                eclipseJavaMethods.add(new EclipseJavaMethod(method));
            }
            
            return eclipseJavaMethods;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    @NotNull
    public Collection<JavaMethod> getAllMethods() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Collection<JavaField> getFields() {
        try {
            IField[] fields = getEclipseElement().getFields();
            List<JavaField> eclipseJavaFields = Lists.newArrayList();
            
            for (IField field : fields) {
                eclipseJavaFields.add(new EclipseJavaField(field));
            }
            
            return eclipseJavaFields;
        } catch (JavaModelException e) {
            KotlinLogger.logAndThrow(e);
            throw new IllegalSelectorException();
        }
    }

    @Override
    @NotNull
    public Collection<JavaField> getAllFields() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Collection<JavaMethod> getConstructors() {
        Collection<JavaMethod> constructors = Lists.newArrayList();
        Collection<JavaMethod> allMethods = getAllMethods();
        for (JavaMethod method : allMethods) {
            if (method.isConstructor()) {
                constructors.add(method);
            }
        }
        
        return constructors;
    }

    @Override
    @NotNull
    public JavaClassifierType getDefaultType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public OriginKind getOriginKind() {
        IType javaType = getEclipseElement();
        
        if (javaType instanceof BinaryType) {
            return OriginKind.COMPILED;
        } else {
            return OriginKind.SOURCE;
        }
    }

}
