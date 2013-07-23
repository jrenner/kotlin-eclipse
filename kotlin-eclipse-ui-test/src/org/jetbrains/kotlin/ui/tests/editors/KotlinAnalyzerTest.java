package org.jetbrains.kotlin.ui.tests.editors;

import org.jetbrains.kotlin.testframework.editor.TextEditorTest;
import org.junit.Test;

public class KotlinAnalyzerTest extends KotlinAnalyzerTestCase {

	@Test
	public void hasAnalyzerKotlinRuntime() {
		doTest(
			"fun main(args : Array<String>) {\n" +
			"println(\"Hello\")\n" +
			"}", "Test1.kt");
	}
	
	@Test
	public void checkAnalyzerFoundError() {
		doTest(
			"fun main(args : Array<String>) {\r\n" +
			"<err>prin</err>(\"Hello\")\r\n" +
			"}", "Test2.kt");
	}
	
	@Test
	public void hasAnalyzerKotlinAnnotations() {
		doTest(
			"import java.util.regex.Pattern\r\n" +
			"fun main(args : Array<String>) {\r\n" +
			"  println(Pattern.compile(\"Some\").matcher(\"Some\"))\r\n" +
			"}", "Test3.kt");
	}
	
	@Test
	public void useJavaCodeFromKotlinFile() {
		TextEditorTest editorWithJavaCode = configureEditor("Some.java", "package testing; public class Some() { }");
		editorWithJavaCode.save();
		editorWithJavaCode.close();
		
		doTest(
			"package testing\r\n" +
			"fun tt() {\r\n" +
			"var a = Some()\r\n" +
			"}", "Test5.kt");
	}
}