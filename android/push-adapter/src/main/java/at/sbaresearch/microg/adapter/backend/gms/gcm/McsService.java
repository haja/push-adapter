package at.sbaresearch.microg.adapter.backend.gms.gcm;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.gms.gcm.mcs.AppData;

import java.util.List;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;
import static at.sbaresearch.microg.adapter.backend.gms.gcm.McsConstants.MCS_DATA_MESSAGE_STANZA_TAG;

public class McsService extends Service {

  private static final String TAG = "GmsGcmMcsSvcAdapter";
  private static long lastIncomingNetworkRealtime = 0;

  private GcmDatabase database;

  @Override
  public void onCreate() {
    super.onCreate();
    // TriggerReceiver.register(this);
    database = new GcmDatabase(this);
  }

  @Override
  public void onDestroy() {
    // alarmManager.cancel(heartbeatIntent);
    // closeAll();
    database.close();
    super.onDestroy();
  }

  // TODO relay intents through here
  private void handleInput(int type, Message message) {
    try {
      switch (type) {
        case MCS_DATA_MESSAGE_STANZA_TAG:
          handleCloudMessage((DataMessageStanza) message);
          break;
          /*
        case MCS_HEARTBEAT_PING_TAG:
          handleHeartbeatPing((HeartbeatPing) message);
          break;
        case MCS_HEARTBEAT_ACK_TAG:
          handleHeartbeatAck((HeartbeatAck) message);
          break;
        case MCS_CLOSE_TAG:
          handleClose((Close) message);
          break;
        case MCS_LOGIN_RESPONSE_TAG:
          handleLoginResponse((LoginResponse) message);
          break;
          */
        default:
          Log.w(TAG, "Unknown message: " + message);
      }
      // resetCurrentDelay();
      lastIncomingNetworkRealtime = SystemClock.elapsedRealtime();
    } catch (Exception e) {
      Log.e(TAG, "Failed to handle message: " + e.getMessage());
      // rootHandler.sendMessage(rootHandler.obtainMessage(MSG_TEARDOWN, e));
    }
  }

  private void handleCloudMessage(DataMessageStanza message) {
    // if (message.persistent_id != null) {
    // GcmPrefs.get(this).extendLastPersistedId(message.persistent_id);
    // }
    // if (SELF_CATEGORY.equals(message.category)) {
    // handleSelfMessage(message);
    // } else {
    handleAppMessage(message);
    // }
  }

  // TODO fix intent receiving
  private void handleAppMessage(DataMessageStanza msg) {
    database.noteAppMessage(msg.category, msg.getSerializedSize());
    GcmDatabase.App app = database.getApp(msg.category);

    Intent intent = new Intent();
    intent.setAction(ACTION_C2DM_RECEIVE);
    intent.setPackage(msg.category);
    intent.putExtra(EXTRA_FROM, msg.from);
    if (app.wakeForDelivery) {
      intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    } else {
      intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
    }
    if (msg.token != null) intent.putExtra(EXTRA_COLLAPSE_KEY, msg.token);
    for (AppData appData : msg.app_data) {
      intent.putExtra(appData.key, appData.value);
    }

    String receiverPermission;
    try {
      String name = msg.category + ".permission.C2D_MESSAGE";
      getPackageManager().getPermissionInfo(name, 0);
      receiverPermission = name;
    } catch (PackageManager.NameNotFoundException e) {
      receiverPermission = null;
    }

    List<ResolveInfo> infos =
        getPackageManager().queryBroadcastReceivers(intent, PackageManager.GET_RESOLVED_FILTER);
    if (infos == null || infos.isEmpty()) {
      logd("No target for message, wut?");
    } else {
      for (ResolveInfo resolveInfo : infos) {
        logd("Target: " + resolveInfo);
        Intent targetIntent = new Intent(intent);
        targetIntent.setComponent(
            new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
        sendOrderedBroadcast(targetIntent, receiverPermission);
      }
    }
  }

  private static void logd(String msg) {
    // if (GcmPrefs.get(null).isGcmLogEnabled()) {
    Log.d(TAG, msg);
    // }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * TODO this is a mockup, remove
   */
  private class DataMessageStanza extends Message {
    public String category;
    public String from;
    public String token;
    public AppData[] app_data;

    public int getSerializedSize() {
      return 0;
    }
  }

  private class Message {
  }
}
