package controllers;

import is.rufan.fantasyplayer.domain.FantasyPlayer;
import is.rufan.fantasyplayer.service.FantasyPlayerService;
import is.rufan.player.domain.Player;
import is.rufan.player.service.PlayerService;
import is.rufan.team.domain.Game;
import is.rufan.team.service.GameService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.domain.TournamentGame;
import is.rufan.tournament.service.TournamentGameService;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.tournament;

import java.util.ArrayList;
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
    protected ApplicationContext uctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    protected ApplicationContext tgctx = new FileSystemXmlApplicationContext("/conf/tournamentgameapp.xml");
    protected ApplicationContext gctx = new FileSystemXmlApplicationContext("/conf/gameapp.xml");


    private PlayerService playerService;
    private TournamentService tournamentService;
    private FantasyPlayerService fantasyPlayerService;
    private UserService userService;
    private TournamentGameService tournamentGameService;
    private GameService gameService;

    final static Form<FantasyPlayer> signupForm = form(FantasyPlayer.class);

    public FantasyTeamController(){
        playerService = (PlayerService) pctx.getBean("playerService");
        tournamentService = (TournamentService) tctx.getBean("tournamentService");
        fantasyPlayerService = (FantasyPlayerService) fctx.getBean("fantasyPlayerService");
        userService = (UserService) uctx.getBean("userService");
        tournamentGameService = (TournamentGameService) tgctx.getBean("tournamentGameService");
        gameService = (GameService) gctx.getBean("gameService");
    }

    public Result tournament(int id){
        List<Player> players = playerService.getPlayers();
        Tournament t = tournamentService.getTournamentById(id);
        Date today = new Date();
        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().before(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players, null));
        }else{
            return redirect("/PageNotFound");
        }
    }

    public Result buildFantasyTeam(int tournamentId){

        List<Player> players = playerService.getPlayers();


        Tournament t = tournamentService.getTournamentById(tournamentId);

        if(t == null){
            return redirect("/PageNotFound");
        }

        /*
        TournamentGame tournamentGame = new TournamentGame();

        for(Tournament tournament : tournamentService.getTournaments()){
            List<Game> games = gameService.getGames();
            for(Game game : games){
                tournamentGame.setGameId(game.getGameId());
                tournamentGame.setTournamentId(tournament.getId());
                tournamentGameService.addTournamentGame(tournamentGame);
            }
        }
        */

        int userId = userService.getUserByUsername(session().get("username")).getId();

        Date today = new Date();
        FantasyPlayer fantasyPlayer = new FantasyPlayer();
        fantasyPlayer.setTournamentId(tournamentId);
        fantasyPlayer.setUserId(userId);

        Form<FantasyPlayer> filledForm = signupForm.bindFromRequest();
        if(!filledForm.field("GoalKeeper").value().equals("-")) {
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("GoalKeeper").value()));
            //fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
        }

        List<FantasyPlayer> tournamentFantasyPlayers = fantasyPlayerService.getFantasyPlayersWithTournamentId(tournamentId);
        List<Player> usersFantasyPlayers = new ArrayList<Player>();
        Player newPlayer;
        for(FantasyPlayer fp : tournamentFantasyPlayers){
            if(fp.getUserId() == userId ){
                for(Player p : players){
                    if(p.getPlayerId() == fp.getPlayerId()){
                        usersFantasyPlayers.add(p);
                    }
                }
            }
        }

        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().before(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players, usersFantasyPlayers));
        }else{
            return redirect("/PageNotFound");
        }
    }
}
