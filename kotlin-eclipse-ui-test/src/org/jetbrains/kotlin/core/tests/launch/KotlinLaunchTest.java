package org.jetbrains.kotlin.core.tests.launch;

import org.junit.Test;

public class KotlinLaunchTest extends KotlinLaunchTestCase {

	@Test
	public void launchSimpleProject() {
		doTest(
				"fun main(args : Array<String>) = print(123)",
				"test_project", "org.jet.pckg", null);
	}
	
	@Test
	public void launchWithProjectNameHavingSpace() {
		doTest(
				"fun main(args : Array<String>) = print(123)",
				"test project", "pckg", null);
	}
	
	@Test
	public void launchWithTwoSourceFolders() {
		doTest(
				"fun main(args : Array<String>) = print(123)",
				"testProject", "pckg", "src2");
	}
	
	@Test
	public void launchWithTwoSourceFoldersWhichHavingSpace() {
		doTest(
				"fun main(args : Array<String>) = print(123)",
				"testProject", "pckg", "src directory");
	}
}
