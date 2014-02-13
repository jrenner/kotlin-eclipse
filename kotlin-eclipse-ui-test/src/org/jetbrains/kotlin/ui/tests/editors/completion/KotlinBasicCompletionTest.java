package org.jetbrains.kotlin.ui.tests.editors.completion;

import org.junit.Test;

public class KotlinBasicCompletionTest extends KotlinBasicCompletionTestCase {

	@Test
	public void testAfterFloatOnNewLine() {
		doTest("testData/completion/basic/common/AfterFloatOnNewLine.kt");
	}
	
	@Test
	public void testAfterIntSeparatedWithComments() {
		doTest("testData/completion/basic/common/AfterIntSeparatedWithComments.kt");
	}
	
	@Test
	public void testAutoCastAfterIf() {
		doTest("testData/completion/basic/common/AutoCastAfterIf.kt");
	}
	
	@Test
	public void testAutoCastAfterIfMethod() {
		doTest("testData/completion/basic/common/AutoCastAfterIfMethod.kt");
	}
	
	@Test
	public void testAutoCastForThis() {
		doTest("testData/completion/basic/common/AutoCastForThis.kt");
	}
	
	@Test
	public void testAutoCastInWhen() {
		doTest("testData/completion/basic/common/AutoCastInWhen.kt");
	}
	
	@Test
	public void testBasicAny() {
		doTest("testData/completion/basic/common/BasicAny.kt");
	}
	
	@Test
	public void testBasicInt() {
		doTest("testData/completion/basic/common/BasicInt.kt");
	}
	
	@Test
	public void testCallLocalLambda() {
		doTest("testData/completion/basic/common/CallLocalLambda.kt");
	}
}
