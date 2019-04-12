package at.sbaresearch.mqtt_backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
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
import at.sbaresearch.mqtt_backend.MqttConnectionManagerService.MqttConnectionBinder;

public class MqttBackendConfigActivity extends AppCompatActivity {

  private static final String TAG = "MqttBackendConfig";

  private static final String SEND_PERM = API.SEND_PERM;
  private static final int SEND_PERM_REQ = 1;

  private MqttConnectionManagerService mqttConnectionManagerService;
  private boolean mqttBound = false;

  private ServiceConnection serviceConnection = new MqttServiceConnection();

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

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "on start");
    final Intent serviceIntent = new Intent(this, MqttConnectionManagerService.class);
    Log.i(TAG, "bind service result: " +
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE));
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i(TAG, "on stop");
    unbindService(serviceConnection);
    mqttBound = false;
  }

  public void disconnect(final View view) {
    if (mqttBound) {
      mqttConnectionManagerService.disconnect();
    } else {
      Snackbar.make(view, "cannot disconnect, service not bound", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
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

  private class MqttServiceConnection implements ServiceConnection {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      Log.i(TAG, "on service bound");
      MqttConnectionBinder binder = (MqttConnectionBinder) service;
      mqttConnectionManagerService = binder.getService();
      mqttBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      Log.i(TAG, "on service unbound");
      mqttBound = false;
    }
  }
}
