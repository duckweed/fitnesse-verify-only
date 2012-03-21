package fitnesse.slim.test.verifyOnly;

/**
 * Copyright AdScale GmbH, Germany, 2007
 */
public class ConstructorThrowsExceptionFixture {
  public ConstructorThrowsExceptionFixture() {
    throw new RuntimeException("aaaaaaaaaaarrrrrrrrrrrrrgggggggggggggghhhhhhhhhhhhh");
  }

  int i = 0;

  boolean day;

  public boolean getDay() {
    return day;
  }

  public boolean isDay() {
    return getDay();
  }

  public void setDay(boolean day) {
    this.day = day;
  }

  public int getI() {
    return i;
  }

  public void setI(int i) {
    this.i = i;
  }
}
