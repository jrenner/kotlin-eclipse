package org.jetbrains.kotlin.ui.tests.editors;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.util.Pair;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jetbrains.kotlin.testframework.utils.EditorTestUtils;

public class KotlinUnresolvedClassFixTestCase extends KotlinEditorTestCase {

	public void doTest(String input, List<Pair<String, String>> files, String expected) {
		testEditor = configureEditor("Test.kt", input);
		
		if (files != null) {
			for (Pair<String, String> file : files) {
				createSourceFile(file.getFirst(), file.getSecond());
			}
		}
		
		testEditor.save();
		joinBuildThread();
		
		try {
			IMarker[] markers = testEditor.getEditingFile().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			List<IMarkerResolution> resolutions = collectResolutions(markers);
			for (IMarkerResolution resolution : resolutions) {
				resolution.run(null);
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		
		EditorTestUtils.assertByEditor(testEditor.getEditor(), expected);
	}
	
	private void joinBuildThread() {
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
			Thread.sleep(3000);
		} catch (OperationCanceledException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<IMarkerResolution> collectResolutions(IMarker[] markers) {
		List<IMarkerResolution> resolutions = new ArrayList<>();
		for (IMarker marker : markers) {
			if (IDE.getMarkerHelpRegistry().hasResolutions(marker)) {
				IMarkerResolution[] markerResolutions = IDE.getMarkerHelpRegistry().getResolutions(marker);
		        if (markerResolutions.length > 0) {
		        	resolutions.add(markerResolutions[0]);
		        }
			}
		}
		
		return resolutions;
	}
}
