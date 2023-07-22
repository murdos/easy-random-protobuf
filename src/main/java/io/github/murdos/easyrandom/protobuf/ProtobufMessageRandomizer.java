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

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.*;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.ContextAwareRandomizer;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.api.RandomizerContext;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;

/**
 * Generate a random Protobuf {@link Message}.
 */
public class ProtobufMessageRandomizer implements ContextAwareRandomizer<Message> {

    private final Class<Message> messageClass;
    private final EnumMap<JavaType, ProtobufValueGenerator> fieldGenerators = new EnumMap<>(JavaType.class);
    private final Random random;
    private RandomizerContext randomizerContext;

    public ProtobufMessageRandomizer(Class<Message> messageClass, EasyRandomParameters parameters) {
        this.messageClass = messageClass;
        this.random = new Random(parameters.getSeed());

        this.fieldGenerators.put(INT, generatorForBasicType(int.class));
        this.fieldGenerators.put(LONG, generatorForBasicType(long.class));
        this.fieldGenerators.put(FLOAT, generatorForBasicType(float.class));
        this.fieldGenerators.put(DOUBLE, generatorForBasicType(double.class));
        this.fieldGenerators.put(BOOLEAN, generatorForBasicType(boolean.class));
        this.fieldGenerators.put(STRING, generatorForBasicType(String.class));
        this.fieldGenerators.put(BYTE_STRING, generatorForBasicType(ByteString.class));
        this.fieldGenerators.put(ENUM, generatorForEnum());
        this.fieldGenerators.put(MESSAGE, generatorForProtoMessage());
    }

    private <T> ProtobufValueGenerator generatorForBasicType(Class<T> type) {
        return (field, containingBuilder) -> getRandomValueForType(type);
    }

    private ProtobufValueGenerator generatorForEnum() {
        return (field, containingBuilder) -> {
            List<EnumValueDescriptor> values = field.getEnumType().getValues();
            int choice = random.nextInt(values.size());
            return values.get(choice);
        };
    }

    private ProtobufValueGenerator generatorForProtoMessage() {
        return (field, containingBuilder) ->
                getRandomValueForType(containingBuilder.newBuilderForField(field).getDefaultInstanceForType().getClass());
    }

    private <T> T getRandomValueForType(Class<T> type) {
        Randomizer<T> randomizer = getRandomizerForType(type);
        if (randomizer instanceof ContextAwareRandomizer) {
            ((ContextAwareRandomizer<?>) randomizer).setRandomizerContext(randomizerContext);
        }
        return randomizer.getRandomValue();
    }

    private <T> Randomizer<T> getRandomizerForType(Class<T> type) {
        return getEasyRandomParameters().getRandomizerProvider().getRandomizerByType(type, randomizerContext);
    }

    @Override
    public void setRandomizerContext(RandomizerContext randomizerContext) {
        this.randomizerContext = randomizerContext;
    }

    @Override
    public Message getRandomValue() {
        Builder builder = instantiateMessageBuilder(messageClass);
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
        ProtobufValueGenerator fieldGenerator;
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
            fieldGenerator = this.fieldGenerators.get(field.getJavaType());
        }

        if (field.isRepeated()) {
            IntegerRangeRandomizer collectionSizeRandomizer = new IntegerRangeRandomizer(
                getEasyRandomParameters().getCollectionSizeRange().getMin(),
                getEasyRandomParameters().getCollectionSizeRange().getMax(),
                getRandomValueForType(long.class)
            );
            for (int i = 0; i < collectionSizeRandomizer.getRandomValue(); i++) {
                containingBuilder.addRepeatedField(field, fieldGenerator.generateFor(field, containingBuilder));
            }
        } else {
            containingBuilder.setField(field, fieldGenerator.generateFor(field, containingBuilder));
        }
    }

    private EasyRandomParameters getEasyRandomParameters() {
        return randomizerContext.getParameters();
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
