package org.jetbrains.kotlin.ui.editors.quickfix;

import java.util.List;

import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;

public class KotlinSearchTypeRequestor extends TypeNameMatchRequestor {
    
    private final List<IType> collector;
    
    public KotlinSearchTypeRequestor(List<IType> collector) {
        this.collector = collector;
    }

    @Override
    public void acceptTypeNameMatch(TypeNameMatch match) {
        if (match.getAccessibility() == IAccessRule.K_ACCESSIBLE) {
            collector.add(match.getType());
        }
    }
}
