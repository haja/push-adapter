package at.sbaresearch.mqtt4android;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("it")
@RunWith(SpringRunner.class)
@Sql("/testdata.sql")
// we need a webEnvironment for our http tests.
// this is defined globally so we do not need to dirty our context before + after http tests.
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
abstract public class AppTest {

}
