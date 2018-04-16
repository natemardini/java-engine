package controllers;



import java.util.Random;


import engine.base.BoaExchange;
import engine.controller.Body;
import engine.controller.Controller;
import engine.controller.Param;
import engine.controller.Path;

@Controller
public abstract class HomeController {

    @Path(uri = "/hello/{bob}", method = "GET")
    public static void index(BoaExchange client, @Param String bob, @Body Object well) {
    	Random random = new Random();
    	try {
			Thread.sleep(random.nextInt(500));
		} catch (InterruptedException e) {
			System.out.println("OMG couldn't even wait properly! " + e);
		}
    	
        client.render("index");
    }
}
