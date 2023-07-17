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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.github.murdos.easyrandom.protobuf.testing.proto3.Proto3Message;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

class ThreadSafetyTest {

    private final EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).collectionSizeRange(3, 10);
    private final EasyRandom easyRandom = new EasyRandom(parameters);

    @Test
    void shouldNotThrowConcurrentModificationExceptionWhenTryingToNext() {
        // GIVEN:
        int threadsCount = 250; // the more, the better :)
        CountDownLatch latch = new CountDownLatch(threadsCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        Callable<Proto3Message> callable = () -> {
            latch.countDown();
            startLatch.await();
            return easyRandom.nextObject(Proto3Message.class);
        };
        Stream<Callable<Proto3Message>> tasks = Stream.generate(() -> callable).limit(threadsCount);

        // WHEN-THEN:
        assertDoesNotThrow(
            () -> {
                List<Future<Proto3Message>> results = tasks.map(executor::submit).collect(Collectors.toList());
                latch.await();
                startLatch.countDown();
                executor.awaitTermination(5L, TimeUnit.SECONDS);
                results.forEach(
                    it -> {
                        try {
                            it.get();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                );
            }
        );
    }
}
