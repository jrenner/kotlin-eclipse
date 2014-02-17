package org.jetbrains.kotlin.ui.editors.completion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.CallableDescriptor;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.descriptors.PackageViewDescriptor;
import org.jetbrains.jet.lang.descriptors.ReceiverParameterDescriptor;
import org.jetbrains.jet.lang.psi.JetCallExpression;
import org.jetbrains.jet.lang.psi.JetExpression;
import org.jetbrains.jet.lang.psi.JetImportDirective;
import org.jetbrains.jet.lang.psi.JetPackageDirective;
import org.jetbrains.jet.lang.psi.JetQualifiedExpression;
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.calls.autocasts.AutoCastUtils;
import org.jetbrains.jet.lang.resolve.calls.autocasts.DataFlowInfo;
import org.jetbrains.jet.lang.resolve.scopes.JetScope;
import org.jetbrains.jet.lang.resolve.scopes.JetScopeUtils;
import org.jetbrains.jet.lang.resolve.scopes.receivers.ExpressionReceiver;
import org.jetbrains.jet.lang.resolve.scopes.receivers.ReceiverValue;
import org.jetbrains.jet.lang.types.JetType;
import org.jetbrains.jet.lang.types.PackageType;
import org.jetbrains.jet.lang.types.expressions.ExpressionTypingUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiElement;

public class KotlinCompletionProvider {
    
    //Source code is taken from org.jetbrains.jet.plugin.codeInsight.TipsManager
    @NotNull
    public static Collection<DeclarationDescriptor> getReferenceVariants(
            @NotNull JetSimpleNameExpression expression,
            @NotNull BindingContext context
    ) {
        JetExpression receiverExpression = expression.getReceiverExpression();
        PsiElement parent = expression.getParent();
        boolean inPositionForCompletionWithReceiver = parent instanceof JetCallExpression || parent instanceof JetQualifiedExpression;
        if (receiverExpression != null && inPositionForCompletionWithReceiver) {
            // Process as call expression
            JetScope resolutionScope = context.get(BindingContext.RESOLUTION_SCOPE, expression);
            JetType expressionType = context.get(BindingContext.EXPRESSION_TYPE, receiverExpression);

            if (expressionType != null && resolutionScope != null && !expressionType.isError()) {
                if (!(expressionType instanceof PackageType)) {
                    ExpressionReceiver receiverValue = new ExpressionReceiver(receiverExpression, expressionType);
                    Set<DeclarationDescriptor> descriptors = new HashSet<DeclarationDescriptor>();

                    DataFlowInfo info = context.get(BindingContext.NON_DEFAULT_EXPRESSION_DATA_FLOW, expression);
                    if (info == null) {
                        info = DataFlowInfo.EMPTY;
                    }

                    List<ReceiverValue> variantsForExplicitReceiver = AutoCastUtils.getAutoCastVariants(receiverValue, context, info);

                    for (ReceiverValue descriptor : variantsForExplicitReceiver) {
                        descriptors.addAll(includeExternalCallableExtensions(
                                excludePrivateDescriptors(descriptor.getType().getMemberScope().getAllDescriptors()),
                                resolutionScope, descriptor));
                    }

                    return descriptors;
                }

                return includeExternalCallableExtensions(
                        excludePrivateDescriptors(expressionType.getMemberScope().getAllDescriptors()),
                        resolutionScope, new ExpressionReceiver(receiverExpression, expressionType));
            }
            return Collections.emptyList();
        }
        else {
            return getVariantsNoReceiver(expression, context);
        }
    }
    
    private static Set<DeclarationDescriptor> includeExternalCallableExtensions(
            @NotNull Collection<DeclarationDescriptor> descriptors,
            @NotNull JetScope externalScope,
            @NotNull final ReceiverValue receiverValue
    ) {
        // It's impossible to add extension function for package
        JetType receiverType = receiverValue.getType();
        if (receiverType instanceof PackageType) {
            return new HashSet<DeclarationDescriptor>(descriptors);
        }

        Set<DeclarationDescriptor> descriptorsSet = Sets.newHashSet(descriptors);

        descriptorsSet.addAll(
                Collections2.filter(JetScopeUtils.getAllExtensions(externalScope),
                                    new Predicate<CallableDescriptor>() {
                                        @Override
                                        public boolean apply(CallableDescriptor callableDescriptor) {
                                            return ExpressionTypingUtils.checkIsExtensionCallable(receiverValue, callableDescriptor);
                                        }
                                    }));

        return descriptorsSet;
    }
    
    @NotNull
    private static Collection<DeclarationDescriptor> getVariantsNoReceiver(
            @NotNull JetSimpleNameExpression expression,
            @NotNull BindingContext context) {
        JetScope resolutionScope = context.get(BindingContext.RESOLUTION_SCOPE, expression);
        if (resolutionScope == null) {
            return Collections.emptySet();
        }
        
        if (expression.getParent() instanceof JetImportDirective || expression.getParent() instanceof JetPackageDirective) {
            return excludeNonPackageDescriptors(resolutionScope.getAllDescriptors());
        }

        Collection<DeclarationDescriptor> descriptors = Sets.newHashSet();

        List<ReceiverParameterDescriptor> result = resolutionScope.getImplicitReceiversHierarchy();
        for (ReceiverParameterDescriptor receiverDescriptor : result) {
            JetType receiverType = receiverDescriptor.getType();
            descriptors.addAll(receiverType.getMemberScope().getAllDescriptors());
        }
        
        descriptors.addAll(resolutionScope.getAllDescriptors());
        
        return excludeNotCallableExtensions(excludePrivateDescriptors(descriptors), resolutionScope);
    }
    
    @NotNull
    private static Collection<DeclarationDescriptor> excludeNonPackageDescriptors(
            @NotNull Collection<DeclarationDescriptor> descriptors
    ) {
        return Collections2.filter(descriptors, new Predicate<DeclarationDescriptor>() {
            @Override
            public boolean apply(DeclarationDescriptor declarationDescriptor) {
                if (declarationDescriptor instanceof PackageViewDescriptor) {
                    // Heuristic: we don't want to complete "System" in "package java.lang.Sys",
                    // so we find class of the same name as package, we exclude this package
                    PackageViewDescriptor parent = ((PackageViewDescriptor) declarationDescriptor).getContainingDeclaration();
                    if (parent != null) {
                        JetScope parentScope = parent.getMemberScope();
                        return parentScope.getClassifier(declarationDescriptor.getName()) == null;
                    }
                    return true;
                }
                return false;
            }
        });
    }
    
    @NotNull
    private static Collection<DeclarationDescriptor> excludePrivateDescriptors(@NotNull Collection<DeclarationDescriptor> descriptors) {
        return Collections2.filter(descriptors, new Predicate<DeclarationDescriptor>() {
            @Override
            public boolean apply(@Nullable DeclarationDescriptor descriptor) {
                if (descriptor == null) {
                    return false;
                }

                return true;
            }
        });
    }
    
    @NotNull
    private static Collection<DeclarationDescriptor> excludeNotCallableExtensions(
            @NotNull Collection<? extends DeclarationDescriptor> descriptors, @NotNull JetScope scope) {
        Set<DeclarationDescriptor> descriptorsSet = Sets.newHashSet(descriptors);

        final List<ReceiverParameterDescriptor> result = scope.getImplicitReceiversHierarchy();

        descriptorsSet.removeAll(
                Collections2.filter(JetScopeUtils.getAllExtensions(scope), new Predicate<CallableDescriptor>() {
                    @Override
                    public boolean apply(CallableDescriptor callableDescriptor) {
                        if (callableDescriptor.getReceiverParameter() == null) {
                            return false;
                        }
                        
                        for (ReceiverParameterDescriptor receiverDescriptor : result) {
                            if (ExpressionTypingUtils.checkIsExtensionCallable(receiverDescriptor.getValue(), callableDescriptor)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }));

        return Lists.newArrayList(descriptorsSet);
    }
}