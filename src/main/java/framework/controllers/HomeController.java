package framework.controllers;

import engine.base.BoaExchange;
import engine.controller.Body;
import engine.controller.Controller;
import engine.controller.Param;
import engine.controller.Path;
import framework.config.Database;
import framework.entities.Firm;
import org.jooq.DSLContext;

import static framework.entities.jooq.Tables.FIRMS;

@Controller
public abstract class HomeController {

    @Path(uri = "/hello/{bob}")
    public static void index(BoaExchange client, @Param String bob) {
        Database db = new Database();
        DSLContext ctx = db.getContext();

        Firm firm = ctx.select()
                .from(FIRMS)
                .fetchAny()
                .into(Firm.class);

        System.out.println("Firm name: " + firm.name);

        client.render("index");
    }
}
