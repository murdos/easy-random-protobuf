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

import com.google.protobuf.Message;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.ContextAwareRandomizer;
import org.jeasy.random.api.RandomizerContext;

/**
 * Generate a random Protobuf {@link Message.Builder}.
 */
public class ProtobufMessageBuilderRandomizer implements ContextAwareRandomizer<Message.Builder> {

    private final ProtobufMessageRandomizer protobufMessageRandomizer;

    public ProtobufMessageBuilderRandomizer(
        Class<Message.Builder> messageBuilderClass,
        EasyRandomParameters parameters,
        ProtobufMessageBuilderCache protobufMessageBuilderCache
    ) {
        this.protobufMessageRandomizer =
            new ProtobufMessageRandomizer(
                retrieveMessageClassFromBuilderClass(messageBuilderClass),
                parameters,
                protobufMessageBuilderCache
            );
    }

    @Override
    public void setRandomizerContext(RandomizerContext randomizerContext) {
        this.protobufMessageRandomizer.setRandomizerContext(randomizerContext);
    }

    @SuppressWarnings("unchecked")
    private static Class<Message> retrieveMessageClassFromBuilderClass(Class<Message.Builder> messageBuilderClass) {
        return (Class<Message>) messageBuilderClass.getEnclosingClass();
    }

    @Override
    public Message.Builder getRandomValue() {
        return protobufMessageRandomizer.getRandomValue().toBuilder();
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
