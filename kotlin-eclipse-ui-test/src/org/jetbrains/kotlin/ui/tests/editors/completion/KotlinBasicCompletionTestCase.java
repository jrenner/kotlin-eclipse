package org.jetbrains.kotlin.ui.tests.editors.completion;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jetbrains.kotlin.testframework.editor.KotlinEditorTestCase;
import org.jetbrains.kotlin.testframework.utils.ExpectedCompletionUtils;
import org.jetbrains.kotlin.ui.editors.codeassist.CompletionProcessor;

import com.intellij.openapi.util.io.FileUtil;

public class KotlinBasicCompletionTestCase extends KotlinEditorTestCase {

	protected void doTest(String testPath) {
		String fileText = getText(testPath);
		testEditor = configureEditor(getName(testPath), fileText);

		joinBuildThread();

		CompletionProcessor ktCompletionProcessor = new CompletionProcessor(testEditor.getEditor());
		ICompletionProposal[] proposals = ktCompletionProcessor.computeCompletionProposals(testEditor.getEditor().getViewer(), getCaret());

		Set<String> actualProposals = new HashSet<String>();
		for (ICompletionProposal proposal : proposals) {
			String replacementString = proposal.getAdditionalProposalInfo();
			if (replacementString == null) replacementString = proposal.getDisplayString();

			actualProposals.add(replacementString);
		}

		Integer expectedNumber = ExpectedCompletionUtils.numberOfItemsShouldPresent(fileText);
		if (expectedNumber != null) {
			Assert.assertEquals(expectedNumber, new Integer(proposals.length));
		}

		List<String> expectedProposals = ExpectedCompletionUtils.itemsShouldExist(fileText);
		assertExists(expectedProposals, actualProposals);

		List<String> unexpectedProposals = ExpectedCompletionUtils.itemsShouldAbsent(fileText);
		assertNotExists(unexpectedProposals, actualProposals);
	}

	private String getName(String testPath) {
		return new Path(testPath).lastSegment();
	}

	private String getText(String testPath) {
		try {
			File file = new File(testPath);
			return String.valueOf(FileUtil.loadFileText(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertExists(List<String> itemsShouldExist, Set<String> actualItems) {
		for (String itemShouldExist : itemsShouldExist) {
			Assert.assertTrue(actualItems.contains(itemShouldExist.trim()));
		}
	}

	private void assertNotExists(List<String> itemsShouldAbsent, Set<String> actualItems) {
		for (String itemShouldAbsent : itemsShouldAbsent) {
			Assert.assertFalse(actualItems.contains(itemShouldAbsent));
		}
	}
}