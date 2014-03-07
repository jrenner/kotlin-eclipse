package org.jetbrains.kotlin.core.resolve.lang.java;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.JavaClassFinder;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaPackage;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.core.resolve.lang.java.structure.EclipseJavaClass;
import org.jetbrains.kotlin.core.resolve.lang.java.structure.EclipseJavaPackage;
import org.jetbrains.kotlin.core.utils.KotlinSearchRequestor;
import org.jetbrains.kotlin.core.utils.KotlinSearchTypeRequestor;

import com.google.common.collect.Lists;

public class EclipseJavaClassFinder implements JavaClassFinder {

    private static SearchEngine searchEngine = new SearchEngine();
    private static IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
    
    @Override
    @Nullable
    public JavaClass findClass(@NotNull FqName fqName) {
        List<IType> searchCollector = Lists.newArrayList();
        TypeNameMatchRequestor requestor = new KotlinSearchTypeRequestor(searchCollector);
        try {
            searchEngine.searchAllTypeNames(null, 
                    SearchPattern.R_EXACT_MATCH, 
                    fqName.asString().toCharArray(), 
                    SearchPattern.R_EXACT_MATCH, 
                    IJavaSearchConstants.TYPE, 
                    searchScope, 
                    requestor,
                    IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
                    null);
        } catch (CoreException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        if (searchCollector.size() != 1) return null;
        
        JavaClass javaClass = new EclipseJavaClass(searchCollector.get(0));
        
        FqName javaFQName = javaClass.getFqName();
        assert javaFQName != null;
        if (!(fqName.equals(javaFQName) || javaFQName.asString().endsWith(fqName.asString()))) {
            throw new IllegalStateException("Requested " + fqName + ", got " + javaClass.getFqName());
        }
        
        return javaClass;
    }

    @Override
    @Nullable
    public JavaPackage findPackage(@NotNull FqName fqName) {
        if (fqName.asString().isEmpty()) return null;
        
        SearchPattern searchPattern = SearchPattern.createPattern(fqName.asString(), IJavaSearchConstants.PACKAGE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
        KotlinSearchRequestor requestor = new KotlinSearchRequestor();
        
        try {
            searchEngine.search(searchPattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, searchScope, requestor, null);
        } catch (CoreException e) {
            KotlinLogger.logAndThrow(e);
        }
        
        List<IPackageFragment> packages = requestor.getPackages();
        
        if (packages.isEmpty()) return null;
        
        JavaPackage javaPackage = new EclipseJavaPackage(packages.get(0));
        
        return javaPackage;
    }

}
