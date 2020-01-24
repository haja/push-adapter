/*
 * Copyright (C) 2013-2017, 2020 microG Project Team, Harald Jagenteufel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.sbaresearch.microg.adapter.backend.gms.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import at.sbaresearch.microg.adapter.backend.R;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmDatabase;
import at.sbaresearch.microg.adapter.backend.registration.app.RegisterAppService;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

public class AskPushPermission extends FragmentActivity {

  private GcmDatabase database;

  private String packageName;
  private Intent intent;
  private boolean answered;
  private String requestId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    database = new GcmDatabase(this);

    packageName = getIntent().getStringExtra(EXTRA_APP);
    intent = getIntent().getParcelableExtra(EXTRA_PENDING_INTENT);

    requestId = null;
    if (intent.hasExtra(EXTRA_KID) && intent.getStringExtra(EXTRA_KID).startsWith("|")) {
      String[] kid = intent.getStringExtra(EXTRA_KID).split("\\|");
      if (kid.length >= 3 && "ID".equals(kid[1])) {
        requestId = kid[2];
      }
    }

    if (database.getApp(packageName) != null) {
      finish();
      return;
    }

    setContentView(R.layout.ask_gcm);

    try {
      PackageManager pm = getPackageManager();
      final ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
      CharSequence label = pm.getApplicationLabel(info);

      ((TextView) findViewById(R.id.permission_message))
          .setText(Html.fromHtml("Allow <b>" + label + "</b> to register for push notifications?"));
      findViewById(R.id.permission_allow_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (answered) return;
          database.noteAppKnown(packageName, true);
          answered = true;
          new Thread(new Runnable() {
            @Override
            public void run() {
             new RegisterAppService()
                  .registerAndReply(AskPushPermission.this, database, intent, packageName,
                      requestId);
            }
          }).start();
          finish();
        }
      });
      findViewById(R.id.permission_deny_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (answered) return;
          database.noteAppKnown(packageName, false);
          answered = true;
          RegisterAppService
              .replyNotAvailable(AskPushPermission.this, intent, packageName, requestId);
          finish();
        }
      });
    } catch (PackageManager.NameNotFoundException e) {
      finish();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (!answered) {
      RegisterAppService.replyNotAvailable(AskPushPermission.this, intent, packageName, requestId);
      answered = true;
    }
    database.close();
  }
}
