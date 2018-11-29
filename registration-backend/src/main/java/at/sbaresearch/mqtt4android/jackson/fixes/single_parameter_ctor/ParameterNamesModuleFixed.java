package at.sbaresearch.mqtt4android.jackson.fixes.single_parameter_ctor;

import at.sbaresearch.mqtt4android.jackson.fixes.single_parameter_ctor.ParameterNamesAnnotationIntrospectorFix.ParameterExtractor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.PackageVersion;

public class ParameterNamesModuleFixed extends SimpleModule {
  private static final long serialVersionUID = 1L;

  private final JsonCreator.Mode creatorBinding;

  public ParameterNamesModuleFixed(JsonCreator.Mode creatorBinding) {
    super(PackageVersion.VERSION);
    this.creatorBinding = creatorBinding;
  }

  public ParameterNamesModuleFixed() {
    super(PackageVersion.VERSION);
    this.creatorBinding = null;
  }

  @Override
  public void setupModule(SetupContext context) {
    super.setupModule(context);
    context.insertAnnotationIntrospector(new ParameterNamesAnnotationIntrospectorFix(creatorBinding, new ParameterExtractor()) {

    });
  }

  @Override
  public int hashCode() { return getClass().hashCode(); }

  @Override
  public boolean equals(Object o) { return this == o; }
}
