package controllers;

import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.*;
import play.mvc.*;

import views.html.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Application extends Controller {

    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    /**
     *
     * @return
     */
    public Result index()
    {
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
        return ok(index.render(activeTournaments));
    }

}
