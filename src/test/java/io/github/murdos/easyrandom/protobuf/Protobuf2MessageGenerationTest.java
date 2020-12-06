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

import com.google.protobuf.StringValue;
import io.github.murdos.easyrandom.protobuf.testing.proto2.Proto2Enum;
import io.github.murdos.easyrandom.protobuf.testing.proto2.Proto2Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Protobuf2MessageGenerationTest {

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(123L)
                .collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto2Message protoInstance = easyRandom.nextObject(Proto2Message.class);

        assertThat(protoInstance.getDoubleField()).isEqualTo(0.7231742029971469);
        assertThat(protoInstance.getFloatField()).isEqualTo(0.72317415f);
        assertThat(protoInstance.getInt32Field()).isEqualTo(-1188957731);
        assertThat(protoInstance.getInt64Field()).isEqualTo(-5106534569952410475L);
        assertThat(protoInstance.getUint32Field()).isEqualTo(1018954901);
        assertThat(protoInstance.getUint64Field()).isEqualTo(-167885730524958550L);
        assertThat(protoInstance.getSint32Field()).isEqualTo(-39088943);
        assertThat(protoInstance.getSint64Field()).isEqualTo(4672433029010564658L);
        assertThat(protoInstance.getFixed32Field()).isEqualTo(1295249578);
        assertThat(protoInstance.getFixed64Field()).isEqualTo(-7216359497931550918L);
        assertThat(protoInstance.getSfixed32Field()).isEqualTo(1087885590);
        assertThat(protoInstance.getSfixed64Field()).isEqualTo(-3581075550420886390L);
        assertThat(protoInstance.getBoolField()).isTrue();
        assertThat(protoInstance.getStringField()).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
        assertThat(protoInstance.getBytesField().toByteArray()).containsExactly(
                -35, -15, 33, -71, -107, 4, -68, 60, -47, -116, -85, -3, -86, -16, 51, 77,
                22, -47, -41, 64, 50, 38, -6, -110, 69, 87, -38, -101, 58, 15, 70, 66
        );
        assertThat(protoInstance.getEnumField()).isEqualTo(Proto2Enum.FOURTH_VALUE);
        assertThat(protoInstance.getStringValueField())
                .isNotNull()
                .extracting(StringValue::getValue).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
        assertThat(protoInstance.getRepeatedStringFieldList()).containsExactly(
                "yedUsFwdkelQbxeTeQOvaScfqIOOmaa",
                "JxkyvRnL",
                "RYtGKbgicZaHCBRQDSx",
                "VLhpfQGTMDYpsBZxvfBoeygjb",
                "UMaAIKKIkknjWEXJUfPxxQHeWKEJ"
        );

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).satisfies(embeddedMessage -> {
            assertThat(embeddedMessage.getStringField()).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
            assertThat(embeddedMessage.getEnumField()).isEqualTo(Proto2Enum.FOURTH_VALUE);
        });
    }
}
