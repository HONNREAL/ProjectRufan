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
        String lastcardnum = "                ";

        TeamService teamService = (TeamService) tctx.getBean("teamService");
        List<Team> teams = teamService.getTeams();

        UserService service = (UserService) ctx.getBean("userService");
        User user = service.getUserByUsername(session().get("username"));
        session().put("email", user.getEmail());

        if(user.getCardNumber() != null){
            lastcardnum = user.getCardNumber();
        }

        return ok(profile.render(teams, signupForm, user, lastcardnum.substring(12)));
    }

    public Result update() {
        Form<UserRegistration> filledForm = signupForm.bindFromRequest();
        UserService service = (UserService) ctx.getBean("userService");
        User user = service.getUserByUsername(session().get("username"));

        TeamService teamService = (TeamService) tctx.getBean("teamService");
        List<Team> teams = teamService.getTeams();
        String lastcardnum = "                ";
        if (!filledForm.field("cardNumber").value().isEmpty()
                || filledForm.field("creditCardType").value() != null
                || !filledForm.field("securityCode").value().isEmpty()
                || !filledForm.field("cardMonth").value().toString().equals("-")
                || !filledForm.field("cardYear").value().toString().equals("-")) {

            if (filledForm.field("cardNumber").value().isEmpty()||
                    filledForm.field("cardNumber").value().length() != 16 ||
                    !filledForm.field("cardNumber").value().matches("[0-9]{16}")){
                filledForm.reject("cardNumber", "Card number invalid.");
            }

            if (filledForm.field("securityCode").value().isEmpty() ||
                    filledForm.field("securityCode").value().length() != 3 ||
                    !filledForm.field("securityCode").value().matches("[0-9]{3}")){
                filledForm.reject("securityCode", "Security Code invalid.");
            }

            if (filledForm.field("cardMonth").value().toString().equals("-")) {
                filledForm.reject("cardNumber", "No month selected.");
            }

            if (filledForm.field("cardYear").value().toString().equals("-")) {
                filledForm.reject("cardNumber", "No year selected.");
            }

            if (filledForm.field("creditCardType").value() == null) {
                filledForm.reject("creditCardType", "No Credit card type selected.");
            }

            if (!filledForm.hasErrors())
            {
                user.setCardNumber(filledForm.get().getCardNumber());
                user.setCardMonth(filledForm.field("cardMonth").value().toString());
                user.setCardYear(filledForm.field("cardYear").value().toString());
                user.setSecurityCode(filledForm.field("securityCode").value().toString());
                user.setCardType(filledForm.field("creditCardType").value());
            }
         }

        if(!filledForm.field("favteam").value().toString().equals("-")) {
            int favTeamId = 0;
            try{
                favTeamId = Integer.parseInt(filledForm.field("favteam").value());
            }catch(Exception e){
                //return badRequest(profile.render(teams, filledForm, user));
            }
            if(!filledForm.hasErrors()) {
                user.setFavTeamId(favTeamId);
            }

        }

        if(user.getCardNumber() != null){
            lastcardnum = user.getCardNumber();
        }

        if (filledForm.hasErrors())
        {
            return badRequest(profile.render(teams, filledForm, user, lastcardnum.substring(12)));
        }
        else
        {
            service.updateUser(user);
            return ok(profile.render(teams, signupForm, user, lastcardnum.substring(12)));
        }


    }
}
