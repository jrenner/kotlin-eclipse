package org.jetbrains.kotlin.core.resolve;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.analyzer.AnalyzeExhaust;
import org.jetbrains.jet.analyzer.AnalyzerFacade;
import org.jetbrains.jet.analyzer.AnalyzerFacadeForEverything;
import org.jetbrains.jet.context.ContextPackage;
import org.jetbrains.jet.context.GlobalContext;
import org.jetbrains.jet.context.GlobalContextImpl;
import org.jetbrains.jet.di.InjectorForLazyResolveWithJava;
import org.jetbrains.jet.lang.descriptors.DependencyKind;
import org.jetbrains.jet.lang.descriptors.ModuleDescriptor;
import org.jetbrains.jet.lang.descriptors.ModuleDescriptorImpl;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.resolve.AnalyzerScriptParameter;
import org.jetbrains.jet.lang.resolve.AnalyzingUtils;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.BindingTraceContext;
import org.jetbrains.jet.lang.resolve.BodiesResolveContext;
import org.jetbrains.jet.lang.resolve.CachedBodiesResolveContext;
import org.jetbrains.jet.lang.resolve.ImportPath;
import org.jetbrains.jet.lang.resolve.TopDownAnalysisContext;
import org.jetbrains.jet.lang.resolve.TopDownAnalysisParameters;
import org.jetbrains.jet.lang.resolve.java.mapping.JavaToKotlinClassMap;
import org.jetbrains.jet.lang.resolve.lazy.ResolveSession;
import org.jetbrains.jet.lang.resolve.lazy.declarations.FileBasedDeclarationProviderFactory;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns;
import org.jetbrains.kotlin.core.injectors.EclipseInjectorForTopDownAnalyzerForJvm;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class EclipseAnalyzerFacadeForJVM implements AnalyzerFacade {
    
    public static final List<ImportPath> DEFAULT_IMPORTS = ImmutableList.of(
            new ImportPath("java.lang.*"),
            new ImportPath("kotlin.*"),
            new ImportPath("kotlin.io.*"));

    private EclipseAnalyzerFacadeForJVM() {
    }

    @Override
    @NotNull
    public AnalyzeExhaust analyzeFiles(@NotNull Project project,
            @NotNull Collection<JetFile> files,
            @NotNull List<AnalyzerScriptParameter> scriptParameters,
            @NotNull Predicate<PsiFile> filesToAnalyzeCompletely) {
        return analyzeFilesWithJavaIntegration(project, files, scriptParameters, filesToAnalyzeCompletely, true);
    }

    @NotNull
    @Override
    public AnalyzeExhaust analyzeBodiesInFiles(@NotNull Project project,
                                               @NotNull List<AnalyzerScriptParameter> scriptParameters,
                                               @NotNull Predicate<PsiFile> filesForBodiesResolve,
                                               @NotNull BindingTrace headersTraceContext,
                                               @NotNull BodiesResolveContext bodiesResolveContext,
                                               @NotNull ModuleDescriptor module
    ) {
        return AnalyzerFacadeForEverything.analyzeBodiesInFilesWithJavaIntegration(
                project, scriptParameters, filesForBodiesResolve,
                headersTraceContext, bodiesResolveContext, module);
    }

    @NotNull
    @Override
    public ResolveSession getLazyResolveSession(@NotNull Project fileProject, @NotNull Collection<JetFile> files) {
        return createLazyResolveSession(fileProject, files, new BindingTraceContext(), true);
    }

    @NotNull
    public static ResolveSession createLazyResolveSession(
            @NotNull Project project,
            @NotNull Collection<JetFile> files,
            @NotNull BindingTrace trace,
            boolean addBuiltIns
    ) {
        GlobalContextImpl globalContext = ContextPackage.GlobalContext();

        // TODO: Replace with stub declaration provider
        FileBasedDeclarationProviderFactory declarationProviderFactory = new FileBasedDeclarationProviderFactory(
                globalContext.getStorageManager(),
                files);

        InjectorForLazyResolveWithJava resolveWithJava = new InjectorForLazyResolveWithJava(
                project,
                globalContext,
                declarationProviderFactory,
                trace);

        resolveWithJava.getModule().addFragmentProvider(
                DependencyKind.BINARIES, resolveWithJava.getJavaDescriptorResolver().getPackageFragmentProvider());

        if (addBuiltIns) {
            resolveWithJava.getModule().addFragmentProvider(
                    DependencyKind.BUILT_INS,
                    KotlinBuiltIns.getInstance().getBuiltInsModule().getPackageFragmentProvider());
        }

        return resolveWithJava.getResolveSession();
    }

    @NotNull
    public static AnalyzeExhaust analyzeOneFileWithJavaIntegrationAndCheckForErrors(
            JetFile file, List<AnalyzerScriptParameter> scriptParameters) {
        AnalyzingUtils.checkForSyntacticErrors(file);

        AnalyzeExhaust analyzeExhaust = analyzeOneFileWithJavaIntegration(file, scriptParameters);

        AnalyzingUtils.throwExceptionOnErrors(analyzeExhaust.getBindingContext());

        return analyzeExhaust;
    }

    @NotNull
    public static AnalyzeExhaust analyzeOneFileWithJavaIntegration(
            JetFile file, List<AnalyzerScriptParameter> scriptParameters) {
        return analyzeFilesWithJavaIntegration(file.getProject(), Collections.singleton(file), scriptParameters,
                                               Predicates.<PsiFile>alwaysTrue());
    }

    @NotNull
    public static AnalyzeExhaust analyzeFilesWithJavaIntegrationAndCheckForErrors(
            Project project,
            Collection<JetFile> files,
            List<AnalyzerScriptParameter> scriptParameters,
            Predicate<PsiFile> filesToAnalyzeCompletely
    ) {
        for (JetFile file : files) {
            AnalyzingUtils.checkForSyntacticErrors(file);
        }

        AnalyzeExhaust analyzeExhaust = analyzeFilesWithJavaIntegration(
                project, files, scriptParameters, filesToAnalyzeCompletely, false);

        AnalyzingUtils.throwExceptionOnErrors(analyzeExhaust.getBindingContext());

        return analyzeExhaust;
    }

    @NotNull
    public static AnalyzeExhaust analyzeFilesWithJavaIntegration(
            Project project,
            Collection<JetFile> files,
            List<AnalyzerScriptParameter> scriptParameters,
            Predicate<PsiFile> filesToAnalyzeCompletely
    ) {
        return analyzeFilesWithJavaIntegration(
                project, files, scriptParameters, filesToAnalyzeCompletely, false);
    }

    @NotNull
    public static AnalyzeExhaust analyzeFilesWithJavaIntegration(
            Project project, Collection<JetFile> files, List<AnalyzerScriptParameter> scriptParameters, Predicate<PsiFile> filesToAnalyzeCompletely,
            boolean storeContextForBodiesResolve) {
        BindingTraceContext bindingTraceContext = new BindingTraceContext();

        return analyzeFilesWithJavaIntegration(project, files, bindingTraceContext, scriptParameters, filesToAnalyzeCompletely,
                                               storeContextForBodiesResolve);
    }

    @NotNull
    public static AnalyzeExhaust analyzeFilesWithJavaIntegration(
            Project project,
            Collection<JetFile> files,
            BindingTrace trace,
            List<AnalyzerScriptParameter> scriptParameters,
            Predicate<PsiFile> filesToAnalyzeCompletely,
            boolean storeContextForBodiesResolve
    ) {
        return analyzeFilesWithJavaIntegration(project, files, trace, scriptParameters, filesToAnalyzeCompletely,
                                               storeContextForBodiesResolve, createJavaModule("<module>"));
    }

    @NotNull
    public static AnalyzeExhaust analyzeFilesWithJavaIntegration(
            Project project,
            Collection<JetFile> files,
            BindingTrace trace,
            List<AnalyzerScriptParameter> scriptParameters,
            Predicate<PsiFile> filesToAnalyzeCompletely,
            boolean storeContextForBodiesResolve,
            ModuleDescriptorImpl module
    ) {
        GlobalContext globalContext = ContextPackage.GlobalContext();
        return analyzeFilesWithJavaIntegrationInGlobalContext(project, files, trace, scriptParameters, filesToAnalyzeCompletely,
                                                              storeContextForBodiesResolve, module, globalContext);
    }

    @NotNull
    public static AnalyzeExhaust analyzeFilesWithJavaIntegrationInGlobalContext(
            Project project,
            Collection<JetFile> files,
            BindingTrace trace,
            List<AnalyzerScriptParameter> scriptParameters,
            Predicate<PsiFile> filesToAnalyzeCompletely,
            boolean storeContextForBodiesResolve,
            ModuleDescriptorImpl module,
            GlobalContext globalContext
    ) {
        TopDownAnalysisParameters topDownAnalysisParameters = new TopDownAnalysisParameters(
                globalContext.getStorageManager(),
                globalContext.getExceptionTracker(),
                filesToAnalyzeCompletely,
                false,
                false,
                scriptParameters
        );

        EclipseInjectorForTopDownAnalyzerForJvm injector = new EclipseInjectorForTopDownAnalyzerForJvm(project, topDownAnalysisParameters, trace, module);
        try {
            module.addFragmentProvider(DependencyKind.BINARIES, injector.getJavaDescriptorResolver().getPackageFragmentProvider());
            TopDownAnalysisContext topDownAnalysisContext = injector.getTopDownAnalyzer().analyzeFiles(topDownAnalysisParameters, files);
            BodiesResolveContext bodiesResolveContext = storeContextForBodiesResolve ?
                                                        new CachedBodiesResolveContext(topDownAnalysisContext) :
                                                        null;
            return AnalyzeExhaust.success(trace.getBindingContext(), bodiesResolveContext, module);
        }
        finally {
            injector.destroy();
        }
    }

    @NotNull
    public static ModuleDescriptorImpl createJavaModule(@NotNull String name) {
        return new ModuleDescriptorImpl(Name.special(name), DEFAULT_IMPORTS, JavaToKotlinClassMap.getInstance());
    }
}
