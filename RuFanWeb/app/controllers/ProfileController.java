package controllers;

import is.rufan.team.domain.Team;
import is.rufan.team.service.TeamService;
import is.rufan.team.service.TeamServiceData;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import is.ruframework.domain.RuException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Result;
import views.html.profile;

import java.util.List;

/**
 * Created by Keli on 21 / 10 / 15.
 */
public class ProfileController extends UserController {

    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/teamapp.xml");

    public Result index()
    {
        TeamService teamService = (TeamService) tctx.getBean("teamService");
        List<Team> teams = teamService.getTeams();
        UserService service = (UserService) ctx.getBean("userService");
        User u = service.getUserByUsername(session().get("username"));
        session().put("email", u.getEmail());
        return ok(profile.render(teams));
    }
}
