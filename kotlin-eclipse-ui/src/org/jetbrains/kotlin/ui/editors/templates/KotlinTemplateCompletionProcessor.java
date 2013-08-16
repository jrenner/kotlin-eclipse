package org.jetbrains.kotlin.ui.editors.templates;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

public class KotlinTemplateCompletionProcessor extends TemplateCompletionProcessor {

    @Override
    protected Template[] getTemplates(String contextTypeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
        //KotlinTemplateManager templateManager = KotlinTemplateManager.INSTANCE.;
        return null;
    }

    @Override
    protected Image getImage(Template template) {
        return null;
    }

}
