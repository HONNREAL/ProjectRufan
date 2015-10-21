package controllers;

import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import play.mvc.Result;
import views.html.index;
import views.html.profile;

/**
 * Created by Keli on 21 / 10 / 15.
 */
public class ProfileController extends UserController {
    public Result index()
    {
        UserService service = (UserService) ctx.getBean("userService");
        User u = service.getUserByUsername(session().get("username"));
        session().put("email", u.getEmail());
        return ok(profile.render("RuFan - Profile"));
    }
}
