package controllers;

import is.rufan.team.service.GameService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentGameService;
import is.rufan.tournament.service.TournamentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.*;

import views.html.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller controls the data flow into the Tournament model object
 * and updates the view index.
 * Invoked by the route: localhost:9000/
 * @author Gunnar Orri Kjartansson
 * @author Þorkell Viktor Þorsteinsson
 */
public class Application extends Controller {

    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    protected ApplicationContext tgctx = new FileSystemXmlApplicationContext("/conf/tournamentgameapp.xml");
    protected ApplicationContext gctx = new FileSystemXmlApplicationContext("/conf/gameapp.xml");

    private TournamentService tournamentService;
    private TournamentGameService tournamentGameService;
    private GameService gameService;

    /**
     * Constructor for Application. Set required services using beans.
     */
    public Application(){
        tournamentService = (TournamentService) tctx.getBean("tournamentService");
        tournamentGameService = (TournamentGameService) tgctx.getBean("tournamentGameService");
        gameService = (GameService) gctx.getBean("gameService");
    }
    /**
     * Search for active tournaments, add them to a list of active tournaments and return
     * to view for rendering.
     * @return Provide index.scala.htm with list of active tournaments for rendering, status 200 OK.
     */
    public Result index()
    {
        List<Tournament> tournamentList = tournamentService.getTournaments();

        List<Tournament> activeTournaments = new ArrayList<Tournament>();
        Date today = new Date();

        for(Tournament t : tournamentList){
            if(t.getStartDate() != null || t.getEndDate() != null){
                if(t.getEndDate().after(today)){
                    activeTournaments.add(t);
                }

            }
        }
        return ok(index.render(activeTournaments));
    }

    /**
     * If a page is not found, this function returns the notfound view for rendering.
     * @return notfound view for rendering, status 200 OK
     */
    public Result PageNotFound()
    {
        return ok(notfound.render());
    }

}
