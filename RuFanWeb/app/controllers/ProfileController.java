package controllers;

import is.rufan.team.domain.Team;
import is.rufan.team.service.TeamService;
import is.rufan.user.domain.User;
import is.rufan.user.domain.UserRegistration;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Result;
import views.html.profile;

import java.text.ParseException;
import java.util.List;

import static play.data.Form.*;

/**
 * Created by Keli on 21 / 10 / 15.
 */
public class ProfileController extends UserController {

    final static Form<UserRegistration> signupForm = form(UserRegistration.class);

    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/teamapp.xml");

    public Result index()
    {
        TeamService teamService = (TeamService) tctx.getBean("teamService");
        List<Team> teams = teamService.getTeams();

        UserService service = (UserService) ctx.getBean("userService");
        User user = service.getUserByUsername(session().get("username"));
        session().put("email", user.getEmail());

        return ok(profile.render(teams, signupForm, user, user.getCardNumber().substring(12)));
    }

    public Result update()
    {
        Form<UserRegistration> filledForm = signupForm.bindFromRequest();
        UserService service = (UserService) ctx.getBean("userService");
        User user = service.getUserByUsername(session().get("username"));

        TeamService teamService = (TeamService) tctx.getBean("teamService");
        List<Team> teams = teamService.getTeams();

        if (filledForm.field("cardNumber").value().length() != 16)
        {
            //filledForm.reject("cardNumber", "The card number is not 16 digits");
        }
        else{
            user.setCardNumber(filledForm.get().getCardNumber());

        }

        if(!filledForm.field("cardMonth").value().toString().equals("-"))
        {
            user.setCardMonth(filledForm.field("cardMonth").value().toString());
        }

        if(!filledForm.field("cardYear").value().toString().equals("-")) {
            user.setCardYear(filledForm.field("cardYear").value().toString());
        }

        if(!filledForm.field("favteam").value().toString().equals("-")) {
            int favTeamId = 0;
            try{
                favTeamId = Integer.parseInt(filledForm.field("favteam").value());
            }catch(Exception e){
                //return badRequest(profile.render(teams, filledForm, user));
            }
            user.setFavTeamId(favTeamId);

        }

        if (filledForm.hasErrors())
        {
            return badRequest(profile.render(teams, filledForm, user, user.getCardNumber().substring(12)));
        }
        else
        {
            service.updateUser(user);
            return ok(profile.render(teams, signupForm, user, user.getCardNumber().substring(12)));
        }


    }
}
