package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.testdata.Clients;
import at.sbaresearch.mqtt4android.testdata.Registrations;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test-setup")
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class TestData {
  Clients clients;
  Registrations registrations;
}
