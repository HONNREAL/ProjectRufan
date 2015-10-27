package controllers;

import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.*;
import play.mvc.*;
import static play.data.Form.form;
import views.html.login;

/**
 * Log in/out controller, sets or clears the session's username and displayName.
 * Invoked by the routes: localhost:9000/login
 *                        localhost:9000/logout
 * @author Gunnar Orri Kjartansson
 * @author Þorkell Viktor Þorsteinsson
 */
public class LoginController extends UserController
{
  final static Form<User> loginForm = form(User.class);
  protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");

  /**
   * A blank login form.
   * @return login view for rendering with the empty loginForm, status 200 OK
   */
  public Result blank()
  {
    return ok(login.render(loginForm));
  }

  /**
   * Validate log in form and set the session's user and display name.
   * @return redirect to index
   */
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

    return redirect("/");
  }

  /**
   * Handle logging out a user, i.e. clear the session.
   * @return redirect to index.
   */
  public Result logout()
  {
    session().clear();
    return redirect("/");
  }
}
