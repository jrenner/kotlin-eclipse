package org.jetbrains.kotlin.ui.tests.editors.completion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

		List<String> actualProposals = getActualProposals();

		Integer expectedNumber = ExpectedCompletionUtils.numberOfItemsShouldPresent(fileText);
		if (expectedNumber != null) {
			Assert.assertEquals("Different count of expected and actual proposals", expectedNumber.intValue(), actualProposals.size());
		}

		Set<String> proposalSet = new HashSet<String>(actualProposals);
		
		List<String> expectedProposals = ExpectedCompletionUtils.itemsShouldExist(fileText);
		assertExists(expectedProposals, proposalSet);

		List<String> unexpectedProposals = ExpectedCompletionUtils.itemsShouldAbsent(fileText);
		assertNotExists(unexpectedProposals, proposalSet);
	}
	
	private List<String> getActualProposals() {
		CompletionProcessor ktCompletionProcessor = new CompletionProcessor(testEditor.getEditor());
		ICompletionProposal[] proposals = ktCompletionProcessor.computeCompletionProposals(testEditor.getEditor().getViewer(), getCaret());
		
		List<String> actualProposals = new ArrayList<String>();
		for (ICompletionProposal proposal : proposals) {
			String replacementString = proposal.getAdditionalProposalInfo();
			if (replacementString == null) replacementString = proposal.getDisplayString();

			actualProposals.add(replacementString);
		}
		
		return actualProposals;
	}

	private String getName(String testPath) {
		return new Path(testPath).lastSegment();
	}

	private String getText(String testPath) {
		try {
			File file = new File(testPath);
			return String.valueOf(FileUtil.loadFileText(file, null));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertExists(List<String> itemsShouldExist, Set<String> actualItems) {
		String errorMessage = getErrorMessage(itemsShouldExist, actualItems);
		for (String itemShouldExist : itemsShouldExist) {
			Assert.assertTrue(errorMessage, actualItems.contains(itemShouldExist.trim()));
		}
	}
	
	private void assertNotExists(List<String> itemsShouldAbsent, Set<String> actualItems) {
		String errorMessage = getErrorMessage(itemsShouldAbsent, actualItems);
		for (String itemShouldAbsent : itemsShouldAbsent) {
			Assert.assertFalse(errorMessage, actualItems.contains(itemShouldAbsent));
		}
	}
	
	private String getErrorMessage(List<String> expected, Set<String> actual) {
		StringBuilder errorMessage = new StringBuilder();
		
		errorMessage.append("Expected: <");
		for (String proposal : expected) {
			errorMessage.append(proposal);
			errorMessage.append(" ");
		}
		errorMessage.append("> ");
		
		errorMessage.append("but was:<");
		for (String proposal : actual) {
			errorMessage.append(proposal);
			errorMessage.append(" ");
		}
		errorMessage.append(">");
		
		return errorMessage.toString();
	}
}