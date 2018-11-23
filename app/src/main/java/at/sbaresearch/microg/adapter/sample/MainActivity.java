package at.sbaresearch.microg.adapter.sample;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import at.sbaresearch.microg.adapter.library.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      }
    });
  }

  public void register(View view) {
    showSnack(view, "registering...");
    final AsyncTask<Context, Void, String> registerTask = new RegisterTask();
    registerTask.execute(this);
  }

  private static void showSnack(View view, String msg) {
    Snackbar.make(view, msg,
        Snackbar.LENGTH_SHORT)
        .setAction("Action", null).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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

  private static class RegisterTask extends AsyncTask<Context, Void, String> {

    private final static String TAG = RegisterTask.class.getSimpleName();

    public RegisterTask() {
    }

    @Override
    protected String doInBackground(Context... ctx) {
      final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx[0]);
      try {
        final String id = gcm.register("testId1");
        return id;
      } catch (IOException e) {
        Log.e(TAG, "registration failed", e);
      }
      return null;
    }

    @Override
    protected void onPostExecute(String id) {
      final String msg = "registration successful, id: " + id;
      Log.i(TAG, msg);
    }
  }
}
