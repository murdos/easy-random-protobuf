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

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.annotation.Priority;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.api.RandomizerRegistry;

/**
 * A registry of randomizers for Protobuf messages.
 *
 */
@Priority(-2)
public class ProtobufRandomizerRegistry implements RandomizerRegistry {

    private EasyRandomParameters parameters;
    private final ConcurrentHashMap<Class<?>, Randomizer<?>> randomizers = new ConcurrentHashMap<>();

    @Override
    public void init(EasyRandomParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public Randomizer<?> getRandomizer(Field field) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Randomizer<?> getRandomizer(Class<?> type) {
        if (ByteString.class.isAssignableFrom(type)) {
            return randomizers.computeIfAbsent(type, clazz -> new ByteStringRandomizer(parameters.getSeed()));
        }
        if (Message.class.isAssignableFrom(type)) {
            return randomizers.computeIfAbsent(
                type,
                clazz -> new ProtobufMessageRandomizer((Class<Message>) type, parameters)
            );
        }
        if (Message.Builder.class.isAssignableFrom(type)) {
            return randomizers.computeIfAbsent(
                type,
                clazz -> new ProtobufMessageBuilderRandomizer((Class<Message.Builder>) type, parameters)
            );
        }
        return null;
    }
}
