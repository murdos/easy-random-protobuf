/*
 * Copyright © 2020 Aurélien Mino (aurelien.mino@gmail.com)
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
package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.approvaltests.JsonApprovals;

public final class ProtobufApprovals {

    private ProtobufApprovals() {}

    public static void verifyAsJson(Message.Builder messageBuilder) {
        verifyAsJson(messageBuilder.build());
    }

    public static void verifyAsJson(Message message) {
        try {
            JsonApprovals.verifyJson(JsonFormat.printer().print(message));
        } catch (InvalidProtocolBufferException e) {
            throw new AssertionError(e);
        }
    }
}
