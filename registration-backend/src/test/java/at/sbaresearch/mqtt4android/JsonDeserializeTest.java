import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonDeserializeTest {

  @Autowired
  ObjectMapper mapper;

  @Test
  public void lombokValueTest() {
    String json = "{ 'field': 'value' }";
    mapper
  }
}
