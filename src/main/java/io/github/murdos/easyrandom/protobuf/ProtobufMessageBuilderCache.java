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
import java.util.*;
import org.jeasy.random.EasyRandomParameters;

/**
 * Cache of populated message builders to avoid infinite recursion
 */
class ProtobufMessageBuilderCache {

    private final EasyRandomParameters parameters;
    private final Random random;
    private final Map<Class<? extends Message.Builder>, List<Message.Builder>> populatedMessageBuilders = new IdentityHashMap<>();

    ProtobufMessageBuilderCache(EasyRandomParameters parameters) {
        this.parameters = parameters;
        this.random = new Random(parameters.getSeed());
    }

    void addPopulatedMessageBuilderReference(Class<? extends Message.Builder> type, Message.Builder messageBuilder) {
        int objectPoolSize = parameters.getObjectPoolSize();
        List<Message.Builder> objects = populatedMessageBuilders.computeIfAbsent(
            type,
            clazz -> new ArrayList<>(objectPoolSize)
        );
        if (objects.size() < objectPoolSize) {
            objects.add(messageBuilder);
        }
    }

    Message.Builder getRandomPopulatedMessageBuilder(Class<? extends Message.Builder> type) {
        int actualPoolSize = populatedMessageBuilders.get(type).size();
        int randomIndex = actualPoolSize > 1 ? random.nextInt(actualPoolSize) : 0;
        return populatedMessageBuilders.get(type).get(randomIndex);
    }

    boolean hasAlreadyRandomizedBuilder(Class<? extends Message.Builder> type) {
        return (
            populatedMessageBuilders.containsKey(type) &&
            populatedMessageBuilders.get(type).size() == parameters.getObjectPoolSize()
        );
    }
}
