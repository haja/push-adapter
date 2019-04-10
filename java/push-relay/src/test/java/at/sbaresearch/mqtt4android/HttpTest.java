package at.sbaresearch.mqtt4android;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;

// this exends app test so the application context stays the same
abstract public class HttpTest extends AppTest {
  protected final String hostPort = "https://localhost:9870";
  @Autowired
  protected RestTemplateBuilder restBuilder;

  @Before
  public void setup() {
    restBuilder = restBuilder.rootUri(hostPort);
  }
}
