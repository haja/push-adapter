package at.sbaresearch.mqtt4android.integration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("it")
@RunWith(SpringRunner.class)
@SpringBootTest
abstract public class AppTest {

}
