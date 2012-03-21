// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.runner;

import util.RegexTestCase;
import util.StreamReader;
import fitnesse.responders.run.TestSummary;

public class CachingResultFormatterTest extends RegexTestCase {
  public void testAddResult() throws Exception {
    CachingResultFormatter formatter = new CachingResultFormatter();
    PageResult result = new PageResult("PageTitle", new TestSummary(1, 2, 3, 4), "content");
    formatter.acceptResult(result);
    formatter.acceptFinalCount(new TestSummary(1, 2, 3, 4));

    String content = new StreamReader(formatter.getResultStream()).read(formatter.getByteCount());
    assertSubString("should get correct message size", "0000000060", content);
    assertSubString(result.toString(), content);
    assertSubString("right tests - should get correct number  ", "0000000001", content);
    assertSubString("wrong tests - should get correct number  ", "0000000002", content);
    assertSubString("ignored tests - should get correct number", "0000000003", content);
    assertSubString("exceptions - should get correct number   ", "0000000004", content);
  }

  public void testIsComposit() throws Exception {
    CachingResultFormatter formatter = new CachingResultFormatter();
    MockResultFormatter mockFormatter = new MockResultFormatter();
    formatter.addHandler(mockFormatter);

    PageResult result = new PageResult("PageTitle", new TestSummary(1, 2, 3, 4), "content");
    formatter.acceptResult(result);
    TestSummary testSummary = new TestSummary(1, 2, 3, 4);
    formatter.acceptFinalCount(testSummary);

    assertEquals(1, mockFormatter.results.size());
    assertEquals(result.toString(), mockFormatter.results.get(0).toString());
    assertEquals(testSummary, mockFormatter.finalSummary);
  }
}
