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

package at.sbaresearch.microg.adapter.backend.gms.common;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

  public static Build getBuild(Context context) {
    return new Build();
  }

  public static byte[] readStreamToEnd(final InputStream is) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    if (is != null) {
      final byte[] buff = new byte[1024];
      int read;
      do {
        bos.write(buff, 0, (read = is.read(buff)) < 0 ? 0 : read);
      } while (read >= 0);
      is.close();
    }
    return bos.toByteArray();
  }
}
