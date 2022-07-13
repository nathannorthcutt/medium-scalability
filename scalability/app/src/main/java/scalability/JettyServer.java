package scalability;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.ConnectionFactory;

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
    public static final Server setupServer(int port) {

        // Create a server
        final var server = new Server();
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

        // Add HTTP protocols
        var connector = new ServerConnector(
            server,
            new ConnectionFactory[] {new HttpConnectionFactory(httpConfiguration),
                new HTTP2CServerConnectionFactory(httpConfiguration)});

        // Setup the server connector
        connector.setPort(8081);
        connector.setReuseAddress(true);
        connector.setIdleTimeout(1000);
        server.addConnector(connector);

        return server;
    }
}
