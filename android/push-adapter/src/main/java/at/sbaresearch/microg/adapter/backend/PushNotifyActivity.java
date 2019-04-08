package at.sbaresearch.microg.adapter.backend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import at.sbaresearch.microg.adapter.backend.registration.device.RegisterDeviceService;

import java.util.Arrays;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

public class PushNotifyActivity extends AppCompatActivity {

  private static final int SEND_PERM_REQ = 1;
  private static final String TAG = "PushNotifyActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_push_notify);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  public void registerDevice(View view) {
    Log.d(TAG, "registerDevice");
    startService(new Intent(this, RegisterDeviceService.class));
  }

  public void ensureConnection(View view) {
    Log.d(TAG, "registerDevice");
    MqttClientAdapter.ensureBackendConnection(this);
  }

  public void reqPermission(View view) {
    reqPermission(PERMISSION_SEND);
    reqPermission(PERMISSION_RECEIVE);
    reqPermission(PERMISSION_CONNECT);
  }

  private void reqPermission(final String permission) {
    if (ContextCompat.checkSelfPermission(this, permission)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{permission}, SEND_PERM_REQ);
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
          Toast.makeText(this, "perm granted", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(this, "perm NOT granted: " +
                  Arrays.deepToString(permissions),
              Toast.LENGTH_SHORT).show();
        }
        break;
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_push_notify, menu);
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
