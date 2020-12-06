package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.misc.BooleanRandomizer;
import org.jeasy.random.randomizers.number.DoubleRandomizer;
import org.jeasy.random.randomizers.number.FloatRandomizer;
import org.jeasy.random.randomizers.number.IntegerRandomizer;
import org.jeasy.random.randomizers.number.LongRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.*;

/**
 * Generate a random Protobuf {@link Message}.
 */
public class ProtobufMessageRandomizer implements Randomizer<Message> {

    private final Class<Message> messageClass;
    private final Random random;
    private final IntegerRangeRandomizer collectionSizeRandomizer;
    private final EnumMap<JavaType, BiFunction<FieldDescriptor, Builder, Object>> fieldGenerators;

    public ProtobufMessageRandomizer(Class<Message> messageClass, EasyRandom easyRandom, EasyRandomParameters parameters) {
        this.messageClass = messageClass;
        this.random = new Random(parameters.getSeed());
        this.collectionSizeRandomizer = new IntegerRangeRandomizer(
            parameters.getCollectionSizeRange().getMin(),
            parameters.getCollectionSizeRange().getMax(),
            random.nextLong()
        );

        this.fieldGenerators = new EnumMap<>(JavaType.class);
        IntegerRandomizer integerRandomizer = new IntegerRandomizer(parameters.getSeed());
        this.fieldGenerators.put(INT, (field, containingBuilder) -> integerRandomizer.getRandomValue());
        LongRandomizer longRandomizer = new LongRandomizer(parameters.getSeed());
        this.fieldGenerators.put(LONG, (field, containingBuilder) -> longRandomizer.getRandomValue());
        FloatRandomizer floatRandomizer = new FloatRandomizer(parameters.getSeed());
        this.fieldGenerators.put(FLOAT, (field, containingBuilder) -> floatRandomizer.getRandomValue());
        DoubleRandomizer doubleRandomizer = new DoubleRandomizer(parameters.getSeed());
        this.fieldGenerators.put(DOUBLE, (field, containingBuilder) -> doubleRandomizer.getRandomValue());
        BooleanRandomizer booleanRandomizer = new BooleanRandomizer(parameters.getSeed());
        this.fieldGenerators.put(BOOLEAN, (field, containingBuilder) -> booleanRandomizer.getRandomValue());
        StringRandomizer stringRandomizer = new StringRandomizer(parameters.getSeed());
        this.fieldGenerators.put(STRING, (field, containingBuilder) -> stringRandomizer.getRandomValue());
        ByteStringRandomizer byteStringRandomizer = new ByteStringRandomizer(parameters.getSeed());
        this.fieldGenerators.put(BYTE_STRING, (field, containingBuilder) -> byteStringRandomizer.getRandomValue());
        this.fieldGenerators.put(ENUM, (field, containingBuilder) -> getRandomEnumValue(field.getEnumType()));
        this.fieldGenerators.put(MESSAGE, (field, containingBuilder) -> easyRandom.nextObject(
            containingBuilder.newBuilderForField(field).getDefaultInstanceForType().getClass()
        ));
    }

    @Override
    public Message getRandomValue() {
        Message defaultInstance = instantiateMessage(messageClass);
        Builder builder = defaultInstance.newBuilderForType();
        Descriptor descriptor = builder.getDescriptorForType();
        for (FieldDescriptor field : descriptor.getFields()) {
            populateField(field, builder);
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
            for (int i = 0; i < collectionSizeRandomizer.getRandomValue(); i++) {
                containingBuilder.addRepeatedField(field, generator.apply(field, containingBuilder));
            }
        } else {
            containingBuilder.setField(field, generator.apply(field, containingBuilder));
        }
    }

    private EnumValueDescriptor getRandomEnumValue(EnumDescriptor enumDescriptor) {
        List<EnumValueDescriptor> values = enumDescriptor.getValues();
        return values.get(random.nextInt(values.size()));
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

}
