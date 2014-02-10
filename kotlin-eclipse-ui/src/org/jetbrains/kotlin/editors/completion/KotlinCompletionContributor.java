package org.jetbrains.kotlin.editors.completion;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.CallableDescriptor;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.descriptors.ReceiverParameterDescriptor;
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.scopes.JetScope;
import org.jetbrains.jet.lang.resolve.scopes.JetScopeUtils;
import org.jetbrains.jet.lang.types.JetType;
import org.jetbrains.jet.lang.types.expressions.ExpressionTypingUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class KotlinCompletionContributor {
    
    @NotNull
    public static Collection<DeclarationDescriptor> getVariantsNoReceiver(
            @Nullable JetSimpleNameExpression expression,
            @NotNull BindingContext context) {
        JetScope resolutionScope = context.get(BindingContext.RESOLUTION_SCOPE, expression);
        if (resolutionScope == null) {
            return Collections.emptySet();
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
