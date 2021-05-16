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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.StringValue;
import io.github.murdos.easyrandom.protobuf.testing.proto2.Proto2Enum;
import io.github.murdos.easyrandom.protobuf.testing.proto2.Proto2Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

class Protobuf2MessageBuilderGenerationTest {

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto2Message.Builder protoBuilderInstance = easyRandom.nextObject(Proto2Message.Builder.class);

        assertThat(protoBuilderInstance.getDoubleField()).isEqualTo(0.7231742029971469);
        assertThat(protoBuilderInstance.getFloatField()).isEqualTo(0.99089885f);
        assertThat(protoBuilderInstance.getInt32Field()).isEqualTo(1295249578);
        assertThat(protoBuilderInstance.getInt64Field()).isEqualTo(4672433029010564658L);
        assertThat(protoBuilderInstance.getUint32Field()).isEqualTo(-1680189627);
        assertThat(protoBuilderInstance.getUint64Field()).isEqualTo(4775521195821725379L);
        assertThat(protoBuilderInstance.getSint32Field()).isEqualTo(-1621910390);
        assertThat(protoBuilderInstance.getSint64Field()).isEqualTo(-2298228485105199876L);
        assertThat(protoBuilderInstance.getFixed32Field()).isEqualTo(-1219562352);
        assertThat(protoBuilderInstance.getFixed64Field()).isEqualTo(2992351518418085755L);
        assertThat(protoBuilderInstance.getSfixed32Field()).isEqualTo(-1366603797);
        assertThat(protoBuilderInstance.getSfixed64Field()).isEqualTo(-3758321679654915806L);
        assertThat(protoBuilderInstance.getBoolField()).isTrue();
        assertThat(protoBuilderInstance.getStringField()).isEqualTo("wSxRIexQAaxVLAiN");
        assertThat(protoBuilderInstance.getBytesField().toByteArray())
            .containsExactly(
                53,
                114,
                79,
                60,
                -14,
                -35,
                50,
                97,
                116,
                107,
                41,
                53,
                -39,
                -28,
                114,
                79,
                -111,
                98,
                -14,
                -11,
                -97,
                102,
                -22,
                83,
                -126,
                104,
                -108,
                -59,
                -97,
                93,
                -122,
                -67
            );

        assertThat(protoBuilderInstance.getEnumField()).isEqualTo(Proto2Enum.THIRD_VALUE);
        assertThat(protoBuilderInstance.getStringValueField())
            .isNotNull()
            .extracting(StringValue::getValue)
            .isEqualTo("tg");
        assertThat(protoBuilderInstance.getRepeatedStringFieldList())
            .containsExactly("AJVH", "WuGaTPB", "NuGSIFWDPVPqKClkqNpxLIRO", "jukCwoSTgRGMwWnAeflhVmclqMX", "bWyqZZW");

        assertThat(protoBuilderInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoBuilderInstance.getEmbeddedMessage())
            .satisfies(
                embeddedMessage -> {
                    assertThat(embeddedMessage.getStringField()).isEqualTo("LRHCsQ");
                    assertThat(embeddedMessage.getEnumField()).isEqualTo(Proto2Enum.THIRD_VALUE);
                }
            );
        assertThat(protoBuilderInstance.getOneofFieldCase().getNumber())
            .isNotEqualTo(Proto2Message.OneofFieldCase.ONEOFFIELD_NOT_SET);
    }
}
