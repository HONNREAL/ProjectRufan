package controllers;


import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.data.UserNotFoundException;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.*;
import play.mvc.*;

import static play.data.Form.form;

import views.html.index;
import views.html.login;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginController extends UserController
{
  final static Form<User> loginForm = form(User.class);
  protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");

  public Result blank()
  {
    return ok(login.render(loginForm));
  }

  public Result login()
  {
    Form<User> filledForm = loginForm.bindFromRequest();

    UserService service = (UserService) ctx.getBean("userService");


    User user = service.getUserByUsername(filledForm.field("username").value());
    if (user == null)
    {
      filledForm.reject("password", "User not found or incorrect password entered.");
      return badRequest(login.render(filledForm));
    }

    if (!user.getPassword().equals(filledForm.field("password").value()))
    {
      filledForm.reject("password", "User not found or incorrect password entered.");
      return badRequest(login.render(filledForm));
    }

    session("username", user.getUsername());
    session("displayName", user.getName());

    TournamentService tournamentService = (TournamentService) tctx.getBean("tournamentService");
    List<Tournament> tournamentList = tournamentService.getTournaments();
    List<Tournament> activeTournaments = new ArrayList<Tournament>();
    Date today = new Date();

    for(Tournament t : tournamentList){
      if(t.getStartDate() != null || t.getEndDate() != null){
        if(t.getStartDate().before(today) && t.getEndDate().after(today)){
          activeTournaments.add(t);
        }

      }
    }

    if (session().get("username") != null)
    {
      return ok(index.render(activeTournaments));
    }
    else
      return redirect("/");
  }

  public Result logout()
  {
    session().clear();
    return redirect("/");
  }
}
