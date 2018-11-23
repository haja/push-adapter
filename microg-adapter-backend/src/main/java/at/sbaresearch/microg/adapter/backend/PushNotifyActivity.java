package at.sbaresearch.microg.adapter.backend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.ACTION_C2DM_RECEIVE;
import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.EXTRA_FROM;

public class PushNotifyActivity extends AppCompatActivity {

  private static final String SEND_PERM = "at.sbaresearch.android.gcm.intent.SEND";
  private static final int SEND_PERM_REQ = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_push_notify);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      }
    });


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
          Toast.makeText(this, "perm granted", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(this, "perm NOT granted", Toast.LENGTH_SHORT).show();
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

  /**
   *  TODO remove this method, call on other class
    */
  @Deprecated
  public void sendIntent(View view) {
    // TODO use micro-g implementation for relay of intents
    Intent intent = new Intent(ACTION_C2DM_RECEIVE);
    intent.putExtra(EXTRA_FROM, "testPushApp");
    // TODO set package for application
    String clientPackageName = "at.sbaresearch.microg.adapter.sample";
    intent.setPackage(clientPackageName);
    sendBroadcast(intent);
  }

}
