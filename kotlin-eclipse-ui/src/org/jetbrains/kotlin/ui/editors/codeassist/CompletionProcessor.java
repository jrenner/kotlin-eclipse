/*******************************************************************************
 * Copyright 2000-2014 JetBrains s.r.o.
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
 *
 *******************************************************************************/
package org.jetbrains.kotlin.ui.editors.codeassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.psi.JetClass;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.psi.JetFunction;
import org.jetbrains.jet.lang.psi.JetPackageDirective;
import org.jetbrains.jet.lang.psi.JetProperty;
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.BindingContextUtils;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;
import org.jetbrains.kotlin.ui.builder.KotlinBuilder;
import org.jetbrains.kotlin.ui.editors.KeywordManager;
import org.jetbrains.kotlin.ui.editors.completion.KotlinCompletionContributor;
import org.jetbrains.kotlin.ui.editors.templates.KotlinApplicableTemplateContext;
import org.jetbrains.kotlin.ui.editors.templates.KotlinDocumentTemplateContext;
import org.jetbrains.kotlin.ui.editors.templates.KotlinTemplateManager;
import org.jetbrains.kotlin.utils.EditorUtil;
import org.jetbrains.kotlin.utils.LineEndUtil;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public class CompletionProcessor implements IContentAssistProcessor, ICompletionListener {
     
    /**
     * Characters for auto activation proposal computation.
     */
    private static final char[] VALID_PROPOSALS_CHARS = new char[] { '.' };
    private static final char[] VALID_INFO_CHARS = new char[] { '(', ',' };
    
    private final JavaEditor editor;
    
    public CompletionProcessor(JavaEditor editor) {
        this.editor = editor;
    }
    
    /**
     * A very simple context which invalidates information after typing several
     * chars.
     */
    private static class KotlinContextValidator implements IContextInformationValidator {
        private int initialOffset;
        
        @Override
        public void install(IContextInformation info, ITextViewer viewer, int offset) {
            this.initialOffset = offset;
        }

        @Override
        public boolean isContextInformationValid(int offset) {
            return Math.abs(initialOffset - offset) < 1;
        }
    }
    
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        String fileText = viewer.getDocument().get();
        
        KotlinPsiManager.INSTANCE.updatePsiFile(EditorUtil.getFile(editor), fileText);
        
        int identOffset = getIdentifierStartOffset(fileText, offset);
        Assert.isTrue(identOffset <= offset);
        
        String identifierPart = fileText.substring(identOffset, offset);
        
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        
        proposals.addAll(generateKeywordProposals(viewer, identOffset, offset, identifierPart));
        proposals.addAll(generateTemplateProposals(viewer, offset, identifierPart));
        proposals.addAll(generateSimpleCompletionProposals(viewer, identOffset, offset, identifierPart));
        
        return proposals.toArray(new ICompletionProposal[proposals.size()]);
    }
    
    @NotNull
    private Collection<ICompletionProposal> generateSimpleCompletionProposals(@NotNull ITextViewer viewer, int identOffset, 
            int offset, @NotNull String identifierPart) {
        IFile file = EditorUtil.getFile(editor);
        
        JetSimpleNameExpression simpleNameExpression = getSimpleNameExpression(file, identOffset);
        if (simpleNameExpression == null) {
            return Collections.emptyList();
        }
        
        IJavaProject javaProject = JavaCore.create(file.getProject());
        BindingContext context = KotlinBuilder.analyzeProjectInForeground(javaProject);
        
        Collection<DeclarationDescriptor> declarationDescriptors = KotlinCompletionContributor.getReferenceVariants(simpleNameExpression, context);
        
        List<ICompletionProposal> proposals = Lists.newArrayList();
        for (DeclarationDescriptor descriptor : declarationDescriptors) {
            String completion = descriptor.getName().asString();
            PsiElement psiElement = BindingContextUtils.descriptorToDeclaration(context, descriptor);
            
            if (completion.startsWith(identifierPart)) {
                proposals.add(new CompletionProposal(completion, identOffset, offset - identOffset, completion.length(), getImage(psiElement), null, null, null));
            }
        }
        
        return proposals;
    }
    
    @Nullable
    private static Image getImage(@Nullable PsiElement element) {
        if (element == null) return null;
        
        String imageName = null;
        if (element instanceof JetClass) {
            if (((JetClass) element).isTrait()) {
                imageName = ISharedImages.IMG_OBJS_INTERFACE;
            } else {
                imageName = ISharedImages.IMG_OBJS_CLASS;
            }
        } else if (element instanceof JetPackageDirective) {
            imageName = ISharedImages.IMG_OBJS_PACKAGE;
        } else if (element instanceof JetFunction) {
            imageName = ISharedImages.IMG_OBJS_PUBLIC;
        } else if (element instanceof JetProperty) {
            imageName = ISharedImages.IMG_FIELD_PUBLIC;
        }
        
        if (imageName != null) {
            return JavaUI.getSharedImages().getImage(imageName);
        }
        
        return null;
    }
    
    private JetSimpleNameExpression getSimpleNameExpression(IFile file, int identOffset) {
        String sourceCode = EditorUtil.getSourceCode(editor);
        String sourceCodeWithMarker = new StringBuilder(sourceCode).insert(identOffset, "IntellijIdeaRulezzz").toString();
        
        JetFile jetFile = (JetFile) KotlinPsiManager.INSTANCE.getParsedFile(file, sourceCodeWithMarker);
        
        int offsetWithourCr = LineEndUtil.convertCrToOsOffset(sourceCodeWithMarker, identOffset);
        PsiElement psiElement = jetFile.findElementAt(offsetWithourCr);
        
        return PsiTreeUtil.getParentOfType(psiElement, JetSimpleNameExpression.class);
    }
    
    private Collection<ICompletionProposal> generateTemplateProposals(ITextViewer viewer, int offset, String identifierPart) {
        List<String> contextTypeIds = KotlinApplicableTemplateContext.getApplicableContextTypeIds(viewer, 
                EditorUtil.getFile(editor), offset - identifierPart.length());
        
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        IRegion region = new Region(offset - identifierPart.length(), identifierPart.length());
        Image templateIcon = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_TEMPLATE);
        
        List<Template> templates = KotlinApplicableTemplateContext.getTemplatesByContextTypeIds(contextTypeIds);
        for (Template template : templates) {
            if (template.getName().startsWith(identifierPart)) {
                TemplateContext templateContext = createTemplateContext(region, template.getContextTypeId());
                proposals.add(new TemplateProposal(template, templateContext, region, templateIcon));
            }
        }
        
        return proposals;
    }
    
    private TemplateContext createTemplateContext(IRegion region, String contextTypeID) {
        return new KotlinDocumentTemplateContext(
                KotlinTemplateManager.INSTANCE.getContextTypeRegistry().getContextType(contextTypeID), 
                editor, region.getOffset(), region.getLength());
    }

    /**
     * Generate list of matching keywords
     * 
     * @param viewer the viewer whose document is used to compute the proposals
     * @param identOffset an offset within the document for which completions should be computed 
     * @param offset current position id the document
     * @param identifierPart part of current keyword
     * @return a collection of matching keywords  
     */
    private Collection<? extends ICompletionProposal> generateKeywordProposals(ITextViewer viewer, int identOffset,
            int offset, String identifierPart) {
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        if (!identifierPart.isEmpty()) {
            if (identOffset == 0 || Character.isWhitespace(viewer.getDocument().get().charAt(identOffset - 1))) {
                for (String keyword : KeywordManager.getAllKeywords()) {
                    if (keyword.startsWith(identifierPart)) {
                        proposals.add(new CompletionProposal(keyword, identOffset, offset - identOffset, keyword.length()));
                    }
                }
            }
        }
        return proposals;
    }

    /**
     * Method searches the beginning of the identifier 
     * 
     * @param text the text where search should be done.
     * @param offset 
     * @return offset of start symbol of identifier
     */
    private int getIdentifierStartOffset(String text, int offset) {
        int identStartOffset = offset;
        
        while ((identStartOffset != 0) && Character.isUnicodeIdentifierPart(text.charAt(identStartOffset - 1))) {
            identStartOffset--;
        }
        return identStartOffset;
    }
    
    
    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return VALID_PROPOSALS_CHARS;
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return VALID_INFO_CHARS;
    }

    @Override
    public String getErrorMessage() {
        return "";
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return new KotlinContextValidator();
    }

    @Override
    public void assistSessionStarted(ContentAssistEvent event) {}

    @Override
    public void assistSessionEnded(ContentAssistEvent event) {}

    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {}

}