/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * This file is part of push-relay.
 *
 *     push-relay is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     push-relay is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with push-relay.  If not, see <https://www.gnu.org/licenses/>.
 */

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
