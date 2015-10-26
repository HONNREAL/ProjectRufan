package controllers;

import is.rufan.fantasyplayer.domain.FantasyPlayer;
import is.rufan.player.domain.Player;
import is.rufan.player.service.PlayerService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.tournament;

import java.util.Date;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by Keli on 26 / 10 / 15.
 */
public class FantasyTeamController extends Controller {
    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    protected ApplicationContext pctx = new FileSystemXmlApplicationContext("/conf/playerapp.xml");
    protected ApplicationContext fctx = new FileSystemXmlApplicationContext("/conf/fantasyplayerapp.xml");

    final static Form<FantasyPlayer> signupForm = form(FantasyPlayer.class);

    public Result tournament(int id){
        PlayerService playerService = (PlayerService) pctx.getBean("playerService");
        List<Player> players = playerService.getPlayers();
        TournamentService tournamentService = (TournamentService) tctx.getBean("tournamentService");
        Tournament t = tournamentService.getTournamentById(id);
        Date today = new Date();
        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().before(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players));
        }else{
            return redirect("/PageNotFound");
        }
    }

    public Result buildFantasyTeam(int tournamentId){
        PlayerService playerService = (PlayerService) pctx.getBean("playerService");
        List<Player> players = playerService.getPlayers();
        TournamentService tournamentService = (TournamentService) tctx.getBean("tournamentService");
        Tournament t = tournamentService.getTournamentById(tournamentId);
        Date today = new Date();

        Form<FantasyPlayer> filledForm = signupForm.bindFromRequest();
        if(filledForm.field("GoalKeeper").value() != null) {
            System.out.println(filledForm.field("GoalKeeper").value());
        }

        if(filledForm.field("Defender1").value() != null) {
            System.out.println(filledForm.field("Defender1").value());
        }

        if(filledForm.field("Defender2").value() != null) {
            System.out.println(filledForm.field("Defender2").value());
        }


        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().before(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players));
        }else{
            return redirect("/PageNotFound");
        }
    }
}
