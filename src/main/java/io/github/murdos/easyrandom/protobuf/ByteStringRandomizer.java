package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.ByteString;
import org.jeasy.random.api.Randomizer;

import java.util.Random;

/**
 * Generate a random Protobuf {@link ByteString}.
 */
public class ByteStringRandomizer implements Randomizer<ByteString> {

    private final Random random;

    public ByteStringRandomizer() {
        this.random = new Random();
    }

    public ByteStringRandomizer(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public ByteString getRandomValue() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return ByteString.copyFrom(bytes);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
