package at.sbaresearch.mqtt_backend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

public class MqttBackendConfigActivity extends AppCompatActivity {

  private static final String TAG = "MqttBackendConfig";

  private MqttAndroidClient mqttAndroidClient;
  private String clientId = "test1";
  private String server = "tcp://10.0.2.2:61613";
  private String user = "admin";
  private String pw = "password";
  private String topic = "foo";

  private MqttCallback recvCallback;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mqtt_backend_config);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "connecting..", Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show();
        connect();
      }
    });
  }

  private void connect() {
    mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), server, clientId);
    final MqttConnectOptions options = new MqttConnectOptions();
    options.setUserName(user);
    options.setPassword(pw.toCharArray());
    options.setAutomaticReconnect(true);
    recvCallback = new ReceiveCallback(getApplicationContext());
    try {
      mqttAndroidClient.setCallback(recvCallback);
      IMqttActionListener connectCb = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          Log.i(TAG, "connection established");
          try {
            mqttAndroidClient.subscribe(topic, 0);
          } catch (MqttException e) {
            Log.e(TAG, "subscribe failed: " + e.getMessage());
          }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          Log.e(TAG, "connect failed: " + exception.getMessage());
        }
      };
      mqttAndroidClient.connect(options, null, connectCb);
    } catch (MqttException e) {
      Log.e(TAG, "error while connecting:" + e.getMessage());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_mqtt_backend_config, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
