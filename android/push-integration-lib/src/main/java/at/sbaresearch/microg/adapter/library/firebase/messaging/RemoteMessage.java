package at.sbaresearch.microg.adapter.library.firebase.messaging;

import java.util.Map;

public class RemoteMessage {
  private final Map<String, String> data;
  private final String from;
  private final String messageId;
  private final long sentTime;

  public RemoteMessage(String from, Map<String, String> data, String messageId, long sentTime) {
    this.from = from;
    this.data = data;
    this.messageId = messageId;
    this.sentTime = sentTime;
  }

  /**
   * Gets the message payload data.
   */
  public Map<String, String> getData() {
    return data;
  }

  /**
   * Get the sender of this message.
   */
  public String getFrom() {
    return from;
  }

  /**
   * Gets the message's ID.
   */
  public String getMessageId() {
    return messageId;
  }

  public final long getSentTime() {
    return sentTime;
  }

}
