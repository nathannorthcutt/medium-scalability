package scalability;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * Helper class for creating servers
 */
public class JettyServer {

    /**
     * Build a new server with HTTP 1.1/2.0 support
     * 
     * @param port The port to use
     * 
     * @return A new {@link Server} bound to the given port
     */
    public static final Server setupServer(int port, boolean enableHttp2) throws Exception {

        // Create a server
        final var server = new Server(new QueuedThreadPool(20));
        server.setStopAtShutdown(true);
        server.setDumpBeforeStop(false);
        server.setDumpAfterStart(false);

        // Setup the configuration
        final var httpConfiguration = new HttpConfiguration();
        httpConfiguration.setIdleTimeout(30000);
        httpConfiguration.setOutputBufferSize(32768);
        httpConfiguration.setRequestHeaderSize(8192);
        httpConfiguration.setResponseHeaderSize(8192);
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.setSendDateHeader(true);
        httpConfiguration.setSendXPoweredBy(false);

        var http11 = new HttpConnectionFactory(httpConfiguration);
        var http2 = new HTTP2CServerConnectionFactory(httpConfiguration);
        // Add HTTP protocols
        var connector = enableHttp2 ? new ServerConnector(server, http2, http11)
            : new ServerConnector(server, http11);

        // Setup the server connector
        connector.setPort(8081);
        connector.setReuseAddress(true);
        connector.setIdleTimeout(10000);
        server.addConnector(connector);

        // Add a collection for handlers that can be modified at runtime
        server.setHandler(new HandlerCollection(true));

        return server;
    }
}
