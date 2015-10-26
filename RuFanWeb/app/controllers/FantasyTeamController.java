package controllers;

import is.rufan.player.domain.Player;
import is.rufan.player.service.PlayerService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.tournament;

import java.util.Date;
import java.util.List;

/**
 * Created by Keli on 26 / 10 / 15.
 */
public class FantasyTeamController extends Controller {
    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    protected ApplicationContext pctx = new FileSystemXmlApplicationContext("/conf/playerapp.xml");

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
}
