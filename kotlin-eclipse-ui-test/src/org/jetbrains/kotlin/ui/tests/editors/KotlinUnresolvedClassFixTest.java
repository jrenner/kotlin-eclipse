package org.jetbrains.kotlin.ui.tests.editors;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.intellij.openapi.util.Pair;

public class KotlinUnresolvedClassFixTest extends KotlinUnresolvedClassFixTestCase {
	
	@Test
	public void importOneStandardClass() {
		doTest("fun test() = ArrayList", 
				null,
				
				"import java.util.ArrayList<br>" +
				"<br>" +
				"fun test() = ArrayList");
	}
	
	@Test
	public void importWithExtraBreakline() {
		doTest(
				"package testing<br>" +
				"fun test() = ArrayList",
				null,
				
				"package testing<br>" +
				"<br>" +
				"import java.util.ArrayList<br>" +
				"<br>" +
				"fun test() = ArrayList");
	}
	
	@Test
	public void importClassWithExistingPackageKeyword() {
		doTest(
				"package testing<br>" +
				"<br>" +
				"fun test() = ArrayList",
				null,
				
				"package testing<br>" +
				"<br>" +
				"import java.util.ArrayList<br>" +
				"<br>" +
				"fun test() = ArrayList");
	}
	
	@Test
	public void importLocalJavaClass() {
		List<Pair<String, String>> files = new ArrayList<>();
		files.add(Pair.create(
				"JavaClass1.java", 
				
				"package testing;<br>" +
				"public class JavaClass1 {}"));
		
		doTest("fun test() = JavaClass1", 
				files,
				
				"import testing.JavaClass1<br>" +
				"<br>" +
				"fun test() = JavaClass1"); 
	}
	
	@Test
	public void fixOnlyUnresolvedReferenceExpressions() {
		doTest("fun test() = Object",
				null,
				"fun test() = Object");
	}
}
