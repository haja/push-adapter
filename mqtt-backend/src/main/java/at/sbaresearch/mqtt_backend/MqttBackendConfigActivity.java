package at.sbaresearch.mqtt_backend;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

public class MqttBackendConfigActivity extends AppCompatActivity {

  private static final String TAG = "MqttBackendConfig";

  private static final String SEND_PERM = API.SEND_PERM;
  private static final int SEND_PERM_REQ = 1;

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
      }
    });
  }

  public void connect(final View view) {
    final String msg = "connecting to " + this.server;
    Log.d(TAG, msg);
    Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
        .setAction("Action", null).show();

    mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), server, clientId);
    final MqttConnectOptions options = new MqttConnectOptions();
    options.setUserName(user);
    options.setPassword(pw.toCharArray());
    options.setAutomaticReconnect(true);
    recvCallback = new ReceiveCallback(getApplicationContext());
    mqttAndroidClient.setCallback(recvCallback);
    final IMqttActionListener connectCb = new IMqttActionListener() {
      @Override
      public void onSuccess(IMqttToken asyncActionToken) {
        Log.i(TAG, "connection established");
        Snackbar.make(view, "connection established", Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show();
        try {
          mqttAndroidClient.subscribe(topic, 0);
        } catch (MqttException e) {
          String msgFail = "subscribe failed: " + e.getMessage();
          Log.e(TAG, msgFail);
          Snackbar.make(view, msgFail, Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
        }
      }

      @Override
      public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e(TAG, "connect failed: " + exception.getMessage());
        Snackbar.make(view, "connect failed", Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show();
      }
    };
    AsyncTask<Void, Void, Void> connectTask =
        new VoidVoidVoidAsyncTask(options, connectCb, mqttAndroidClient);
    connectTask.execute();
  }

  public void disconnect(final View view) {
    if (mqttAndroidClient != null) {
      try {
        mqttAndroidClient.disconnect();
      } catch (MqttException e) {
        Log.e(TAG, "disconnect: ", e);
      }
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

  public void reqPermission(View view) {
    if (ContextCompat.checkSelfPermission(this, SEND_PERM)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{SEND_PERM}, SEND_PERM_REQ);
    } else {
      // all fine
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case SEND_PERM_REQ: {
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(this, "perm granted: " + API.SEND_PERM,
              Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(this, "perm NOT granted",
              Toast.LENGTH_SHORT).show();
        }
        break;
      }
    }
  }

  private static class VoidVoidVoidAsyncTask extends AsyncTask<Void, Void, Void> {
    private final MqttConnectOptions options;
    private final IMqttActionListener connectCb;
    private final MqttAndroidClient mqttAndroidClient;

    public VoidVoidVoidAsyncTask(MqttConnectOptions options, IMqttActionListener connectCb, MqttAndroidClient mqttAndroidClient) {
      this.options = options;
      this.connectCb = connectCb;
      this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        this.mqttAndroidClient.connect(options, null, connectCb);
      } catch (MqttException e) {
        Log.e(TAG, "connction failed" + e.getMessage());
      }
      return null;
    }
  }
}
