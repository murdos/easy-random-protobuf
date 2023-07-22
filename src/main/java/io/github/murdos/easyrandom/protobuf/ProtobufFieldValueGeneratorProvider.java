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
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import org.jeasy.random.api.ContextAwareRandomizer;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.api.RandomizerContext;

class ProtobufFieldValueGeneratorProvider {

    private final Random random;
    private final EnumMap<Descriptors.FieldDescriptor.JavaType, ProtobufFieldValueGenerator> fieldGenerators = new EnumMap<>(
        Descriptors.FieldDescriptor.JavaType.class
    );
    private final Supplier<RandomizerContext> randomizerContextSupplier;

    public ProtobufFieldValueGeneratorProvider(long seed, Supplier<RandomizerContext> randomizerContextSupplier) {
        this.randomizerContextSupplier = randomizerContextSupplier;
        this.random = new Random(seed);
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

    public ProtobufFieldValueGenerator get(Descriptors.FieldDescriptor.JavaType javaType) {
        return fieldGenerators.get(javaType);
    }

    private <T> ProtobufFieldValueGenerator generatorForBasicType(Class<T> type) {
        return (field, containingBuilder) -> getRandomValueForType(type);
    }

    private ProtobufFieldValueGenerator generatorForEnum() {
        return (field, containingBuilder) -> {
            List<Descriptors.EnumValueDescriptor> values = field.getEnumType().getValues();
            int choice = random.nextInt(values.size());
            return values.get(choice);
        };
    }

    private ProtobufFieldValueGenerator generatorForProtoMessage() {
        return (field, containingBuilder) ->
            getRandomValueForType(containingBuilder.newBuilderForField(field).getDefaultInstanceForType().getClass());
    }

    private <T> T getRandomValueForType(Class<T> type) {
        return getRandomizerForType(type).getRandomValue();
    }

    private <T> Randomizer<T> getRandomizerForType(Class<T> type) {
        RandomizerContext randomizerContext = randomizerContextSupplier.get();
        Randomizer<T> randomizer = randomizerContext
            .getParameters()
            .getRandomizerProvider()
            .getRandomizerByType(type, randomizerContext);
        if (randomizer instanceof ContextAwareRandomizer) {
            ((ContextAwareRandomizer<?>) randomizer).setRandomizerContext(randomizerContext);
        }
        return randomizer;
    }
}
