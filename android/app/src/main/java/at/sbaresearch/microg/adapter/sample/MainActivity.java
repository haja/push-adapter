/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * This file is part of push-relay.
 *
 *     push-relay is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     push-relay is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with push-relay.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.sbaresearch.microg.adapter.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String URL_BACKEND = "http://10.0.2.2:8888";

  private BackendRestClient backendRestClient;

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
        sendMessage("test message 123", view);
      }
    });

    backendRestClient = createRestClient();
  }

  private BackendRestClient createRestClient() {
    val logger = new HttpLoggingInterceptor();
    logger.setLevel(Level.BODY);
    val cl = new OkHttpClient.Builder();
    cl.addInterceptor(logger);
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(URL_BACKEND)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(cl.build())
        .build();
    return retrofit.create(BackendRestClient.class);
  }

  private void sendMessage(String message, View view) {
    showSnack(view, "sending: " + message);
    SendMessageTask sendTask = new SendMessageTask(backendRestClient);
    sendTask.execute(message);
  }

  public void register(View view) {
    showSnack(view, "registering...");
    RegisterTask registerTask = new RegisterTask(backendRestClient);
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

}
