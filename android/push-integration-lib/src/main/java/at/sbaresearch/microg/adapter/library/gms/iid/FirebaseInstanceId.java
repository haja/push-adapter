/*
 * Copyright (C) 2013-2017 microG Project Team
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

package at.sbaresearch.microg.adapter.library.gms.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.common.PublicApi;
import at.sbaresearch.microg.adapter.library.gms.gcm.CloudMessagingRpc;
import at.sbaresearch.microg.adapter.library.gms.gcm.GcmConstants;

import java.io.IOException;

import static at.sbaresearch.microg.adapter.library.gms.gcm.GcmConstants.EXTRA_SENDER;

/**
 * Instance ID provides a unique identifier for each app instance and a mechanism
 * to authenticate and authorize actions (for example, sending a GCM message).
 * <p/>
 * Instance ID is stable but may become invalid, if:
 * <ul>
 * <li>App deletes Instance ID</li>
 * <li>Device is factory reset</li>
 * <li>User uninstalls the app</li>
 * <li>User clears app data</li>
 * </ul>
 * If Instance ID has become invalid, the app can call {@link FirebaseInstanceId#getId()}
 * to request a new Instance ID.
 * To prove ownership of Instance ID and to allow servers to access data or
 * services associated with the app, call {@link FirebaseInstanceId#getToken(java.lang.String, java.lang.String)}.
 */
@PublicApi
public class FirebaseInstanceId {
  /**
   * Error returned when failed requests are retried too often.  Use
   * exponential backoff when retrying requests
   */
  public static final String ERROR_BACKOFF = "RETRY_LATER";

  /**
   * Blocking methods must not be called on the main thread.
   */
  public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";

  /**
   * Tokens can't be generated. Only devices with Google Play are supported.
   */
  public static final String ERROR_MISSING_INSTANCEID_SERVICE = "MISSING_INSTANCEID_SERVICE";

  /**
   * The device cannot read the response, or there was a server error.
   * Application should retry the request later using exponential backoff
   * and retry (on each subsequent failure increase delay before retrying).
   */
  public static final String ERROR_SERVICE_NOT_AVAILABLE = GcmConstants.ERROR_SERVICE_NOT_AVAILABLE;

  /**
   * Timeout waiting for a response.
   */
  public static final String ERROR_TIMEOUT = "TIMEOUT";

  private static final String TAG = "InstanceID";

  private CloudMessagingRpc rpc;
  private static FirebaseInstanceId instance;

  private FirebaseInstanceId() {
  }

  /**
   * Resets Instance ID and revokes all tokens.
   *
   * @throws IOException
   */
  public void deleteInstanceId() throws IOException {
    Log.w(TAG, "deleteInstanceId: called, not implemented");
    throw new UnsupportedOperationException();
  }

  /**
   * Revokes access to a scope (action) for an entity previously
   * authorized by {@link FirebaseInstanceId#getToken(java.lang.String, java.lang.String)}.
   * <p/>
   * Do not call this function on the main thread.
   *
   * @param authorizedEntity Entity that must no longer have access.
   * @param scope Action that entity is no longer authorized to perform.
   * @throws IOException if the request fails.
   */
  public void deleteToken(String authorizedEntity, String scope) throws IOException {
    // TODO implement token revocation?
    Log.w(TAG, "deleteToken: not implemented");
  }

  /**
   * Returns a stable identifier that uniquely identifies the app instance.
   *
   * @return The identifier for the application instance.
   */
  public String getId() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an instance of this class.
   *
   * @return InstanceID instance.
   */
  public static FirebaseInstanceId getInstance(Context context) {
    if (instance == null) {
      instance = new FirebaseInstanceId();
      instance.rpc = new CloudMessagingRpc(context.getApplicationContext());
    }
    return instance;
  }

  /**
   * Returns a token that authorizes an Entity (example: cloud service) to perform
   * an action on behalf of the application identified by Instance ID.
   * <p/>
   * This is similar to an OAuth2 token except, it applies to the
   * application instance instead of a user.
   * <p/>
   * Do not call this function on the main thread.
   *
   * @param authorizedEntity Entity authorized by the token.
   * @param scope Action authorized for authorizedEntity.
   * @return a token that can identify and authorize the instance of the
   * application on the device.
   * @throws IOException if the request fails.
   */
  public RelayConnection getToken(String authorizedEntity, String scope) throws IOException {
    if (Looper.getMainLooper() == Looper.myLooper()) throw new IOException(ERROR_MAIN_THREAD);

    Log.i(TAG, "getToken: authorizedEntity: " + authorizedEntity);

    if (authorizedEntity == null) {
      throw new IllegalArgumentException("not authorizedEntity");
    }

    Bundle extras = new Bundle();
    extras.putString(EXTRA_SENDER, authorizedEntity);
    return rpc.handleRegisterMessageResult(rpc.sendRegisterMessageBlocking(extras));
  }

  public static class RelayConnection {
    public final String token;
    public final String relayUrl;
    public final byte[] cert;

    public RelayConnection(String token, String relayUrl, byte[] cert) {
      this.token = token;
      this.relayUrl = relayUrl;
      this.cert = cert;
    }

    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("RelayConnection{");
      sb.append("token='").append(token).append('\'');
      sb.append(", relayUrl='").append(relayUrl).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }
}