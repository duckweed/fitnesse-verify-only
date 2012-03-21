// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.slim;

import fitnesse.html.HtmlUtil;
import fitnesse.slim.converters.VoidConverter;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static util.ListUtility.list;

// Extracted Test class to be implemented by all Java based Slim ports
// The tests for PhpSlim and JsSlim implement this class

public abstract class ListExecutorTestBase {
  protected List<Object> statements;
  protected ListExecutor executor;
  protected List<Object> expectedResults = new ArrayList<Object>();
  protected String testClass = "TestSlim";

  protected abstract ListExecutor getListExecutor() throws Exception;

  protected abstract String getTestClassPath();

  @Before
  public void setup() throws Exception {
    executor = getListExecutor();
    statements = new ArrayList<Object>();
    statements.add(list("i1", "import", getTestClassPath()));
    statements.add(list("m1", "make", "testSlim", testClass));
    expectedResults.add(list("i1", "OK"));
    expectedResults.add(list("m1", "OK"));
  }

  @Test
  public void checkSetup() {
    respondsWith(list());
  }

  @Test()
  public void invalidOperation() throws Exception {
    statements.add(list("inv1", "invalidOperation"));
    assertExceptionReturned("message:<<INVALID_STATEMENT: invalidOperation.>>", "inv1");
  }

  @Test(expected = SlimError.class)
  public void malformedStatement() throws Exception {
    statements.add(list("id", "call", "notEnoughArguments"));
    assertExceptionReturned("XX", "id");
  }

  @Test
  public void noSuchInstance() throws Exception {
    statements.add(list("id", "call", "noSuchInstance", "noSuchMethod"));
    assertExceptionReturned("message:<<NO_INSTANCE noSuchInstance.>>", "id");
  }

  @Test
  public void emptyListReturnsNicely() throws Exception {
    statements.clear();
    executor.execute(statements);
    expectedResults.clear();
    respondsWith(list());
  }

  @Test
  public void createWithFullyQualifiedNameWorks() throws Exception {
    statements.clear();
    statements.add(list("m1", "make", "testSlim", getTestClassPath() + "." + testClass));
    expectedResults.clear();
    respondsWith(list(list("m1", "OK")));
  }

  @Test
  public void exceptionInConstructorIsPassedThrough() throws Exception {
    statements.clear();
    expectedResults.clear();
    statements.add(list("m1", "make", "x", getTestClassPath() + ".ConstructorThrows", "thrown message"));
    assertExceptionReturned("thrown message", "m1");
  }

  protected void respondsWith(List<Object> expected) {
    assertEquals(expectedMap(expected), resultMap());
  }

  Map<String, Object> resultMap() {
    List<Object> result = executor.execute(statements);
    return SlimClient.resultToMap(result);
  }

  private Map<String, Object> expectedMap(List<Object> expected) {
    expectedResults.addAll(expected);
    return SlimClient.resultToMap(expectedResults);
  }


  protected void assertExceptionReturned(String message, String returnTag) {
    Map<String, Object> results = SlimClient.resultToMap(executor.execute(statements));
    String result = (String) results.get(returnTag);
    assertTrue(result, result.indexOf(message) != -1);
    assertTrue(result, result.indexOf(SlimServer.EXCEPTION_TAG) != -1);
  }


}
