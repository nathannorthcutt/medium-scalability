package scalability;

import java.io.IOException;
import java.nio.channels.Channels;
import java.time.Duration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LongTailHandler extends AbstractHandler {

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        var ctx = request.startAsync();
        ctx.setTimeout(1000);
        baseRequest.setHandled(true);

        IOSimulator.longTailDelay(Duration.ofMillis(10), Duration.ofMillis(100)).handle((res, err) -> {
            try {
                if (res != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");

                    try {
                        Channels.newChannel(response.getOutputStream()).write(res);
                    } catch (Throwable t) {

                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } finally {
                ctx.complete();
            }

            return res;
        });
    }

}
