package controllers;

import is.rufan.user.domain.User;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
    /**
     *
     * @return
     */
    public Result index()
    {
        return ok(index.render("RuFan"));
    }

}
