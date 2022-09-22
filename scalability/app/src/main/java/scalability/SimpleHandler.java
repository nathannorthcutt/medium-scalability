package scalability;

import java.io.IOException;
import java.nio.channels.Channels;
import java.time.Duration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SimpleHandler extends AbstractHandler {

    // cache our payload bytes so we aren't calling this every time
    // private static final byte[] RESPONSE_BUFFER = "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8);

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        var buffer = IOSimulator.delay(Duration.ofMillis(10), Duration.ofMillis(50)).join();

        // Set the status and content type
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        // Write a response
        try (var out = response.getOutputStream()) {
            Channels.newChannel(out).write(buffer);
            out.flush();
        } catch (Throwable t) {

        } finally {
            baseRequest.setHandled(true);
        }
    }

}
