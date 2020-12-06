package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.annotation.Priority;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.api.RandomizerRegistry;

import java.lang.reflect.Field;

/**
 * A registry of randomizers for Protobuf messages.
 *
 */
@Priority(-2)
public class ProtobufRandomizerRegistry implements RandomizerRegistry {

    private EasyRandom easyRandom;
    private EasyRandomParameters parameters;

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
        if (Message.class.isAssignableFrom(type)) {
            if (easyRandom == null) {
                easyRandom = new EasyRandom(parameters);
            }
            return new ProtobufMessageRandomizer((Class<Message>) type, easyRandom, parameters);
        }
        return null;
    }
}
