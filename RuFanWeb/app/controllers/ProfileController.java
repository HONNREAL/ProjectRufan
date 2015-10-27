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
import java.util.List;
import java.util.logging.Logger;
import static play.data.Form.*;

/**
 * Controller that controls data flow to object User.
 * Invoked by the route: localhost:9000/profile
 * @author Gunnar Orri Kjartansson
 * @author Þorkell Viktor Þorsteinsson
 */
public class ProfileController extends UserController {

    final static Form<UserRegistration> signupForm = form(UserRegistration.class);

    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/teamapp.xml");
    Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * Get user information to display on profile page and provide a signup form.
     * @return Provide view profile.scala.html with a list of teams, a signup form,
     * the session's user and "blurred" card number for rendering, status 200 OK.
     */
    public Result index()
    {
        if(session().get("username") == null){
            return redirect("/");
        }
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

    /**
     * Validate the credit card form and update the card information and
     * render the profile view with the information.
     * @return Provide view profile.scala.html with with updated info from form, status 200 OK or 400 BadRequest.
     */
    public Result update() {
        Form<UserRegistration> filledForm = signupForm.bindFromRequest();
        UserService service = (UserService) ctx.getBean("userService");
        User user = service.getUserByUsername(session().get("username"));

        TeamService teamService = (TeamService) tctx.getBean("teamService");
        List<Team> teams = teamService.getTeams();
        String lastcardnum = "                ";
        // If anything about credit card has been filled, everything has to be.
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
        // If a team has been chosen from dropdown, parse the team ID and set it for the user.
        if(!filledForm.field("favteam").value().toString().equals("-")) {
            int favTeamId = 0;
            try{
                favTeamId = Integer.parseInt(filledForm.field("favteam").value());
            }catch(Exception e){
                log.severe("Could not parse teamID from dropdownlist in profile");
                return badRequest(profile.render(teams, filledForm, user, lastcardnum.substring(12)));
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
