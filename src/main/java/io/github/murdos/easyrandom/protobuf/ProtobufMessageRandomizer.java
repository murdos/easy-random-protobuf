package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
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
import java.util.List;
import java.util.Random;

/**
 * Generate a random Protobuf {@link Message}.
 */
public class ProtobufMessageRandomizer implements Randomizer<Message> {

    private final Class<Message> messageClass;
    private final EasyRandom easyRandom;
    private final EasyRandomParameters parameters;
    private final Random random;
    private final LongRandomizer longRandomizer;
    private final BooleanRandomizer booleanRandomizer;
    private final DoubleRandomizer doubleRandomizer;
    private final StringRandomizer stringRandomizer;
    private final IntegerRandomizer integerRandomizer;
    private final FloatRandomizer floatRandomizer;
    private final ByteStringRandomizer byteStringRandomizer;

    public ProtobufMessageRandomizer(Class<Message> messageClass, EasyRandom easyRandom, EasyRandomParameters parameters) {
        this.messageClass = messageClass;
        this.parameters = parameters;
        this.easyRandom = easyRandom;
        this.random = new Random(parameters.getSeed());
        this.booleanRandomizer = new BooleanRandomizer(parameters.getSeed());
        this.doubleRandomizer = new DoubleRandomizer(parameters.getSeed());
        this.stringRandomizer = new StringRandomizer(parameters.getSeed());
        this.longRandomizer = new LongRandomizer(parameters.getSeed());
        this.integerRandomizer = new IntegerRandomizer(parameters.getSeed());
        this.floatRandomizer = new FloatRandomizer(parameters.getSeed());
        this.byteStringRandomizer = new ByteStringRandomizer(parameters.getSeed());
    }

    @Override
    public Message getRandomValue() {
        Message defaultInstance = instantiateMessage(messageClass);
        return getRandomMessageValue(defaultInstance.newBuilderForType());
    }

    private Message instantiateMessage(Class<Message> clazz) {
        try {
            Method getDefaultInstanceMethod = clazz.getMethod("getDefaultInstance");
            return (Message) getDefaultInstanceMethod.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Message getRandomMessageValue(Builder builder) {
        Descriptor descriptor = builder.getDescriptorForType();
        for (FieldDescriptor field : descriptor.getFields()) {
            populateField(field, builder);
        }
        return builder.build();
    }

    private void populateField(FieldDescriptor field, Builder containingBuilder) {
        if (field.isRepeated()) {
            int randomSize = getRandomCollectionSize(parameters);
            for (int i = 0; i < randomSize; i++) {
                containingBuilder.addRepeatedField(field, getFieldValue(field, containingBuilder));
            }
        } else {
            containingBuilder.setField(field, getFieldValue(field, containingBuilder));
        }
    }

    private int getRandomCollectionSize(EasyRandomParameters parameters) {
        EasyRandomParameters.Range<Integer> collectionSizeRange = parameters.getCollectionSizeRange();
        return new IntegerRangeRandomizer(collectionSizeRange.getMin(), collectionSizeRange.getMax(), random.nextLong()).getRandomValue();
    }

    private Object getFieldValue(FieldDescriptor field, Builder containingBuilder) {
        switch (field.getJavaType()) {
            case INT:
                return integerRandomizer.getRandomValue();
            case LONG:
                return longRandomizer.getRandomValue();
            case FLOAT:
                return floatRandomizer.getRandomValue();
            case DOUBLE:
                return doubleRandomizer.getRandomValue();
            case BOOLEAN:
                return booleanRandomizer.getRandomValue();
            case STRING:
                return stringRandomizer.getRandomValue();
            case BYTE_STRING:
                return byteStringRandomizer.getRandomValue();
            case ENUM:
                return getRandomEnumValue(field.getEnumType());
            case MESSAGE:
                return easyRandom.nextObject(containingBuilder.newBuilderForField(field).getDefaultInstanceForType().getClass());
            default:
                throw new IllegalArgumentException("Unhandled JavaType: " + field.getJavaType());
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
