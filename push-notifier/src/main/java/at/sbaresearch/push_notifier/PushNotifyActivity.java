package at.sbaresearch.push_notifier;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PushNotifyActivity extends AppCompatActivity {

  private static final String SEND_PERM = "at.sbaresearch.android.gcm.intent.SEND";
  private static final int SEND_PERM_REQ = 1;
  private static final String INTENT_RECEIVE = "at.sbaresearch.android.c2dm.intent.RECEIVE";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_push_notify);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    final PushNotifyActivity activity = this;
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(activity, SEND_PERM)
            != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(activity, new String[]{SEND_PERM}, SEND_PERM_REQ);
        } else {
          // all fine
        }
      }
    });


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

  public void sendIntent(View view) {
    Intent intent = new Intent(INTENT_RECEIVE);
    intent.putExtra(GCM_API.EXTRA_FROM, "testPushApp");
    sendBroadcast(intent);
  }

}
