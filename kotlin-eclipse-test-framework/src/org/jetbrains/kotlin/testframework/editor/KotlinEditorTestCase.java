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
package org.jetbrains.kotlin.testframework.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.jetbrains.kotlin.testframework.utils.WorkspaceUtil;
import org.junit.After;
import org.junit.Before;

public abstract class KotlinEditorTestCase {
	
	public enum Separator {
		TAB, SPACE;
	}
	
	protected TextEditorTest testEditor;
	public static final String ERR_TAG_OPEN = "<err>";
	public static final String ERR_TAG_CLOSE = "</err>";
	public static final String BR = "<br>";
	
	private Separator initialSeparator;
	private int initialSpacesCount;

	@After
	public void afterTest() {
		deleteProjectAndCloseEditors();
		
		EditorsUI.getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, (Separator.SPACE == initialSeparator));
		EditorsUI.getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, initialSpacesCount);
	}
	
	@Before
	public void beforeTest() {
		refreshWorkspace();
		
		initialSeparator = EditorsUI.getPreferenceStore().getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS) ? Separator.TAB : Separator.SPACE;
		initialSpacesCount = EditorsUI.getPreferenceStore().getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}
	
	protected TextEditorTest configureEditor(String fileName, String content) {
    	return configureEditor(fileName, content, TextEditorTest.TEST_PROJECT_NAME, TextEditorTest.TEST_PACKAGE_NAME);
    }
	
    protected TextEditorTest configureEditor(String fileName, String content, String projectName, String packageName) {
    	TextEditorTest testEditor = new TextEditorTest(projectName);
		String toEditor = resolveTestTags(content);
		testEditor.createEditor(fileName, toEditor, packageName);
		
		return testEditor;
    }
    
    public void deleteProjectAndCloseEditors() {
		try {
			IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				project.delete(true, true, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
    }
    
    public void refreshWorkspace() {
		WorkspaceUtil.refreshWorkspace();
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_REFRESH, new NullProgressMonitor());
		} catch (OperationCanceledException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public String resolveTestTags(String text) {
		return text
				.replaceAll(ERR_TAG_OPEN, "")
				.replaceAll(ERR_TAG_CLOSE, "")
				.replaceAll(BR, System.lineSeparator());
    }
    
    public String removeTags(String text) {
    	return resolveTestTags(text).replaceAll("<caret>", "");
    }
    
    protected int getCaret() {
		return testEditor.getEditor().getViewer().getTextWidget().getCaretOffset();
	}
    
	public void createSourceFile(String referenceFileName, String referenceFile) {
		referenceFile = removeTags(referenceFile);
		try {
			testEditor.getTestJavaProject().createSourceFile("testing", referenceFileName, referenceFile);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void joinBuildThread() {
		while (true) {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				break;
			} catch (OperationCanceledException | InterruptedException e) {
			}
		}
	}
}
