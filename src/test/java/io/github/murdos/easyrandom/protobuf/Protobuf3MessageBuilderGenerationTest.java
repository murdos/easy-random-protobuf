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

import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

class Protobuf3MessageBuilderGenerationTest {

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message.Builder protoBuilderInstance = easyRandom.nextObject(Proto3Message.Builder.class);

        ProtobufApprovals.verifyAsJson(protoBuilderInstance);
    }

    @Test
    void shouldSequentiallyGenerateDifferentObjects() {
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message.Builder firstInstance = easyRandom.nextObject(Proto3Message.Builder.class);
        Proto3Message.Builder secondInstance = easyRandom.nextObject(Proto3Message.Builder.class);

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
}
