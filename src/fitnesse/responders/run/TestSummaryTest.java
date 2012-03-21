package fitnesse.responders.run;

import fitnesse.FitNesseContext;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestSummaryTest {
  @Test
  public void tallyPageCountsShouldCountPagesCorrectly() throws Exception {
    TestSummary pageCounts = new TestSummary(0, 0, 0, 0);
    pageCounts.tallyPageCounts(new TestSummary(32, 0, 0, 0)); // 1 right.
    pageCounts.tallyPageCounts(new TestSummary(0, 99, 0, 0)); // 1 wrong.
    pageCounts.tallyPageCounts(new TestSummary(0, 0, 0, 0)); // 1 ignore.
    pageCounts.tallyPageCounts(new TestSummary(0, 0, 0, 88)); // 1 exception.
    pageCounts.tallyPageCounts(new TestSummary(20, 1, 0, 0)); // 1 wrong;
    pageCounts.tallyPageCounts(new TestSummary(20, 20, 0, 20)); // 1 wrong;
    pageCounts.tallyPageCounts(new TestSummary(20, 0, 0, 20)); // 1 exception;
    Assert.assertEquals(new TestSummary(1, 3, 1, 2), pageCounts);
  }

  @Test
  public void toStringWorksCorrectly() throws Exception {
    assertEquals("0 right, 0 wrong, 0 ignored, 0 exceptions", new TestSummary(0, 0, 0, 0).toString());
    assertEquals("11 right, 13 wrong, 17 ignored, 19 exceptions", new TestSummary(11, 13, 17, 19).toString());
  }

  @Test
  public void toStringWithVerifyOnly() throws Exception {
    setVerifyOnlyOn();
    TestSummary testSummary = new TestSummary(23, 29, 31, 37);
    testSummary.verified = 41;
    assertEquals("23 right, 29 wrong, 31 ignored, 37 exceptions, 41 verified", testSummary.toString());
  }

  private void setVerifyOnlyOn() {
    FitNesseContext.globalContext = new FitNesseContext();
    FitNesseContext.setVerifyOnly(true);
  }
}
