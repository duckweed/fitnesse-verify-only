// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.responders.run.formatters;

import java.io.IOException;

import util.TimeMeasurement;
import fitnesse.FitNesseContext;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.html.TagGroup;
import fitnesse.responders.run.TestPage;
import fitnesse.responders.run.TestSummary;
import fitnesse.responders.run.TestSystem;
import fitnesse.wiki.WikiPage;

public abstract class TestHtmlFormatter extends InteractiveFormatter {
  protected TimeMeasurement latestTestTime;

  public TestHtmlFormatter(FitNesseContext context, final WikiPage page) {
    super(context, page);
  }

  //special constructor for TestRunner.  Used only for formatting.
  //todo this is nasty coupling. 
  public TestHtmlFormatter(FitNesseContext context) {
    super(context, null);
  }

  @Override
  public void newTestStarted(TestPage test, TimeMeasurement timeMeasurement) throws IOException {
    writeData(getPage().getData().getHeaderPageHtml());
  }

  @Override
  public void testSystemStarted(TestSystem testSystem, String testSystemName, String testRunner) {
  }

  @Override
  public void testComplete(TestPage testPage, TestSummary testSummary, TimeMeasurement timeMeasurement) throws IOException {
    super.testComplete(testPage, testSummary, timeMeasurement);
    latestTestTime = timeMeasurement;

    processTestResults(getRelativeName(testPage), testSummary);
  }

  public void processTestResults(String relativeName, TestSummary testSummary) throws IOException {
    getAssertionCounts().add(testSummary);
  }

  @Override
  public void testOutputChunk(String output) throws IOException {
    writeData(output);
  }

  @Override
  public void allTestingComplete(TimeMeasurement totalTimeMeasurement) throws IOException {
    super.allTestingComplete(totalTimeMeasurement);
    removeStopTestLink();
    publishAndAddLog();
    finishWritingOutput();
    close();
  }

  @Override
  protected void finishWritingOutput() throws IOException {
    writeData(testSummary());
<<<<<<< HEAD
    writeData("<br/><div class=\"footer\">\n");
    writeData(getPage().getData().getFooterPageHtml());
    writeData("</div>\n");
    if (htmlPage != null)
      writeData(htmlPage.postDivision);
  }

  protected void publishAndAddLog() throws IOException {
    if (log != null) {
      log.publish();
      writeData(executionStatus(log));
    }
  }

  protected String cssClassFor(TestSummary testSummary) {
    boolean anyWrong = testSummary.getWrong() > 0;
    boolean anyExceptions = testSummary.getExceptions() > 0;
    boolean noTests = testSummary.getRight() + testSummary.getIgnores() == 0;
    boolean anyVerified = testSummary.getVerified() > 0;

    if (anyWrong || wasInterupted) {
      return "fail";
    }
    if (anyExceptions) {
      return "error";
    }
    if (noTests && ! anyVerified) {
      return "error";
    }
    if (anyVerified) {
      return "verified";
    }
    return "pass";
  }


  public String executionStatus(CompositeExecutionLog logs) {
    return logs.executionStatusHtml();
=======
    super.finishWritingOutput();
>>>>>>> e9ce61dc0a4dfd8e58bb774cf471f15fea33f11f
  }

  protected String makeSummaryContent() {
    String summaryContent;
    if (latestTestTime != null) {
      summaryContent = String.format("<strong>Assertions:</strong> %s (%.03f seconds)", getAssertionCounts(), latestTestTime.elapsedSeconds());
    } else {
      summaryContent = String.format("<strong>Assertions:</strong> %s ", getAssertionCounts());
    }
    return summaryContent;
  }

  @Override
  public int getErrorCount() {
    return getAssertionCounts().getWrong() + getAssertionCounts().getExceptions();
  }

  @Override
  public void addMessageForBlankHtml() throws Exception {
    TagGroup html = new TagGroup();
    HtmlTag h2 = new HtmlTag("h2");
    h2.add("Oops!  Did you forget to add to some content to this ?");
    html.add(h2.html());
    html.add(HtmlUtil.HR.html());
    writeData(html.html());
  }

  @Override
  public void errorOccured() {
    latestTestTime = null;
    super.errorOccured();
  }
}
