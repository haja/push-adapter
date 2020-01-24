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

package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.PushTestHelper;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static at.sbaresearch.mqtt4android.PushTestHelper.*;
import static org.assertj.core.api.Assertions.*;

public class PushMessageTest extends AppTest {

  @Autowired
  PushResource pushResource;
  @Autowired
  TestData testData;
  @Autowired
  PushTestHelper helper;

  @Test
  public void pushMessage_validToken_shouldNotThrow() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = helper.pushMessageBuilder()
        .token(reg.getToken());
    pushResource.sendMessage(toReq(msg));
  }

  @Test
  public void pushMessage_invalidToken_shouldFail() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = helper.pushMessageBuilder()
        .token(reg.getToken() + "-modified");
    assertThatThrownBy(() -> pushResource.sendMessage(toReq(msg)))
        .isInstanceOf(RuntimeException.class);
  }
}
