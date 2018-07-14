package framework.controllers;

import engine.base.BoaExchange;
import engine.controller.Controller;
import engine.controller.Param;
import engine.controller.Path;
import framework.entities.Firm;
import framework.entities.Matter;


import java.util.List;


@Controller
public abstract class HomeController {

    @Path(uri = "/hello/{bob}")
    public static void index(BoaExchange client, @Param String bob) {
        Firm firm = Firm.getOne(1);
        List<Matter> matterList = firm.getMatters();

        System.out.println("Firm name: " + firm.name + " / " + matterList.size());

        client.render("index");
    }
}
