package at.sbaresearch.mqtt4android.jackson;

public class TestPojo {
  private final String prop;
  private final String prop2;

  //@java.beans.ConstructorProperties({"prop", "prop2"})
  public TestPojo(String prop, String prop2) {
    this.prop = prop;
    this.prop2 = prop2;
  }

  public String getProp2() {
    return prop2;
  }

  public String getProp() {
    return this.prop;
  }


}
