package org.jetbrains.kotlin.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.jetbrains.jet.lang.diagnostics.Diagnostic;
import org.jetbrains.jet.lang.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.kotlin.core.resolve.KotlinAnalyzer;

import com.intellij.openapi.util.TextRange;

public class AnalyzerScheduler extends Job {
    
    private final IJavaProject javaProject;
    
    private boolean canceling;
    
    public AnalyzerScheduler(IJavaProject javaProject) {
        super("Analyze " + javaProject.getElementName());
        
        this.javaProject = javaProject;
        canceling = false;
    }
    
    public static void analyzeProjectInBackground(IJavaProject javaProject) {
        new AnalyzerScheduler(javaProject).schedule();
    }
    
    @Override
    protected void canceling() {
        canceling = true;
    }
    
    @Override
    protected synchronized IStatus run(IProgressMonitor monitor) {
        try {
            assert javaProject != null : "JavaProject is null";
            
            if (canceling) {
                return Status.CANCEL_STATUS;
            }
            
            BindingContext bindingContext = KotlinAnalyzer.analyzeProject(javaProject);
            
            AnnotationManager.clearAllMarkersFromProject(javaProject);
            
            List<Diagnostic> diagnostics = new ArrayList<Diagnostic>(bindingContext.getDiagnostics());
            for (Diagnostic diagnostic : diagnostics) {
                List<TextRange> ranges = diagnostic.getTextRanges();
                if (ranges.isEmpty()) {
                    continue;
                }
                
                int problemSeverity = 0;
                switch (diagnostic.getSeverity()) {
                    case ERROR:
                        problemSeverity = IMarker.SEVERITY_ERROR;
                        break;
                    case WARNING:
                        problemSeverity = IMarker.SEVERITY_WARNING;
                        break;
                    default:
                        continue;
                }
                
                IFile curFile = ResourcesPlugin.getWorkspace().getRoot().
                        getFileForLocation(new Path(diagnostic.getPsiFile().getVirtualFile().getPath()));
                
                AnnotationManager.addProblemMarker(curFile, DefaultErrorMessages.RENDERER.render(diagnostic), 
                        problemSeverity, diagnostic.getPsiFile().getText(), ranges.get(0));
                
                if (canceling) {
                    return Status.CANCEL_STATUS;
                }
            }
            
            return Status.OK_STATUS;
            
        } finally {
            canceling = false;
        }
    }
}