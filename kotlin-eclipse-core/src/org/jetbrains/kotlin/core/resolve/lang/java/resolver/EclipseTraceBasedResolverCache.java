/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.core.resolve.lang.java.resolver;

import static org.jetbrains.jet.lang.resolve.BindingContext.CLASS;
import static org.jetbrains.jet.lang.resolve.BindingContext.COMPILE_TIME_INITIALIZER;
import static org.jetbrains.jet.lang.resolve.BindingContext.CONSTRUCTOR;
import static org.jetbrains.jet.lang.resolve.BindingContext.FQNAME_TO_CLASS_DESCRIPTOR;
import static org.jetbrains.jet.lang.resolve.BindingContext.VARIABLE;

import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.ClassDescriptor;
import org.jetbrains.jet.lang.descriptors.ConstructorDescriptor;
import org.jetbrains.jet.lang.descriptors.PropertyDescriptor;
import org.jetbrains.jet.lang.descriptors.SimpleFunctionDescriptor;
import org.jetbrains.jet.lang.resolve.BindingContextUtils;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.CompileTimeConstantUtils;
import org.jetbrains.jet.lang.resolve.constants.CompileTimeConstant;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaResolverCache;
import org.jetbrains.jet.lang.resolve.java.resolver.ResolverPackage;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaElement;
import org.jetbrains.jet.lang.resolve.java.structure.JavaField;
import org.jetbrains.jet.lang.resolve.java.structure.JavaMethod;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaClassImpl;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaElementImpl;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaFieldImpl;
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaMethodImpl;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.resolve.lang.java.structure.EclipseJavaClass;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;

public class EclipseTraceBasedResolverCache implements JavaResolverCache {
    private BindingTrace trace;

    @Inject
    public void setTrace(BindingTrace trace) {
        this.trace = trace;
    }

    @Nullable
    @Override
    public ClassDescriptor getClassResolvedFromSource(@NotNull FqName fqName) {
        return trace.get(FQNAME_TO_CLASS_DESCRIPTOR, fqName.toUnsafe());
    }

    @Nullable
    @Override
    public ClassDescriptor getClass(@NotNull JavaClass javaClass) {
        return trace.get(CLASS, ((EclipseJavaClass) javaClass).getEclipseElement());
    }

    @Override
    public void recordMethod(@NotNull JavaMethod method, @NotNull SimpleFunctionDescriptor descriptor) {
        BindingContextUtils.recordFunctionDeclarationToDescriptor(trace, ((JavaMethodImpl) method).getPsi(), descriptor);
    }

    @Override
    public void recordConstructor(@NotNull JavaElement element, @NotNull ConstructorDescriptor descriptor) {
        trace.record(CONSTRUCTOR, ((JavaElementImpl) element).getPsi(), descriptor);
    }

    @Override
    public void recordField(@NotNull JavaField field, @NotNull PropertyDescriptor descriptor) {
        PsiField psiField = ((JavaFieldImpl) field).getPsi();
        trace.record(VARIABLE, psiField, descriptor);

        if (!descriptor.isVar()) {
            PsiExpression initializer = psiField.getInitializer();
            Object evaluatedExpression = JavaConstantExpressionEvaluator.computeConstantExpression(initializer, false);
            if (evaluatedExpression != null) {
                CompileTimeConstant<?> constant =
                        ResolverPackage.resolveCompileTimeConstantValue(evaluatedExpression,
                                                                        CompileTimeConstantUtils.isPropertyCompileTimeConstant(descriptor),
                                                                        descriptor.getType());
                if (constant != null) {
                    trace.record(COMPILE_TIME_INITIALIZER, descriptor, constant);
                }
            }
        }
    }

    @Override
    public void recordClass(@NotNull JavaClass javaClass, @NotNull ClassDescriptor descriptor) {
        trace.record(CLASS, ((JavaClassImpl) javaClass).getPsi(), descriptor);
    }
}
