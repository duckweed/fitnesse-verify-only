package fitnesse.slim.test.verifyOnly;

/**
 * Copyright AdScale GmbH, Germany, 2007
 */
public class ConstructorDoesntThrowExceptionFixture {
  public ConstructorDoesntThrowExceptionFixture() {
  }

  int i = 0;

  public int getI() {
    return i;
  }

  public void setI(int i) {
    this.i = i;
  }

}
