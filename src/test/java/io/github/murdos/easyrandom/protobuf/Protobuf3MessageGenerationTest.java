package io.github.murdos.easyrandom.protobuf;

import com.google.protobuf.StringValue;
import io.github.murdos.easyrandom.protobuf.testing.proto3.EmbeddedProto3Message;
import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Enum;
import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Message;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Protobuf3MessageGenerationTest {

    @Test
    void generatedValuesShouldBeValidAccordingToValidationConstraints() {
        EasyRandom easyRandom = new EasyRandom();
        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.getDoubleField()).isNotZero();
        assertThat(protoInstance.getFloatField()).isNotZero();
        assertThat(protoInstance.getInt32Field()).isNotZero();
        assertThat(protoInstance.getInt64Field()).isNotZero();
        assertThat(protoInstance.getUint32Field()).isNotZero();
        assertThat(protoInstance.getUint64Field()).isNotZero();
        assertThat(protoInstance.getSint32Field()).isNotZero();
        assertThat(protoInstance.getSint64Field()).isNotZero();
        assertThat(protoInstance.getFixed32Field()).isNotZero();
        assertThat(protoInstance.getFixed64Field()).isNotZero();
        assertThat(protoInstance.getSfixed32Field()).isNotZero();
        assertThat(protoInstance.getSfixed64Field()).isNotZero();
        assertThat(protoInstance.getBoolField()).isTrue();
        assertThat(protoInstance.getStringField()).isNotBlank();
        assertThat(protoInstance.getBytesField()).isNotEmpty();
        assertThat(protoInstance.getEnumField()).isIn((Object[]) Proto3Enum.values());
        assertThat(protoInstance.getStringValueField()).isNotNull();
        assertThat(protoInstance.getRepeatedStringFieldList()).isNotEmpty();

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).satisfies(embeddedMessage -> {
            assertThat(embeddedMessage.getStringField()).isNotBlank();
            assertThat(embeddedMessage.getEnumField()).isIn((Object[]) Proto3Enum.values());
        });
    }

    @Test
    void shouldUseCollectionSizeRangeParameters() {
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(3, 3);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.getRepeatedStringFieldList()).hasSize(3);
    }

    @Test
    void shouldGenerateTheSameValueForTheSameSeed() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(123L)
                .collectionSizeRange(3, 10);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.getDoubleField()).isEqualTo(0.7231742029971469);
        assertThat(protoInstance.getFloatField()).isEqualTo(0.72317415f);
        assertThat(protoInstance.getInt32Field()).isEqualTo(-1188957731);
        assertThat(protoInstance.getInt64Field()).isEqualTo(-5106534569952410475L);
        assertThat(protoInstance.getUint32Field()).isEqualTo(1018954901);
        assertThat(protoInstance.getUint64Field()).isEqualTo(-167885730524958550L);
        assertThat(protoInstance.getSint32Field()).isEqualTo(-39088943);
        assertThat(protoInstance.getSint64Field()).isEqualTo(4672433029010564658L);
        assertThat(protoInstance.getFixed32Field()).isEqualTo(1295249578);
        assertThat(protoInstance.getFixed64Field()).isEqualTo(-7216359497931550918L);
        assertThat(protoInstance.getSfixed32Field()).isEqualTo(1087885590);
        assertThat(protoInstance.getSfixed64Field()).isEqualTo(-3581075550420886390L);
        assertThat(protoInstance.getBoolField()).isTrue();
        assertThat(protoInstance.getStringField()).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
        assertThat(protoInstance.getBytesField().toByteArray()).containsExactly(
                -35, -15, 33, -71, -107, 4, -68, 60, -47, -116, -85, -3, -86, -16, 51, 77,
                22, -47, -41, 64, 50, 38, -6, -110, 69, 87, -38, -101, 58, 15, 70, 66
        );
        assertThat(protoInstance.getEnumField()).isEqualTo(Proto3Enum.SECOND_VALUE);
        assertThat(protoInstance.getStringValueField())
                .isNotNull()
                .extracting(StringValue::getValue).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
        assertThat(protoInstance.getRepeatedStringFieldList()).containsExactly(
                "yedUsFwdkelQbxeTeQOvaScfqIOOmaa",
                "JxkyvRnL",
                "RYtGKbgicZaHCBRQDSx",
                "VLhpfQGTMDYpsBZxvfBoeygjb",
                "UMaAIKKIkknjWEXJUfPxxQHeWKEJ"
        );

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).satisfies(embeddedMessage -> {
            assertThat(embeddedMessage.getStringField()).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
            assertThat(embeddedMessage.getEnumField()).isEqualTo(Proto3Enum.SECOND_VALUE);
        });
    }

    @Test
    void shouldUseCustomRandomizerForSubMessageWhenItsDefined() {
        EmbeddedProto3Message customEmbeddedMessage = EmbeddedProto3Message.newBuilder()
                .setStringField("custom string value")
                .setEnumField(Proto3Enum.FIRST_VALUE)
                .build();
        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(EmbeddedProto3Message.class, () -> customEmbeddedMessage);
        EasyRandom easyRandom = new EasyRandom(parameters);

        Proto3Message protoInstance = easyRandom.nextObject(Proto3Message.class);

        assertThat(protoInstance.hasEmbeddedMessage()).isTrue();
        assertThat(protoInstance.getEmbeddedMessage()).isEqualTo(customEmbeddedMessage);
    }
}
