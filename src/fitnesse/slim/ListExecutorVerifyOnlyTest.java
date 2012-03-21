// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.slim;

import fitnesse.html.HtmlUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static util.ListUtility.list;

public class ListExecutorVerifyOnlyTest extends ListExecutorTestBase {

  static final String VERIFIED = "verified:";


  @Before
  public void before() {
    MethodExecutor.setVerifyOnly(true);
    StatementExecutor.setVerifyOnly(true);
  }

  @After
  public void after() {
    MethodExecutor.setVerifyOnly(false);
    StatementExecutor.setVerifyOnly(false);
  }

  @Override
  protected ListExecutor getListExecutor() throws Exception {
    SlimFactory slimFactory = new JavaSlimFactory();
    return slimFactory.getListExecutor(false, false);
  }

  @Override
  protected String getTestClassPath() {
    return "fitnesse.slim.test";
  }

  @Test
  public void oneFunctionCallVerbose_forVerify() throws Exception {
    executor.verbose = true;
    PrintStream oldOut = System.out;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    System.setOut(new PrintStream(os));

    statements.add(list("id", "call", "testSlim", "returnString"));
    executor.execute(statements);

    System.setOut(oldOut);
    assertEquals("!1 Instructions" + HtmlUtil.ENDL +
      "[i1, import, fitnesse.slim.test]\n" +
      HtmlUtil.ENDL +
      "[i1, OK]" + HtmlUtil.ENDL +
      "------" + HtmlUtil.ENDL +
      "[m1, make, testSlim, TestSlim]\n" +
      HtmlUtil.ENDL +
      "[m1, OK]" + HtmlUtil.ENDL +
      "------" + HtmlUtil.ENDL +
      "[id, call, testSlim, returnString]\n" +
      HtmlUtil.ENDL +
      "[id, verified:]" + HtmlUtil.ENDL +
      "------" + HtmlUtil.ENDL, os.toString());
  }

  @Test
  public void oneFunctionCallWithBlankArgument_forVerify() throws Exception {
    statements.add(list("id", "call", "testSlim", "echoString", ""));
    respondsWith(list(list("id", VERIFIED)));
  }

  @Test
  public void oneFunctionCallToShowThatLaterImportsTakePrecedence_forVerify() throws Exception {
    statements.add(0, list("i2", "import", getTestClassPath() + ".testSlimInThisPackageShouldNotBeTheOneUsed"));
    statements.add(list("id", "call", "testSlim", "returnString"));
    expectedResults.add(0, list("i2", "OK"));
    respondsWith(list(list("id", VERIFIED)));
  }

  @Test
  public void canPassArgumentsToConstructor_forVerify() throws Exception {
    statements.add(list("m2", "make", "testSlim2", testClass, "3"));
    statements.add(list("c1", "call", "testSlim2", "returnConstructorArg"));
    statements.add(list("c2", "call", "testSlim", "returnConstructorArg"));
    respondsWith(
      list(
        list("m2", "OK"),
        list("c1", VERIFIED),
        list("c2", VERIFIED)
      )
    );
  }

  @Test
  public void multiFunctionCall_forVerify() throws Exception {
    statements.add(list("id1", "call", "testSlim", "addTo", "1", "2"));
    statements.add(list("id2", "call", "testSlim", "addTo", "3", "4"));
    respondsWith(list(list("id1", VERIFIED), list("id2", VERIFIED)));
  }

  @Test
  public void callAndAssign_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "addTo", "5", "6"));
    statements.add(list("id2", "call", "testSlim", "echoInt", "$v"));
    respondsWith(list(list("id1", VERIFIED), list("id2", VERIFIED)));
  }

  @Test
  public void canReplaceMultipleSymbolsInAnArgument_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v1", "testSlim", "echoString", "Bob"));
    statements.add(list("id2", "callAndAssign", "v2", "testSlim", "echoString", "Martin"));
    statements.add(list("id3", "call", "testSlim", "echoString", "name: $v1 $v2"));
    respondsWith(list(list("id1", VERIFIED), list("id2", VERIFIED), list("id3", VERIFIED)));
  }

  @Test
  public void canReplaceMultipleSymbolsInAnArgumentWhenOneVarIsPrefixOfAnother_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "echoString", "Bob"));
    statements.add(list("id2", "callAndAssign", "v1", "testSlim", "echoString", "Martin"));
    statements.add(list("id3", "call", "testSlim", "echoString", "name: $v $v1"));
    respondsWith(list(list("id1", VERIFIED), list("id2", VERIFIED), list("id3", VERIFIED)));
  }

  @Test
  public void canReplaceSymbolWhenValueIsNull_forVerify() throws Exception {
    statements.add(list("id1", "make", "nf", "NullFixture"));
    statements.add(list("id2", "callAndAssign", "v", "nf", "getNull"));
    statements.add(list("id3", "call", "testSlim", "echoString", "$v"));
    respondsWith(list(list("id1", "OK"), list("id2", VERIFIED), list("id3", VERIFIED)));
  }

  @Test
  public void passAndReturnList_forVerify() throws Exception {
    List<String> l = list("one", "two");
    statements.add(list("id", "call", "testSlim", "echoList", l));
    respondsWith(list(list("id", VERIFIED)));
  }

  @Test
  public void passAndReturnListWithVariable_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "addTo", "3", "4"));
    statements.add(list("id2", "call", "testSlim", "echoList", list("$v")));
    respondsWith(list(list("id1", VERIFIED), list("id2", VERIFIED)));
  }

  @Test
  public void callToVoidFunctionReturnsVoidValue_forVerify() throws Exception {
    statements.add(list("id", "call", "testSlim", "voidFunction"));
    respondsWith(list(list("id", VERIFIED)));
  }

  @Test
  public void callToFunctionReturningNull_forVerify() throws Exception {
    statements.add(list("id", "call", "testSlim", "nullString"));
    respondsWith(list(list("id", VERIFIED)));
  }

  @Test
  public void methodAcceptsTestSlimFromSymbol_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "createTestSlimWithString",
      "test string"));
    statements.add(list("id2", "call", "testSlim", "getStringFromOther", "$v"));
    respondsWith(list(list("id1", VERIFIED), list("id2", VERIFIED)));
  }

  //  @Test
  public void methodAcceptsObjectFromSymbol_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "createTestSlimWithString", "test string"));
    statements.add(list("id2", "call", "testSlim", "isSame", "$v"));
//    statements.add(list("m2", "make", "chainedTestSlim", "$v"));
//    statements.add(list("id3", "call", "chainedTestSlim", "isSame", "$v"));

    respondsWith(list(list("id1", "TestSlim: 0, test string"), list("id2", "false"), list("m2", "OK"), list("id3", "true")));
  }

  @Test
  public void givenASymbolWillVerifyMethodExists() throws Exception {
    String id2 = "id2";
    statements.add(list(id2, "call", "testSlim", "isSame", "$v"));
    String value = runSingleLineAndGetRes(id2);
    assertEquals(VERIFIED, value);
  }

  @Test
  public void cantMakeObjectFromSymbolWithVerifyOnly() throws Exception {
    statements.add(list("m2", "make", "chainedTestSlim", "$v"));
    String value = runSingleLineAndGetRes("m2");
    assertTrue(value.contains("<<COULD_NOT_INVOKE_CONSTRUCTOR $v[0]>>"));
  }


  @Test
  public void fixtureChainingWithAssignmentFromFactory_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "createTestSlimWithString", "test string"));
    statements.add(list("m2", "make", "chainedTestSlim", "$v"));

    Map<String, Object> map = resultMap();
    assertEquals(4, map.size());
    String m2 = (String) map.get("m2");
    assertTrue("should not be able to chain make with verify only - '" + m2 + "'", m2.contains("<<COULD_NOT_INVOKE_CONSTRUCTOR $v"));
  }

  @Test
  public void constructorAcceptsTestSlimFromSymbol_forVerify() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "createTestSlimWithString",
      "test string"));
    respondsWith(list(
      list("id1", VERIFIED)
    ));
  }

  @Test
  public void constructorWillNotAcceptTestSlimFromSymbol_forVerify_2() throws Exception {
    statements.add(list("id1", "callAndAssign", "v", "testSlim", "createTestSlimWithString",
      "test string"));
    statements.add(list("m2", "make", "newTestSlim", testClass, "4", "$v"));

    Map<String, Object> map = resultMap();
    assertEquals(4, map.size());
    assertTrue("should not be able to chain make with verify only", ((String) map.get("m2")).contains("<<COULD_NOT_INVOKE_CONSTRUCTOR TestSlim[2]>>"));
  }

  private String runSingleLineAndGetRes(String id) {
    Map<String, Object> resultMap = SlimClient.resultToMap(executor.execute(statements));
    assertEquals(3, resultMap.size());
    String value = (String) resultMap.get(id);
    assertNotNull(value);
    return value;
  }
}
