package controllers;

import is.rufan.user.domain.UserRegistration;
import is.rufan.user.service.UserService;
import play.mvc.*;
import play.data.*;
import views.html.signup;
import views.html.summary;
import static play.data.Form.*;

/**
 * Validates login credentials.
 * Invoked by the route: localhost:9000/signup
 * @author Gunnar Orri Kjartansson
 * @author Þorkell Viktor Þorsteinsson
 */
public class SignupController extends UserController
{
  final static Form<UserRegistration> signupForm = form(UserRegistration.class);

  /**
   * Blank signup form.
   * @return Provide view signup.scala.html with a blank signup form, status 200 OK.
   */
  public Result blank()
  {
    return ok(signup.render(signupForm));
  }

  /**
   * Validate sign up credentials.
   * @return Provide view summary.scala.html with the user created, status 200 OK.
   */
  public Result signup()
  {
    Form<UserRegistration> filledForm = signupForm.bindFromRequest();

    UserService service = (UserService) ctx.getBean("userService");

    if (!"true".equals(filledForm.field("accept").value()))
    {
      filledForm.reject("accept", "You must accept the terms and conditions");
    }

    if(service.getUserByUsername(filledForm.field("username").value()) != null)
    {
        filledForm.reject("username", "Username already exists");
    }

    if (filledForm.field("username").value().length() < 4)
    {
      filledForm.reject("username", "Display Name must be at least 4 characters");
    }

    if (!filledForm.field("password").value().equals(filledForm.field("repeatPassword").value()))
    {
      filledForm.reject("repeatPassword", "The passwords you entered don't match");
    }

    if (filledForm.field("password").value().length() < 6)
    {
      filledForm.reject("password", "The password is too short");
    }

    if (filledForm.hasErrors())
    {
      return badRequest(signup.render(filledForm));
    }
    else
    {
      UserRegistration created = filledForm.get();
      service.addUser(created);
      return ok(summary.render(created));
    }
  }
}
