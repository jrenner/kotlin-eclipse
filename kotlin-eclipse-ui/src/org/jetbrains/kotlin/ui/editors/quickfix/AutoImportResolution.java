package org.jetbrains.kotlin.ui.editors.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.core.log.KotlinLogger;

public class AutoImportResolution implements IMarkerResolutionGenerator2 {
    
    private static SearchEngine searchEngine = new SearchEngine();
    private static IMarkerResolution[] NO_RESOLUTIONS = new IMarkerResolution[] { };
    
    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        String markedText = marker.getAttribute("markedText", null);
        if (markedText == null) {
            return NO_RESOLUTIONS;
        }
        
        List<IType> typeResolutions = findAllTypes(markedText);
        List<AutoImportMarkerResolution> markerResolutions = new ArrayList<AutoImportMarkerResolution>();
        for (IType type : typeResolutions) {
            markerResolutions.add(new AutoImportMarkerResolution(type));
        }
        
        return markerResolutions.toArray(new IMarkerResolution[markerResolutions.size()]);
    }
    
    @NotNull
    private List<IType> findAllTypes(@NotNull String typeName) {
        SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.CLASS_AND_INTERFACE, 
                IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
        if (pattern == null) {
            return Collections.emptyList();
        }
        
        IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        
        List<IType> searchCollector = new ArrayList<IType>();
        TypeNameMatchRequestor requestor = new KotlinSearchTypeRequestor(searchCollector);
        try {
            searchEngine.searchAllTypeNames(null, 
                    SearchPattern.R_EXACT_MATCH, 
                    typeName.toCharArray(), 
                    SearchPattern.R_EXACT_MATCH, 
                    IJavaSearchConstants.CLASS_AND_INTERFACE, 
                    scope, 
                    requestor,
                    IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
                    null);
        } catch (CoreException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        return searchCollector;
    }

    @Override
    public boolean hasResolutions(IMarker marker) {
        return marker.getAttribute("isQuickFixable", false);
    }
}
