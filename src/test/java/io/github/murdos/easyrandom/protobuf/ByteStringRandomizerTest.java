package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ByteStringRandomizerTest {

    private static final long SEED = 123L;

    @Test
    void generatedByteStringShouldNotBeNull() {
        ByteString generatedValue = new ByteStringRandomizer().getRandomValue();
        assertThat(generatedValue).isNotNull();
    }

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        int[] expectedByteArray = {-35, -15, 33, -71, -107, 4, -68, 60, -47, -116, -85, -3, -86, -16, 51, 77, 22, -47,
                -41, 64, 50, 38, -6, -110, 69, 87, -38, -101, 58, 15, 70, 66};
        ByteString generatedValue = new ByteStringRandomizer(SEED).getRandomValue();
        assertThat(generatedValue.toByteArray()).containsExactly(expectedByteArray);
    }


}
