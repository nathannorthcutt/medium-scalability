package scalability;

import java.io.IOException;
import java.nio.channels.Channels;
import java.time.Duration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AsyncHandler extends AbstractHandler {

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        final var ctx = request.isAsyncStarted() ? request.getAsyncContext() : request.startAsync();
        ctx.setTimeout(Duration.ofSeconds(1).toMillis());
        baseRequest.setHandled(true);

        ctx.addListener(new AsyncListener() {

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                event.getAsyncContext().complete();
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {}

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {}

        });

        IOSimulator.delay(Duration.ofMillis(10), Duration.ofMillis(50)).thenAccept(buffer -> {
            // Set the status and content type
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            // Write a response
            try (var out = response.getOutputStream()) {
                Channels.newChannel(out).write(buffer);
                out.flush();
            } catch (Throwable t) {

            } finally {
                ctx.complete();
            }
        });
    }

}
