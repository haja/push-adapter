package at.sbaresearch.mqtt4android.jackson;

public class TestSinglePojo {
  String singleProp;

  //@java.beans.ConstructorProperties({"singleProp"})
  public TestSinglePojo(String singleProp) {
    this.singleProp = singleProp;
  }

  public String getSingleProp() {
    return singleProp;
  }
}
