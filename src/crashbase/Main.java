package crashbase;

import org.meshy.leanhttp.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String args[]) throws IOException {
        ServerSocket socket = null;
        String bindAddr = null;
        int port = 8080;
        Path dataDir = Paths.get("data");

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-b":
                    bindAddr = args[++i];
                    break;
                case "-p":
                    port = Integer.parseInt(args[++i]);
                    break;
                case "-d":
                    dataDir = Paths.get(args[++i]);
                    break;
                case "-i":
                    socket = (ServerSocket) System.inheritedChannel();
                    break;
                default:
                    System.err.println("Usage: crashbase [-i] [-p PORT] [-b ADDR] [-d DIR]");
                    System.err.println("");
                    System.err.println("  -d DIR   data directory");
                    System.err.println("  -i        use inherited socket (stdin)");
                    System.err.println("  -p PORT   port to bind to");
                    System.err.println("  -b ADDR   address to bind to");
                    System.exit(1);
            }
        }

        if (socket == null) {
            socket = new ServerSocket(port, -1, InetAddress.getByName(bindAddr));
        }

        Crashbase crashbase = new Crashbase(dataDir);
        Webapp webapp = new Webapp(crashbase);
        HttpServer server = new HttpServer(webapp, socket);

        server.serve();
    }
}
