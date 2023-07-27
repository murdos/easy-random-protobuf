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

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.ContextAwareRandomizer;
import org.jeasy.random.api.RandomizerContext;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;

/**
 * Generate a random Protobuf {@link Message}.
 */
public class ProtobufMessageRandomizer implements ContextAwareRandomizer<Message> {

    private final Class<Message> messageClass;
    private final ProtobufMessageBuilderCache protobufMessageBuilderCache;
    private final ProtobufFieldValueGeneratorProvider fieldGeneratorsProvider;
    private final IntegerRangeRandomizer collectionSizeRandomizer;
    private final Random random;
    private RandomizerContext randomizerContext;

    public ProtobufMessageRandomizer(
        Class<Message> messageClass,
        EasyRandomParameters parameters,
        ProtobufMessageBuilderCache protobufMessageBuilderCache
    ) {
        this.messageClass = messageClass;
        this.protobufMessageBuilderCache = protobufMessageBuilderCache;
        this.random = new Random(parameters.getSeed());
        this.fieldGeneratorsProvider =
            new ProtobufFieldValueGeneratorProvider(parameters.getSeed(), this::getRandomizerContext);
        this.collectionSizeRandomizer =
            new IntegerRangeRandomizer(
                parameters.getCollectionSizeRange().getMin(),
                parameters.getCollectionSizeRange().getMax(),
                random.nextLong()
            );
    }

    @Override
    public void setRandomizerContext(RandomizerContext randomizerContext) {
        this.randomizerContext = randomizerContext;
    }

    private RandomizerContext getRandomizerContext() {
        return randomizerContext;
    }

    @Override
    public Message getRandomValue() {
        Builder builder = instantiateMessageBuilder(messageClass);
        // If the type has been already randomized, return one cached instance to avoid recursion
        // Builder is used since we need to add a reference to the cache before fully populating the message
        if (protobufMessageBuilderCache.hasAlreadyRandomizedBuilder(builder.getClass())) {
            return protobufMessageBuilderCache.getRandomPopulatedMessageBuilder(builder.getClass()).build();
        } else {
            protobufMessageBuilderCache.addPopulatedMessageBuilderReference(builder.getClass(), builder);
        }
        Descriptor descriptor = builder.getDescriptorForType();
        List<FieldDescriptor> plainFields = descriptor
            .getFields()
            .stream()
            .filter(field -> field.getContainingOneof() == null)
            .toList();
        for (FieldDescriptor fieldDescriptor : plainFields) {
            populateField(fieldDescriptor, builder);
        }
        for (Descriptors.OneofDescriptor oneofDescriptor : descriptor.getOneofs()) {
            populateOneof(oneofDescriptor, builder);
        }
        return builder.build();
    }

    private static Message.Builder instantiateMessageBuilder(Class<Message> clazz) {
        try {
            Method getDefaultInstanceMethod = clazz.getMethod("getDefaultInstance");
            Message message = (Message) getDefaultInstanceMethod.invoke(null);
            return message.newBuilderForType();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void populateField(FieldDescriptor field, Builder containingBuilder) {
        ProtobufFieldValueGenerator fieldGenerator;
        if (field.isMapField()) {
            fieldGenerator =
                (fieldDescriptor, parentBuilder) -> {
                    Builder mapEntryBuilder = parentBuilder.newBuilderForField(fieldDescriptor);
                    for (FieldDescriptor subField : fieldDescriptor.getMessageType().getFields()) {
                        populateField(subField, mapEntryBuilder);
                    }
                    return mapEntryBuilder.build();
                };
        } else {
            fieldGenerator = fieldGeneratorsProvider.get(field.getJavaType());
        }

        if (field.isRepeated()) {
            int collectionSize = collectionSizeRandomizer.getRandomValue();
            for (int i = 0; i < collectionSize; i++) {
                containingBuilder.addRepeatedField(field, fieldGenerator.generateFor(field, containingBuilder));
            }
        } else {
            containingBuilder.setField(field, fieldGenerator.generateFor(field, containingBuilder));
        }
    }

    private void populateOneof(Descriptors.OneofDescriptor oneofDescriptor, Builder builder) {
        int fieldCount = oneofDescriptor.getFieldCount();
        int oneofCase = random.nextInt(fieldCount);
        FieldDescriptor selectedCase = oneofDescriptor.getField(oneofCase);
        populateField(selectedCase, builder);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
