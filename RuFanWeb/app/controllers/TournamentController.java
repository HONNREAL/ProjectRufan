package controllers;

import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.tournaments;
import views.html.tournament;

import java.util.Date;
import java.util.List;

/**
 * Created by Keli on 22 / 10 / 15.
 */
public class TournamentController extends Controller {
    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");

    public Result index()
    {
        TournamentService tournamentService = (TournamentService) tctx.getBean("tournamentService");
        List<Tournament> tournamentList = tournamentService.getTournaments();
        return ok(tournaments.render(tournamentList));
    }

    public Result tournament(int id){
        TournamentService tournamentService = (TournamentService) tctx.getBean("tournamentService");
        Tournament t = tournamentService.getTournamentById(id);
        Date today = new Date();
        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().before(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t));
        }else{
            return redirect("/PageNotFound");
        }
    }
}
