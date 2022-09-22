package scalability;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Request;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoadSheddingHandler extends AsyncHandler {

    final Semaphore GATE = new Semaphore(500);

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        try {
            if (GATE.tryAcquire(10, TimeUnit.MILLISECONDS)) {
                var ctx = request.startAsync();
                ctx.addListener(new AsyncListener() {

                    @Override
                    public void onComplete(AsyncEvent event) throws IOException {
                        GATE.release();
                    }

                    @Override
                    public void onTimeout(AsyncEvent event) throws IOException {}

                    @Override
                    public void onError(AsyncEvent event) throws IOException {}

                    @Override
                    public void onStartAsync(AsyncEvent event) throws IOException {}

                });
                super.handle(target, baseRequest, request, response);
            } else {
                response.setStatus(503);
                baseRequest.setHandled(true);
            }
        } catch (Throwable t) {
            response.setStatus(500);
            baseRequest.setHandled(true);
        }
    }

}
