package crashbase;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.meshy.leanhttp.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static freemarker.template.Configuration.VERSION_2_3_23;
import static org.meshy.leanhttp.HttpRequests.GET;
import static org.meshy.leanhttp.HttpRequests.POST;
import static org.meshy.leanhttp.HttpResponses.*;
import static org.meshy.leanhttp.HttpStatus.SEE_OTHER;

public class Webapp implements HttpHandler {

    private final Crashbase crashbase;
    private final HttpRouter router;
    private final Configuration freemarker;

    public Webapp(Crashbase crashbase) {
        this.crashbase = crashbase;
        router = new HttpRouter();
        router.on(GET, "/", this::index);
        router.on(POST, "/dumps", this::receiveDump);

        freemarker = new Configuration(VERSION_2_3_23);
        freemarker.setTemplateLoader(new ClassTemplateLoader(getClass(), "templates/"));
    }

    private HttpResponse receiveDump(HttpRequest request) throws IOException {
        String app = request.query("app").get();
        InputStream in = request.bodyStream();
        long dumpId = crashbase.storeDump(app, in);
        return redirect(SEE_OTHER, request.contextUri().resolve("dumps/" + dumpId).toString());
    }

    private HttpResponse index(HttpRequest request) {
        return render("index.ftl");
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return router.handle(request);
    }

    private HttpResponse render(String template, Object... keysAndValues) {
        Map<String,Object> model = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            model.put((String)keysAndValues[i], keysAndValues[i + 1]);
        }

        try {
            StringWriter sw = new StringWriter();
            freemarker.getTemplate(template).process(model, sw);
            HttpResponse response = ok(sw.toString());
            response.setHeader("Content-Type", "text/html; charset=utf-8");
            return response;
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
