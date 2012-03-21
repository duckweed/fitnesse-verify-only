package fitnesse.slim;

public abstract class SlimFactory {

  public abstract NameTranslator getMethodNameTranslator();

  public SlimServer getSlimServer(boolean verbose, boolean verifyOnly) {
    return new SlimServer(verbose, verifyOnly, this);
  }

  public ListExecutor getListExecutor(boolean verbose, boolean verifyOnly) {
    return new ListExecutor(verbose, verifyOnly, this);
  }

  public abstract StatementExecutorInterface getStatementExecutor();

  public void stop() {
  }

}
