package scalability;

import java.io.IOException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SimpleHandler extends AbstractHandler {

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        // Write a response
        try (var out = response.getOutputStream()) {
            out.write("{\"status\":\"ok\"}".getBytes());
            out.flush();
        } catch (Throwable t) {

        } finally {
            baseRequest.setHandled(true);
        }
    }

}
