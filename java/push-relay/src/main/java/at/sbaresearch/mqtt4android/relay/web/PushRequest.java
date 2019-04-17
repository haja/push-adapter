package at.sbaresearch.mqtt4android.relay.web;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;

/**
 * TODO this should be FCM/relay compatible API definition
 * see for proper definition
 * https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send
 */
@Value
@Builder
public class PushRequest {
  Boolean validate_only;
  Message message;

  @Value
  @Builder
  public static class Message {
    HashMap<String, String> data;
    String token;
  }
}
