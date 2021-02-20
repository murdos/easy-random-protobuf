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
import io.github.murdos.easyrandom.protobuf.testing.proto3.EmbeddedProto3Message;
import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Enum;
import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Protobuf3MessageGenerationTest {

    @Test
    void generatedValuesShouldBeValidAccordingToValidationConstraints() {
        EasyRandom easyRandom = new EasyRandom();
        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.getDoubleField()).isNotZero();
        assertThat(protoInstance.getFloatField()).isNotZero();
        assertThat(protoInstance.getInt32Field()).isNotZero();
        assertThat(protoInstance.getInt64Field()).isNotZero();
        assertThat(protoInstance.getUint32Field()).isNotZero();
        assertThat(protoInstance.getUint64Field()).isNotZero();
        assertThat(protoInstance.getSint32Field()).isNotZero();
        assertThat(protoInstance.getSint64Field()).isNotZero();
        assertThat(protoInstance.getFixed32Field()).isNotZero();
        assertThat(protoInstance.getFixed64Field()).isNotZero();
        assertThat(protoInstance.getSfixed32Field()).isNotZero();
        assertThat(protoInstance.getSfixed64Field()).isNotZero();
        assertThat(protoInstance.getBoolField()).isTrue();
        assertThat(protoInstance.getStringField()).isNotBlank();
        assertThat(protoInstance.getBytesField()).isNotEmpty();
        assertThat(protoInstance.getEnumField()).isIn((Object[]) Proto3Enum.values());
        assertThat(protoInstance.getStringValueField()).isNotNull();
        assertThat(protoInstance.getRepeatedStringFieldList()).isNotEmpty();

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).satisfies(embeddedMessage -> {
            assertThat(embeddedMessage.getStringField()).isNotBlank();
            assertThat(embeddedMessage.getEnumField()).isIn((Object[]) Proto3Enum.values());
        });
        assertThat(protoInstance.getOneofFieldCase()).isNotEqualTo(Proto3Message.OneofFieldCase.ONEOFFIELD_NOT_SET);
    }

    @Test
    void shouldUseCollectionSizeRangeParameters() {
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(3, 3);
        EasyRandom easyRandom = new EasyRandom(parameters);
        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);
        assertThat(protoInstance.getRepeatedStringFieldList()).hasSize(3);
    }

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(123L)
                .collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);
        assertThat(protoInstance.getDoubleField()).isEqualTo(0.7231742029971469);
        assertThat(protoInstance.getFloatField()).isEqualTo(0.99089885f);
        assertThat(protoInstance.getInt32Field()).isEqualTo(1295249578);
        assertThat(protoInstance.getInt64Field()).isEqualTo(4672433029010564658L);
        assertThat(protoInstance.getUint32Field()).isEqualTo(-1680189627);
        assertThat(protoInstance.getUint64Field()).isEqualTo(4775521195821725379L);
        assertThat(protoInstance.getSint32Field()).isEqualTo(-1621910390);
        assertThat(protoInstance.getSint64Field()).isEqualTo(-2298228485105199876L);
        assertThat(protoInstance.getFixed32Field()).isEqualTo(-1219562352);
        assertThat(protoInstance.getFixed64Field()).isEqualTo(2992351518418085755L);
        assertThat(protoInstance.getSfixed32Field()).isEqualTo(-1366603797);
        assertThat(protoInstance.getSfixed64Field()).isEqualTo(-3758321679654915806L);
        assertThat(protoInstance.getBoolField()).isTrue();
        assertThat(protoInstance.getStringField()).isEqualTo("wSxRIexQAaxVLAiN");
        assertThat(protoInstance.getBytesField().toByteArray()).containsExactly(
                53,114,79,60,-14,-35,50,97,116,107,41,53,-39,-28,114,79,-111,
                98,-14,-11,-97,102,-22,83,-126,104,-108,-59,-97,93,-122,-67
        );

        assertThat(protoInstance.getEnumField()).isEqualTo(Proto3Enum.SECOND_VALUE);
        assertThat(protoInstance.getStringValueField())
                .isNotNull()
                .extracting(StringValue::getValue).isEqualTo("tg");
        assertThat(protoInstance.getRepeatedStringFieldList()).containsExactly(
                "AJVH",
                "WuGaTPB",
                "NuGSIFWDPVPqKClkqNpxLIRO",
                "jukCwoSTgRGMwWnAeflhVmclqMX",
                "bWyqZZW"
        );

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).satisfies(embeddedMessage -> {
            assertThat(embeddedMessage.getStringField()).isEqualTo("LRHCsQ");
            assertThat(embeddedMessage.getEnumField()).isEqualTo(Proto3Enum.UNKNOWN);
        });
        assertThat(protoInstance.getOneofFieldCase()).isEqualTo(Proto3Message.OneofFieldCase.THIRDCHOICE);
    }

    @Test
    void shouldGenerateDifferentObject() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(123L)
                .collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);
        Proto3Message secondInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.getDoubleField()).isNotEqualTo(secondInstance.getDoubleField());
        assertThat(protoInstance.getFloatField()).isNotEqualTo(secondInstance.getFloatField());
        assertThat(protoInstance.getInt32Field()).isNotEqualTo(secondInstance.getInt32Field());
        assertThat(protoInstance.getInt64Field()).isNotEqualTo(secondInstance.getInt64Field());
        assertThat(protoInstance.getUint32Field()).isNotEqualTo(secondInstance.getUint32Field());
        assertThat(protoInstance.getUint64Field()).isNotEqualTo(secondInstance.getUint64Field());
        assertThat(protoInstance.getSint32Field()).isNotEqualTo(secondInstance.getSint32Field());
        assertThat(protoInstance.getSint64Field()).isNotEqualTo(secondInstance.getSint64Field());
        assertThat(protoInstance.getFixed32Field()).isNotEqualTo(secondInstance.getFixed32Field());
        assertThat(protoInstance.getFixed64Field()).isNotEqualTo(secondInstance.getFixed64Field());
        assertThat(protoInstance.getSfixed32Field()).isNotEqualTo(secondInstance.getSfixed32Field());
        assertThat(protoInstance.getSfixed64Field()).isNotEqualTo(secondInstance.getSfixed64Field());

        assertThat(protoInstance.getStringField()).isNotEqualTo(secondInstance.getStringField());
        assertThat(protoInstance.getBytesField()).isNotEqualTo(secondInstance.getBytesField());

        assertThat(protoInstance.getStringValueField()).isNotEqualTo(secondInstance.getStringValueField());
        assertThat(protoInstance.getRepeatedStringFieldList()).isNotEqualTo(secondInstance.getRepeatedStringFieldList());

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).satisfies(embeddedMessage -> {
            assertThat(embeddedMessage.getStringField()).isNotEqualTo(secondInstance.getEmbeddedMessage().getStringField());
        });

    }

    @Test
    void shouldUseCustomRandomizerForSubMessageWhenItsDefined() {
        EmbeddedProto3Message customEmbeddedMessage = EmbeddedProto3Message.newBuilder()
                .setStringField("custom string value")
                .setEnumField(Proto3Enum.FIRST_VALUE)
                .build();
        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(EmbeddedProto3Message.class, () -> customEmbeddedMessage);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).isEqualTo(customEmbeddedMessage);
    }
}
