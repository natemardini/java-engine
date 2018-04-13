package start;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class HomeController {

    public static void index(BoaSocket client) {
        client.respond(HttpHelpers.CREATED, "Howdy");
    }
}
