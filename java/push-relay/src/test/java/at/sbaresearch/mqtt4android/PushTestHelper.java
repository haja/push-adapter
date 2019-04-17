package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.relay.web.PushRequest;
import at.sbaresearch.mqtt4android.relay.web.PushRequest.Message;
import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("it")
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PushTestHelper {

  public Message.MessageBuilder pushMessageBuilder() {
    return Message.builder()
        .token("token-not-set")
        .data(HashMap.of("data1", "value1").toJavaMap());
  }

  public Message.MessageBuilder pushMessageBuilder(String messageData) {
    val data = HashMap.of("message", messageData);
    return pushMessageBuilder()
        .data(data.toJavaMap());
  }

  public static PushRequest toReq(Message.MessageBuilder msgBuilder) {
    return PushRequest.builder()
        .message(msgBuilder.build())
        .build();
  }
}
