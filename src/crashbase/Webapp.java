package crashbase;

import org.meshy.leanhttp.*;

import java.io.IOException;

public class Webapp implements HttpHandler {
    private final Crashbase crashbase;
    private final HttpRouter router;

    public Webapp(Crashbase crashbase) {
        this.crashbase = crashbase;
        router = new HttpRouter();
        router.on(HttpRequests.GET, "/", this::index);
    }

    private HttpResponse index(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        return null;
    }
}
