package at.sbaresearch.mqtt4android;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static at.sbaresearch.mqtt4android.relay.web.PushResource.PushDto.PushDtoBuilder;
import static at.sbaresearch.mqtt4android.relay.web.PushResource.PushDto.builder;


@Profile("it")
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PushTestHelper {

  public PushDtoBuilder pushMessageBuilder() {
    return builder()
        .name("this is a test")
        .token("token-not-set")
        .data(HashMap.of("data1", "value1"));
  }
}
