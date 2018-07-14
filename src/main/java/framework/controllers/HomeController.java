package framework.controllers;

import engine.base.BoaExchange;
import engine.controller.Controller;
import engine.controller.Param;
import engine.controller.Path;

import java.util.HashMap;
import java.util.Map;


@Controller
public abstract class HomeController {

    @Path(uri = "/hello/{name}")
    public static void index(BoaExchange client, @Param String name) {

        Map<String, String> context = new HashMap<>() {{
            put("name", name);
        }};

        client.render("index", context);
    }
}
