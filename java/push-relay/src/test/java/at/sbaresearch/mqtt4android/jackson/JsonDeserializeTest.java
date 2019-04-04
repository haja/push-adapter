package at.sbaresearch.mqtt4android.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonDeserializeTest {

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
