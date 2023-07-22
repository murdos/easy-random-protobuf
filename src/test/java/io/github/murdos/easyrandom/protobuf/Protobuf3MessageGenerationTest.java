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

import io.github.murdos.easyrandom.protobuf.testing.proto3.EmbeddedProto3Message;
import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Enum;
import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

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
        assertThat(protoInstance.getEmbeddedMessage())
            .satisfies(
                embeddedMessage -> {
                    assertThat(embeddedMessage.getStringField()).isNotBlank();
                    assertThat(embeddedMessage.getEnumField()).isIn((Object[]) Proto3Enum.values());
                }
            );
        assertThat(protoInstance.getOneofFieldCase()).isNotEqualTo(Proto3Message.OneofFieldCase.ONEOFFIELD_NOT_SET);
        assertThat(protoInstance.getMapFieldMap()).isNotEmpty();
    }

    @Test
    void shouldUseCollectionSizeRangeParameters() {
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(3, 3);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.getRepeatedStringFieldList()).hasSize(3);
        assertThat(protoInstance.getMapFieldMap()).hasSize(3);
    }

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        ProtobufApprovals.verifyAsJson(protoInstance);
    }

    @Test
    void shouldSequentiallyGenerateDifferentObjects() {
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message firstInstance = easyRandom.nextObject(Proto3Message.class);
        Proto3Message secondInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(firstInstance.getDoubleField()).isNotEqualTo(secondInstance.getDoubleField());
        assertThat(firstInstance.getFloatField()).isNotEqualTo(secondInstance.getFloatField());
        assertThat(firstInstance.getInt32Field()).isNotEqualTo(secondInstance.getInt32Field());
        assertThat(firstInstance.getInt64Field()).isNotEqualTo(secondInstance.getInt64Field());
        assertThat(firstInstance.getUint32Field()).isNotEqualTo(secondInstance.getUint32Field());
        assertThat(firstInstance.getUint64Field()).isNotEqualTo(secondInstance.getUint64Field());
        assertThat(firstInstance.getSint32Field()).isNotEqualTo(secondInstance.getSint32Field());
        assertThat(firstInstance.getSint64Field()).isNotEqualTo(secondInstance.getSint64Field());
        assertThat(firstInstance.getFixed32Field()).isNotEqualTo(secondInstance.getFixed32Field());
        assertThat(firstInstance.getFixed64Field()).isNotEqualTo(secondInstance.getFixed64Field());
        assertThat(firstInstance.getSfixed32Field()).isNotEqualTo(secondInstance.getSfixed32Field());
        assertThat(firstInstance.getSfixed64Field()).isNotEqualTo(secondInstance.getSfixed64Field());

        assertThat(firstInstance.getStringField()).isNotEqualTo(secondInstance.getStringField());
        assertThat(firstInstance.getBytesField()).isNotEqualTo(secondInstance.getBytesField());

        assertThat(firstInstance.getStringValueField()).isNotEqualTo(secondInstance.getStringValueField());
        assertThat(firstInstance.getRepeatedStringFieldList())
            .isNotEqualTo(secondInstance.getRepeatedStringFieldList());

        assertThat(firstInstance.hasEmbeddedMessage()).isTrue();
        assertThat(firstInstance.getEmbeddedMessage())
            .satisfies(
                embeddedMessage -> {
                    assertThat(embeddedMessage.getStringField())
                        .isNotEqualTo(secondInstance.getEmbeddedMessage().getStringField());
                }
            );
        assertThat(firstInstance.getMapFieldMap()).isNotEqualTo(secondInstance.getMapFieldMap());
    }

    @Test
    void shouldUseCustomRandomizerForSubMessageWhenItsDefined() {
        EmbeddedProto3Message customEmbeddedMessage = EmbeddedProto3Message
            .newBuilder()
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
