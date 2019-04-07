package at.sbaresearch.microg.adapter.backend.registration.device;

import android.content.Context;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.MqttClientAdapter;
import at.sbaresearch.microg.adapter.backend.R;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs.MqttSettings;
import at.sbaresearch.microg.adapter.backend.registration.NullHostNameVerifier;
import at.sbaresearch.microg.adapter.backend.registration.device.HttpRegisterDeviceClient.DeviceRegisterRequest;
import at.sbaresearch.microg.adapter.backend.registration.device.HttpRegisterDeviceClient.DeviceRegisterResponse;
import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import lombok.val;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HttpRegisterDeviceService {
  private static final String TAG = "HttpRegDevSrv";

  private HttpRegisterDeviceClient httpClient;

  public HttpRegisterDeviceService(Context context) throws Exception {
    // normal okhttp pinning does not work with self signed certs
    val factory = new PinningSslFactory(context.getResources().openRawResource(R.raw.server));
    OkHttpClient client = new OkHttpClient.Builder()
        .sslSocketFactory(factory.getSocketFactory(), factory.getTrustManager())
        .hostnameVerifier(new NullHostNameVerifier())
        .build();
    httpClient = new Retrofit.Builder()
        .baseUrl(HttpRegisterDeviceClient.SERVICE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(client)
        .build()
        .create(HttpRegisterDeviceClient.class);
  }

  public void register(Context context) {
    val registerCall = httpClient.registerDevice(new DeviceRegisterRequest("foobar"));
    registerCall.enqueue(new Callback<DeviceRegisterResponse>() {
      @Override
      public void onResponse(Call<DeviceRegisterResponse> call,
          Response<DeviceRegisterResponse> response) {
        Log.i(TAG, "onDeviceRegResponse: " + response.code());
        val resp = response.body();
        if (resp == null) {
          Log.w(TAG, "deviceResponse was null");
          return;
        }

        val prefs = GcmPrefs.get(context);
        val settings = fromRequest(resp.host, resp.port, resp.mqttTopic,
            resp.encodedPrivateKey, resp.encodedCert);
        prefs.setMqttSettings(settings);

        MqttClientAdapter.ensureBackendConnection(context);
      }

      private MqttSettings fromRequest(String host, Integer port, String topic,
          byte[] encodedPrivateKey, byte[] encodedCert) {
        return new MqttSettings(host, port, topic, encodedPrivateKey, encodedCert);
      }

      @Override
      public void onFailure(Call<DeviceRegisterResponse> call, Throwable e) {
        Log.w(TAG, "onDeviceReg failed", e);
      }
    });
  }
}
