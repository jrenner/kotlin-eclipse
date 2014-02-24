package org.jetbrains.kotlin.core.resolve.lang.java;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
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
        
        if (!fqName.equals(javaClass.getFqName())) {
            throw new IllegalStateException("Requested " + fqName + ", got " + javaClass.getFqName());
        }
        
        return javaClass;
    }

    @Override
    @Nullable
    public JavaPackage findPackage(@NotNull FqName fqName) {
//        TODO: Implement method findPackage(FqName)
        return null;
    }

}
