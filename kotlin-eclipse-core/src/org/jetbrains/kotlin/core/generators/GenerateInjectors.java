package org.jetbrains.kotlin.core.generators;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.jet.context.GlobalContext;
import org.jetbrains.jet.di.DependencyInjectorGenerator;
import org.jetbrains.jet.di.DiType;
import org.jetbrains.jet.di.GivenExpression;
import org.jetbrains.jet.di.InjectorForTopDownAnalyzer;
import org.jetbrains.jet.di.InjectorGeneratorUtil;
import org.jetbrains.jet.lang.descriptors.ModuleDescriptorImpl;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.MutablePackageFragmentProvider;
import org.jetbrains.jet.lang.resolve.TopDownAnalyzer;
import org.jetbrains.jet.lang.resolve.java.resolver.PsiBasedExternalAnnotationResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.PsiBasedMethodSignatureChecker;
import org.jetbrains.jet.lang.resolve.java.resolver.TraceBasedErrorReporter;
import org.jetbrains.jet.lang.resolve.java.resolver.TraceBasedExternalSignatureResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.TraceBasedJavaResolverCache;
import org.jetbrains.jet.lang.resolve.kotlin.VirtualFileFinder;
import org.jetbrains.kotlin.core.resolve.lang.java.EclipseJavaClassFinder;

import com.intellij.openapi.project.Project;


// NOTE: After making changes, you need to re-generate the injectors.
//       To do that, you can run main in this class.
public class GenerateInjectors {
    public static void main(String[] args) throws Throwable {
        for (DependencyInjectorGenerator generator : createGenerators()) {
            try {
                generator.generate();
            }
            catch (Throwable e) {
                System.err.println(generator.getOutputFile());
                throw e;
            }
        }
    }
    
    public static List<DependencyInjectorGenerator> createGenerators() throws IOException {
        return Arrays.asList(
                generateInjectorForTopDownAnalyzerForJvm()
//                generateInjectorForTopDownAnalyzerBasic(),
//                generateInjectorForJavaDescriptorResolver()
                );
    }
    
    private static String defaultName(Class<?> entityType) {
        return InjectorGeneratorUtil.var(new DiType(entityType));
    }
    
    private static DependencyInjectorGenerator generateInjectorForTopDownAnalyzerForJvm() {
        DependencyInjectorGenerator generator = new DependencyInjectorGenerator();
        addFields(generator, Arrays.asList(
                EclipseJavaClassFinder.class, 
                TraceBasedExternalSignatureResolver.class,
                TraceBasedJavaResolverCache.class,
                TraceBasedErrorReporter.class,
                PsiBasedMethodSignatureChecker.class,
                PsiBasedExternalAnnotationResolver.class,
                MutablePackageFragmentProvider.class));
        
        generator.implementInterface(InjectorForTopDownAnalyzer.class);
        
        generator.addParameter(false, new DiType(Project.class), defaultName(Project.class), true, false);
        generator.addParameter(false, new DiType(GlobalContext.class), defaultName(GlobalContext.class), true, true);
        generator.addParameter(false, new DiType(BindingTrace.class), defaultName(BindingTrace.class), true, false);
        generator.addParameter(false, new DiType(ModuleDescriptorImpl.class), defaultName(ModuleDescriptorImpl.class), true, true);
        
        generator.addField(true, new DiType(TopDownAnalyzer.class), defaultName(TopDownAnalyzer.class), null, false);
        
        generator.addField(false, new DiType(MutablePackageFragmentProvider.class), 
                defaultName(MutablePackageFragmentProvider.class), null, false);
        
        
        generator.addField(false, new DiType(VirtualFileFinder.class), defaultName(VirtualFileFinder.class),
                new GivenExpression(VirtualFileFinder.class.getName() + ".SERVICE.getInstance(project)"), false);
        
        generator.configure("src", "org.jetbrains.kotlin.core.injectors", "TestEclipseInjectorForTopDownAnalyzerForJvm",
              GenerateInjectors.class.getCanonicalName());
        
        return generator;
    }
    
    private static DependencyInjectorGenerator addFields(DependencyInjectorGenerator generator, List<Class<?>> types) {
        for (Class<?> type : types) {
            generator.addField(false, new DiType(type), defaultName(type), null, false);
        }
        
        return generator;
    }
}
//    private static DependencyInjectorGenerator generateInjectorForTopDownAnalyzerForJvm() throws IOException {
//        DependencyInjectorGenerator generator = new DependencyInjectorGenerator();
//        generator.implementInterface(InjectorForTopDownAnalyzer.class);
//        generateInjectorForTopDownAnalyzerCommon(generator);
//        generator.addPublicField(JavaDescriptorResolver.class);
//        generator.addField(EclipseJavaClassFinder.class);
//        generator.addField(TraceBasedExternalSignatureResolver.class);
//        generator.addField(TraceBasedJavaResolverCache.class);
//        generator.addField(TraceBasedErrorReporter.class);
//        generator.addField(PsiBasedMethodSignatureChecker.class);
//        generator.addField(PsiBasedExternalAnnotationResolver.class);
//        generator.addField(MutablePackageFragmentProvider.class);
//        generator.addField(false, VirtualFileFinder.class, "virtualFileFinder",
//                new GivenExpression(
//                        VirtualFileFinder.class.getName() + ".SERVICE.getInstance(project)"));
//        generator.configure("src", "org.jetbrains.kotlin.core.injectors", "TestEclipseInjectorForTopDownAnalyzerForJvm",
//                           GenerateInjectors.class.getCanonicalName());
//        return generator;
//    }
//
//    private static DependencyInjectorGenerator generateInjectorForJavaDescriptorResolver() throws IOException {
//        DependencyInjectorGenerator generator = new DependencyInjectorGenerator();
//
//        // Parameters
//        generator.addParameter(Project.class);
//        generator.addParameter(BindingTrace.class);
//        
//        // Fields
//        generator.addField(true, GlobalContextImpl.class, null, new GivenExpression("org.jetbrains.jet.context.ContextPackage.GlobalContext()"));
//        generator.addField(false, StorageManager.class, null, new GivenExpression("globalContext.getStorageManager()"));
//        generator.addPublicField(JavaClassFinderImpl.class);
//        generator.addField(TraceBasedExternalSignatureResolver.class);
//        generator.addField(TraceBasedJavaResolverCache.class);
//        generator.addField(TraceBasedErrorReporter.class);
//        generator.addField(PsiBasedMethodSignatureChecker.class);
//        generator.addField(PsiBasedExternalAnnotationResolver.class);
//        generator.addPublicField(JavaDescriptorResolver.class);
//        generator.addField(false, VirtualFileFinder.class, "virtualFileFinder",
//                           new GivenExpression(VirtualFileFinder.class.getName() + ".SERVICE.getInstance(project)"));
//        generator.addField(true, ModuleDescriptorImpl.class, "module",
//                           new GivenExpression("org.jetbrains.jet.lang.resolve.java.AnalyzerFacadeForJVM.createJavaModule(\"<fake-jdr-module>\")"));
//
//        generator.configure("src", "org.jetbrains.kotlin.core.injectors", "InjectorForJavaDescriptorResolver",
//                            GenerateInjectors.class.getCanonicalName());
//        return generator;
//    }
//
//    private static DependencyInjectorGenerator generateInjectorForTopDownAnalyzerCommon(DependencyInjectorGenerator generator) {
//        // Fields
////        generator.addParameter(Project.class);
////        generator.addParameter(GlobalContext.class, true);
////        generator.addParameter(BindingTrace.class);
////        generator.addPublicParameter(ModuleDescriptorImpl.class);
////        generator.addPublicField(TopDownAnalyzer.class);
////        generator.addField(MutablePackageFragmentProvider.class);
//        generator.addPublicField(TopDownAnalyzer.class);
//        generator.addPublicField(TopDownAnalysisContext.class);
//        generator.addPublicField(BodyResolver.class);
//        generator.addPublicField(ControlFlowAnalyzer.class);
//        generator.addPublicField(DeclarationsChecker.class);
//        generator.addPublicField(DescriptorResolver.class);
//        generator.addField(false, StorageManager.class, null, new GivenExpression("topDownAnalysisParameters.getStorageManager()"));
//        generator.addField(CallResolverExtensionProvider.class);
//
//        // Parameters
//        generator.addPublicParameter(Project.class);
//        generator.addParameter(TopDownAnalysisParameters.class);
//        generator.addPublicParameter(BindingTrace.class);
//        generator.addPublicParameter(ModuleDescriptorImpl.class);
//        return generator;
//    }
//
//    private static DependencyInjectorGenerator generateMacroInjector() throws IOException {
//        DependencyInjectorGenerator generator = new DependencyInjectorGenerator();
//
//        // Fields
//        generator.addPublicField(ExpressionTypingServices.class);
//        generator.addField(CallResolverExtensionProvider.class);
//        generator.addField(false, GlobalContext.class, null, new GivenExpression("org.jetbrains.jet.context.ContextPackage.GlobalContext()"));
//        generator.addField(false, StorageManager.class, null, new GivenExpression("globalContext.getStorageManager()"));
//        generator.addField(false, PlatformToKotlinClassMap.class, null, new GivenExpression("moduleDescriptor.getPlatformToKotlinClassMap()"));
//
//        // Parameters
//        generator.addPublicParameter(Project.class);
//        generator.addParameter(ModuleDescriptor.class);
//
//        generator.configure("compiler/frontend/src", "org.jetbrains.jet.di", "InjectorForMacros", GenerateInjectors.class.getCanonicalName());
//        return generator;
//    }
//
//    private static DependencyInjectorGenerator generateTestInjector() throws IOException {
//        DependencyInjectorGenerator generator = new DependencyInjectorGenerator();
//
//        // Fields
//        generator.addPublicField(DescriptorResolver.class);
//        generator.addPublicField(ExpressionTypingServices.class);
//        generator.addPublicField(TypeResolver.class);
//        generator.addPublicField(CallResolver.class);
//        generator.addField(CallResolverExtensionProvider.class);
//        generator.addField(false, StorageManager.class, null, new GivenExpression("globalContext.getStorageManager()"));
//        generator.addField(true, KotlinBuiltIns.class, null, new GivenExpression("KotlinBuiltIns.getInstance()"));
//        generator.addField(false, PlatformToKotlinClassMap.class, null, new GivenExpression("moduleDescriptor.getPlatformToKotlinClassMap()"));
//        generator.addField(false, GlobalContext.class, null, new GivenExpression("org.jetbrains.jet.context.ContextPackage.GlobalContext()"));
//
//        // Parameters
//        generator.addPublicParameter(Project.class);
//        generator.addParameter(ModuleDescriptor.class);
//
//        generator.configure("compiler/tests", "org.jetbrains.jet.di", "InjectorForTests", GenerateInjectors.class.getCanonicalName());
//        return generator;
//    }
//
//    private static DependencyInjectorGenerator generateInjectorForBodyResolve() throws IOException {
//        DependencyInjectorGenerator generator = new DependencyInjectorGenerator();
//        // Fields
//        generator.addPublicField(BodyResolver.class);
//        generator.addField(CallResolverExtensionProvider.class);
//        generator.addField(false, PlatformToKotlinClassMap.class, null, new GivenExpression("moduleDescriptor.getPlatformToKotlinClassMap()"));
//        generator.addField(FunctionAnalyzerExtension.class);
//        generator.addField(false, StorageManager.class, null, new GivenExpression("topDownAnalysisParameters.getStorageManager()"));
//
//        // Parameters
//        generator.addPublicParameter(Project.class);
//        generator.addParameter(TopDownAnalysisParameters.class);
//        generator.addPublicParameter(BindingTrace.class);
//        generator.addPublicParameter(BodiesResolveContext.class);
//        generator.addParameter(ModuleDescriptor.class);
//        generator.configure("compiler/frontend/src", "org.jetbrains.jet.di", "InjectorForBodyResolve", GenerateInjectors.class.getCanonicalName());
//        return generator;
//    }