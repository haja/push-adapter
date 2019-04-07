package at.sbaresearch.mqtt4android.sample.backend;

import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;

@RestController
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppResource {

  private static final String URL_RELAY = "https://localhost:9876/push/{requestId}";

  @NonFinal
  Option<Tuple2<String, byte[]>> currentRegId = Option.none();
  RestTemplateBuilder templateBuilder;

  public AppResource(RestTemplateBuilder builder) {
    this.templateBuilder = builder;
  }

  @PostMapping(value = "/register",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public void register(@RequestBody AppRegistrationRequest request) {
    log.info("register called with registrationId {}", request.getRegistrationId());
    // TODO link with app instance / userId.. mocked for now
    this.currentRegId = Option.of(Tuple.of(
        request.getRegistrationId(),
        request.getRelayCert()
    ));
  }

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public void sendMessage(@RequestBody String message) {
    log.info("sending message: {}", message);
    currentRegId.peek(pushMessage(message))
        .onEmpty(() -> log.warn("not registered, cannot send message"));
  }

  private Consumer<Tuple2<String, byte[]>> pushMessage(
      @RequestBody String message) {
    return regTuple -> {
      val cert = regTuple._2;
      try {
        val requestFactory = setupRequestFactory(cert);
        templateBuilder
            .requestFactory(() -> requestFactory)
            .build()
            .postForLocation(URL_RELAY, message, HashMap.of("requestId", regTuple._1).toJavaMap());
      } catch (Exception e) {
        log.error("cannot create ssl connection", e);
      }
    };
  }

  private HttpComponentsClientHttpRequestFactory setupRequestFactory(byte[] cert) throws Exception {
    val ssl = new PinningSslFactory(new ByteArrayInputStream(cert));

    val sslSF = new SSLConnectionSocketFactory(ssl.getSslContext(), NoopHostnameVerifier.INSTANCE);
    val httpClient = HttpClients.custom()
        .setSSLSocketFactory(sslSF)
        .build();

    val requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }

  @Value
  public static class AppRegistrationRequest {
    String registrationId;
    byte[] relayCert;
  }
}
