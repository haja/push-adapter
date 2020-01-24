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

package at.sbaresearch.mqtt4android.jackson;

import at.sbaresearch.mqtt4android.AppTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

public class JsonDeserializeTest extends AppTest {

  @Autowired
  ObjectMapper mapper;
  private final String json = "{ \"prop\": \"value\", \"prop2\": \"value2\" }";
  // FIXME this is not working (one prop on json)
  //  Jackson then tries unwrapping or something...
  //  see
  private final String jsonSingle = "{ \"singleProp\": \"singleValue\" }";

  @Test
  public void pojoValueTest() throws IOException {
    TestPojo x = mapper.readValue(json, TestPojo.class);
    assertThat(x).isNotNull();
    assertThat(x.getProp()).isEqualTo("value");
    assertThat(x.getProp2()).isEqualTo("value2");
  }

  @Test
  public void lombokValueTest() throws IOException {
    LombokPojo x = mapper.readValue(json, LombokPojo.class);
    assertThat(x).isNotNull();
    assertThat(x.getProp()).isEqualTo("value");
    assertThat(x.getProp2()).isEqualTo("value2");
  }

  @Test
  public void pojoSingleValueTest() throws IOException {
    TestSinglePojo x = mapper.readValue(jsonSingle, TestSinglePojo.class);
    assertThat(x).isNotNull();
    assertThat(x.getSingleProp()).isEqualTo("singleValue");
  }

  @Test
  public void lombokSingleValueTest() throws IOException {
    LombokSinglePojo x = mapper.readValue(jsonSingle, LombokSinglePojo.class);
    assertThat(x).isNotNull();
    assertThat(x.getSingleProp()).isEqualTo("singleValue");
  }
}
