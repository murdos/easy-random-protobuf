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

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;

/**
 * Generate a random Protobuf {@link Message}.
 */
public class ProtobufMessageRandomizer implements Randomizer<Message> {

    private final Class<Message> messageClass;
    private final EnumMap<JavaType, BiFunction<FieldDescriptor, Builder, Object>> fieldGenerators;
    private final EasyRandom easyRandom;
    private final EasyRandomParameters parameters;

    public ProtobufMessageRandomizer(
        Class<Message> messageClass,
        EasyRandom easyRandom,
        EasyRandomParameters parameters
    ) {
        this.messageClass = messageClass;
        this.easyRandom = easyRandom;
        this.parameters = parameters;

        this.fieldGenerators = new EnumMap<>(JavaType.class);
        this.fieldGenerators.put(INT, (field, containingBuilder) -> easyRandom.nextInt());
        this.fieldGenerators.put(LONG, (field, containingBuilder) -> easyRandom.nextLong());
        this.fieldGenerators.put(FLOAT, (field, containingBuilder) -> easyRandom.nextFloat());
        this.fieldGenerators.put(DOUBLE, (field, containingBuilder) -> easyRandom.nextDouble());
        this.fieldGenerators.put(BOOLEAN, (field, containingBuilder) -> easyRandom.nextBoolean());
        this.fieldGenerators.put(
                STRING,
                (field, containingBuilder) -> new StringRandomizer(easyRandom.nextLong()).getRandomValue()
            );
        this.fieldGenerators.put(
                BYTE_STRING,
                (field, containingBuilder) -> new ByteStringRandomizer(easyRandom.nextLong()).getRandomValue()
            );
        this.fieldGenerators.put(ENUM, (field, containingBuilder) -> getRandomEnumValue(field.getEnumType()));
        this.fieldGenerators.put(
                MESSAGE,
                (field, containingBuilder) ->
                    easyRandom.nextObject(
                        containingBuilder.newBuilderForField(field).getDefaultInstanceForType().getClass()
                    )
            );
    }

    @Override
    public Message getRandomValue() {
        Message defaultInstance = instantiateMessage(messageClass);
        Builder builder = defaultInstance.newBuilderForType();
        Descriptor descriptor = builder.getDescriptorForType();
        List<Descriptors.OneofDescriptor> oneofs = descriptor.getOneofs();
        List<FieldDescriptor> plainFields = descriptor
            .getFields()
            .stream()
            .filter(field -> field.getContainingOneof() == null)
            .collect(Collectors.toList());
        for (FieldDescriptor fieldDescriptor : plainFields) {
            populateField(fieldDescriptor, builder);
        }
        for (Descriptors.OneofDescriptor oneofDescriptor : oneofs) {
            populateOneof(oneofDescriptor, builder);
        }
        return builder.build();
    }

    private static Message instantiateMessage(Class<Message> clazz) {
        try {
            Method getDefaultInstanceMethod = clazz.getMethod("getDefaultInstance");
            return (Message) getDefaultInstanceMethod.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void populateField(FieldDescriptor field, Builder containingBuilder) {
        BiFunction<FieldDescriptor, Builder, Object> generator = this.fieldGenerators.get(field.getJavaType());
        if (field.isRepeated()) {
            IntegerRangeRandomizer collectionSizeRandomizer = new IntegerRangeRandomizer(
                parameters.getCollectionSizeRange().getMin(),
                parameters.getCollectionSizeRange().getMax(),
                easyRandom.nextLong()
            );
            for (int i = 0; i < collectionSizeRandomizer.getRandomValue(); i++) {
                containingBuilder.addRepeatedField(field, generator.apply(field, containingBuilder));
            }
        } else {
            containingBuilder.setField(field, generator.apply(field, containingBuilder));
        }
    }

    private void populateOneof(Descriptors.OneofDescriptor oneofDescriptor, Builder builder) {
        int fieldCount = oneofDescriptor.getFieldCount();
        int oneofCase = easyRandom.nextInt(fieldCount);
        FieldDescriptor selectedCase = oneofDescriptor.getField(oneofCase);
        populateField(selectedCase, builder);
    }

    private EnumValueDescriptor getRandomEnumValue(EnumDescriptor enumDescriptor) {
        List<EnumValueDescriptor> values = enumDescriptor.getValues();
        int choice = easyRandom.nextInt(values.size());
        return values.get(choice);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
