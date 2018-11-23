// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: protos-repo/mcs.proto
package at.sbaresearch.microg.adapter.backend.gms.gcm.mcs;

public final class AppData {

  public static final String DEFAULT_KEY = "";
  public static final String DEFAULT_VALUE = "";

  public final String key;

  public final String value;

  public AppData(String key, String value) {
    this.key = key;
    this.value = value;
  }

  private AppData(Builder builder) {
    this(builder.key, builder.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AppData appData = (AppData) o;

    if (key != null ? !key.equals(appData.key) : appData.key != null) return false;
    return value != null ? value.equals(appData.value) : appData.value == null;
  }

  @Override
  public int hashCode() {
    int result = key != null ? key.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  public static final class Builder {

    public String key;
    public String value;

    public Builder() {
    }

    public Builder(AppData message) {
      if (message == null) return;
      this.key = message.key;
      this.value = message.value;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder value(String value) {
      this.value = value;
      return this;
    }

    public AppData build() {
      return new AppData(this);
    }
  }
}
