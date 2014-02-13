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
	public void testBeforeDotInCall() {
		doTest("testData/completion/basic/common/BeforeDotInCall.kt");
	}

	@Test
	public void testCallLocalLambda() {
		doTest("testData/completion/basic/common/CallLocalLambda.kt");
	}

	@Test
	public void testClassObjectElementsInClass() {
		doTest("testData/completion/basic/common/ClassObjectElementsInClass.kt");
	}

	@Test
	public void testClassRedeclaration1() {
		doTest("testData/completion/basic/common/ClassRedeclaration1.kt");
	}

	@Test
	public void testClassRedeclaration2() {
		doTest("testData/completion/basic/common/ClassRedeclaration2.kt");
	}

	@Test
	public void testExtendClassName() {
		doTest("testData/completion/basic/common/ExtendClassName.kt");
	}

	@Test
	public void testExtendQualifiedClassName() {
		doTest("testData/completion/basic/common/ExtendQualifiedClassName.kt");
	}

	@Test
	public void testExtensionForProperty() {
		doTest("testData/completion/basic/common/ExtensionForProperty.kt");
	}

	@Test
	public void testExtensionFunReceiver() {
		doTest("testData/completion/basic/common/ExtensionFunReceiver.kt");
	}

	@Test
	public void testExtensionFunReceiverForce() {
		doTest("testData/completion/basic/common/ExtensionFunReceiverForce.kt");
	}

	@Test
	public void testExtensionInsideFunction() {
		doTest("testData/completion/basic/common/ExtensionInsideFunction.kt");
	}

	@Test
	public void testExtensionToIntInFloatStyle() {
		doTest("testData/completion/basic/common/ExtensionToIntInFloatStyle.kt");
	}

	@Test
	public void testFromImports() {
		doTest("testData/completion/basic/common/FromImports.kt");
	}

	@Test
	public void testFunctionCompletionFormatting() {
		doTest("testData/completion/basic/common/FunctionCompletionFormatting.kt");
	}

	@Test
	public void testInCallExpression() {
		doTest("testData/completion/basic/common/InCallExpression.kt");
	}

	@Test
	public void testInClassInitializer() {
		doTest("testData/completion/basic/common/InClassInitializer.kt");
	}

	@Test
	public void testInClassPropertyAccessor() {
		doTest("testData/completion/basic/common/InClassPropertyAccessor.kt");
	}

	@Test
	public void testInEmptyImport() {
		doTest("testData/completion/basic/common/InEmptyImport.kt");
	}

	@Test
	public void testInExpressionNoPrefix() {
		doTest("testData/completion/basic/common/InExpressionNoPrefix.kt");
	}

	@Test
	public void testInExtendTypeAnnotation() {
		doTest("testData/completion/basic/common/InExtendTypeAnnotation.kt");
	}

	@Test
	public void testInFileWithMultiDeclaration() {
		doTest("testData/completion/basic/common/InFileWithMultiDeclaration.kt");
	}

	@Test
	public void testInFileWithTypedef() {
		doTest("testData/completion/basic/common/InFileWithTypedef.kt");
	}

	@Test
	public void testInFunInClassInitializer() {
		doTest("testData/completion/basic/common/InFunInClassInitializer.kt");
	}

	@Test
	public void testInGlobalPropertyInitializer() {
		doTest("testData/completion/basic/common/InGlobalPropertyInitializer.kt");
	}

	@Test
	public void testInImport() {
		doTest("testData/completion/basic/common/InImport.kt");
	}

	@Test
	public void testInInitializerInPropertyAccessor() {
		doTest("testData/completion/basic/common/InInitializerInPropertyAccessor.kt");
	}

	@Test
	public void testInLocalObjectDeclaration() {
		doTest("testData/completion/basic/common/InLocalObjectDeclaration.kt");
	}

	@Test
	public void testInLongDotQualifiedExpression() {
		doTest("testData/completion/basic/common/InLongDotQualifiedExpression.kt");
	}

	@Test
	public void testInMiddleOfPackage() {
		doTest("testData/completion/basic/common/InMiddleOfPackage.kt");
	}

	@Test
	public void testInMiddleOfPackageDirective() {
		doTest("testData/completion/basic/common/InMiddleOfPackageDirective.kt");
	}

	@Test
	public void testInObjectInDelegationSpecifier() {
		doTest("testData/completion/basic/common/InObjectInDelegationSpecifier.kt");
	}

	@Test
	public void testInPackageBegin() {
		doTest("testData/completion/basic/common/InPackageBegin.kt");
	}

	@Test
	public void testInParametersTypes() {
		doTest("testData/completion/basic/common/InParametersTypes.kt");
	}

	@Test
	public void testInParametersTypesForce() {
		doTest("testData/completion/basic/common/InParametersTypesForce.kt");
	}

	@Test
	public void testInTypeAnnotation() {
		doTest("testData/completion/basic/common/InTypeAnnotation.kt");
	}

	@Test
	public void testJavaPackage() {
		doTest("testData/completion/basic/common/JavaPackage.kt");
	}

	@Test
	public void testLocalMultideclarationValues() {
		doTest("testData/completion/basic/common/LocalMultideclarationValues.kt");
	}

	@Test
	public void testNamedObject() {
		doTest("testData/completion/basic/common/NamedObject.kt");
	}

	@Test
	public void testNoClassNameDuplication() {
		doTest("testData/completion/basic/common/NoClassNameDuplication.kt");
	}

	@Test
	public void testNoCompletionAfterBigFloat() {
		doTest("testData/completion/basic/common/NoCompletionAfterBigFloat.kt");
	}

	@Test
	public void testNoCompletionAfterFloat() {
		doTest("testData/completion/basic/common/NoCompletionAfterFloat.kt");
	}

	@Test
	public void testNoCompletionAfterInt() {
		doTest("testData/completion/basic/common/NoCompletionAfterInt.kt");
	}

	@Test
	public void testNoCompletionAfterLong() {
		doTest("testData/completion/basic/common/NoCompletionAfterLong.kt");
	}

	@Test
	public void testNoEmptyPackage() {
		doTest("testData/completion/basic/common/NoEmptyPackage.kt");
	}

	@Test
	public void testNoObjectInTypePosition() {
		doTest("testData/completion/basic/common/NoObjectInTypePosition.kt");
	}

	@Test
	public void testNoTopLevelCompletionInQualifiedUserTypes() {
		doTest("testData/completion/basic/common/NoTopLevelCompletionInQualifiedUserTypes.kt");
	}

	@Test
	public void testObjectRedeclaration1() {
		doTest("testData/completion/basic/common/ObjectRedeclaration1.kt");
	}

	@Test
	public void testObjectRedeclaration2() {
		doTest("testData/completion/basic/common/ObjectRedeclaration2.kt");
	}

	@Test
	public void testOnlyScopedClassesWithoutExplicit() {
		doTest("testData/completion/basic/common/OnlyScopedClassesWithoutExplicit.kt");
	}

	@Test
	public void testOverloadFunctions() {
		doTest("testData/completion/basic/common/OverloadFunctions.kt");
	}

	@Test
	public void testShortClassNamesInTypePosition() {
		doTest("testData/completion/basic/common/ShortClassNamesInTypePosition.kt");
	}

	@Test
	public void testStandardJetArrayFirst() {
		doTest("testData/completion/basic/common/StandardJetArrayFirst.kt");
	}

	@Test
	public void testStandardJetDoubleFirst() {
		doTest("testData/completion/basic/common/StandardJetDoubleFirst.kt");
	}

	@Test
	public void testSubpackageInFun() {
		doTest("testData/completion/basic/common/SubpackageInFun.kt");
	}

	@Test
	public void testTopLevelClassCompletionInQualifiedCall() {
		doTest("testData/completion/basic/common/TopLevelClassCompletionInQualifiedCall.kt");
	}

	@Test
	public void testVariableClassName() {
		doTest("testData/completion/basic/common/VariableClassName.kt");
	}

	@Test
	public void testVisibilityClassMembersFromExternal() {
		doTest("testData/completion/basic/common/VisibilityClassMembersFromExternal.kt");
	}

	@Test
	public void testVisibilityClassMembersFromExternalForce() {
		doTest("testData/completion/basic/common/VisibilityClassMembersFromExternalForce.kt");
	}

	@Test
	public void testVisibilityInSubclass() {
		doTest("testData/completion/basic/common/VisibilityInSubclass.kt");
	}

	@Test
	public void testVisibilityInSubclassForce() {
		doTest("testData/completion/basic/common/VisibilityInSubclassForce.kt");
	}

}
