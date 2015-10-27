package controllers;

import is.rufan.player.domain.Player;
import is.rufan.player.service.PlayerService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.tournaments;
import views.html.tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Keli on 22 / 10 / 15.
 */
public class TournamentController extends Controller {
    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    private TournamentService tournamentService;

    public TournamentController() {
        tournamentService = (TournamentService) tctx.getBean("tournamentService");
    }

    public Result index()
    {

        List<Tournament> tournamentList = tournamentService.getTournaments();
        List<Tournament> activeTournaments = new ArrayList<Tournament>();
        List<Tournament> inActiveTournaments = new ArrayList<Tournament>();

        Date today = new Date();

        for(Tournament t : tournamentList){
            if(t.getStartDate() != null || t.getEndDate() != null){
                if(t.getEndDate().after(today)){
                    activeTournaments.add(t);
                } else{
                    inActiveTournaments.add(t);
                }

            }
        }

        return ok(tournaments.render(inActiveTournaments, activeTournaments));
    }

}
