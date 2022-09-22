package scalability;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Simple class to emulate I/O operations from a downstream system
 */
public class IOSimulator {

    // Create a buffer so we aren't doing translation of bytes from the string every
    // operation
    private final static ByteBuffer BUFFER =
        ByteBuffer.wrap("{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8));

    // Pareto distribution factors
    public final static double PARETO_90_10 = 1.0d / 1.05;
    public final static double PARETO_80_20 = 1.0d / 1.16d;
    public final static double PARETO_70_30 = 1.0d / 1.42d;

    /**
     * Creates a future that will complete after a delay that has an average around
     * the given duration but has a long tail (Pareto distribution with 80:20)
     *
     * @param duration The minimum {@link Duration} to wait
     * @param max The maximum {@link Duration} to wait
     * @return A {@link CompletableFuture} that contains the returned {@link ByteBuffer}
     */
    public static final CompletableFuture<ByteBuffer> longTailDelay(Duration duration, Duration max) {
        return longTailDelay(duration, max, PARETO_80_20);
    }

    /**
     * Creates a future that will complete after a delay that has an average around
     * the given duration but has a long tail (Pareto distribution with 80:20)
     *
     * @param duration The minimum {@link Duration} to wait
     * @param max The maximum {@link Duration} to wait
     * @param distribution The distribution factor to use for the long tail
     *            calculation
     * @return A {@link CompletableFuture} that contains the returned {@link ByteBuffer}
     */
    public static final CompletableFuture<ByteBuffer> longTailDelay(
        Duration duration,
        Duration max,
        double distribution) {

        // Verify bounds
        assert duration != null && duration.toMillis() > 0 : "invalid duration";
        assert max != null && max.toMillis() > 0 && max.toMillis() > duration.toMillis() : "invalid maximum value";

        // Create an empty future container
        final var cf = new CompletableFuture<ByteBuffer>();

        // Calculate the delay for this call
        final var delay = (long) Math.min(
            duration.toMillis() / Math.pow(ThreadLocalRandom.current().nextDouble(), distribution),
            max.toMillis());

        // Use the delayed executor provided by the CompletableFuture infrastructure
        CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
            .execute(() -> cf.complete(BUFFER.asReadOnlyBuffer()));

        // Return the future
        return cf;
    }

    /**
     * Creates a future that will complete after a delay that has an average around
     * the given duration min/max values
     *
     * @param min The minimum {@link Duration} to wait
     * @param max The maximum {@link Duration} to wait
     * @return A {@link CompletableFuture} that contains the returned {@link ByteBuffer}
     */
    public static final CompletableFuture<ByteBuffer> delay(Duration min, Duration max) {
        // Create an empty future container
        final var cf = new CompletableFuture<ByteBuffer>();

        // Calculate the delay in this call so we end up with a binomial distribution
        var delay =
            (long) (ThreadLocalRandom.current().nextDouble() * (max.toMillis() - min.toMillis())) + min.toMillis();

        // Use the delayed executor provided by the CompletableFuture infrastructure
        CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
            .execute(() -> cf.complete(BUFFER.asReadOnlyBuffer()));

        // Return the future
        return cf;
    }
}
