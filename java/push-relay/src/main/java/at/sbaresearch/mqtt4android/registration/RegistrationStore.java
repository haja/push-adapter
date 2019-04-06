package at.sbaresearch.mqtt4android.registration;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationStore {

  @NonFinal
  Map<String, String> store = HashMap.empty();

  public void register(String token, String topic) {
    // TODO store in DB / persist across restarts
    store = store.put(token, topic);
  }

  public String getTopic(String token) {
    return store.get(token).getOrElseThrow(() -> new KeyNotFoundException(token));
  }

  public class KeyNotFoundException extends RuntimeException {
    public KeyNotFoundException(String token) {
      super("key not found: " + token);
    }
  }
}
