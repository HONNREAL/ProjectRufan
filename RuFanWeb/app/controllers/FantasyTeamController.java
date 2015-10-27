package controllers;

import is.rufan.fantasyplayer.domain.FantasyPlayer;
import is.rufan.fantasyplayer.service.FantasyPlayerService;
import is.rufan.player.domain.Player;
import is.rufan.player.domain.Position;
import is.rufan.player.service.PlayerService;
import is.rufan.team.domain.Game;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
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
 * Controller that controls the flow of data into the fantasyTeam object ands
 * updates the tournament view for rendering.
 */
public class FantasyTeamController extends Controller {
    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    protected ApplicationContext pctx = new FileSystemXmlApplicationContext("/conf/playerapp.xml");
    protected ApplicationContext fctx = new FileSystemXmlApplicationContext("/conf/fantasyplayerapp.xml");
    protected ApplicationContext uctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    protected ApplicationContext tgctx = new FileSystemXmlApplicationContext("/conf/tournamentgameapp.xml");
    protected ApplicationContext gctx = new FileSystemXmlApplicationContext("/conf/gameapp.xml");
    protected ApplicationContext teamctx = new FileSystemXmlApplicationContext("/conf/teamapp.xml");


    private PlayerService playerService;
    private TournamentService tournamentService;
    private FantasyPlayerService fantasyPlayerService;
    private UserService userService;
    private TournamentGameService tournamentGameService;
    private GameService gameService;
    private TeamService teamService;

    final static Form<FantasyPlayer> signupForm = form(FantasyPlayer.class);

    public FantasyTeamController(){
        playerService = (PlayerService) pctx.getBean("playerService");
        tournamentService = (TournamentService) tctx.getBean("tournamentService");
        fantasyPlayerService = (FantasyPlayerService) fctx.getBean("fantasyPlayerService");
        userService = (UserService) uctx.getBean("userService");
        tournamentGameService = (TournamentGameService) tgctx.getBean("tournamentGameService");
        gameService = (GameService) gctx.getBean("gameService");
        teamService = (TeamService) teamctx.getBean("teamService");
    }

    public Result tournament(int id){
        List<Player> players = playerService.getPlayers();
        Tournament t = tournamentService.getTournamentById(id);
        Date today = new Date();

        int userId = userService.getUserByUsername(session().get("username")).getId();

        List<FantasyPlayer> tournamentFantasyPlayers = fantasyPlayerService.getFantasyPlayersWithTournamentId(id);
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
            return ok(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams()));
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

        int userId = userService.getUserByUsername(session().get("username")).getId();


        List<FantasyPlayer> tournamentFantasyPlayers = fantasyPlayerService.getFantasyPlayersWithTournamentId(tournamentId);
        List<Player> usersFantasyPlayers = new ArrayList<Player>();
        Player newPlayer;

        Date today = new Date();

        FantasyPlayer fantasyPlayer = new FantasyPlayer();
        fantasyPlayer.setTournamentId(tournamentId);
        fantasyPlayer.setUserId(userId);

        Form<FantasyPlayer> filledForm = signupForm.bindFromRequest();

        for(FantasyPlayer fp : tournamentFantasyPlayers){
            if(fp.getUserId() == userId ){
                for(Player p : players){
                    if(p.getPlayerId() == fp.getPlayerId()) {
                        usersFantasyPlayers.add(p);
                    }
                }
                filledForm.reject("GoalKeeper", "Team Already Built");
            }
        }

        if(filledForm.field("GoalKeeper").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Defender1").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Defender2").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Defender3").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Defender4").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Midfielder1").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Midfielder2").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Midfielder3").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Midfielder4").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Forward1").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        if(filledForm.field("Forward2").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }

        if(!filledForm.hasErrors()){
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("GoalKeeper").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Defender1").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Defender2").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Defender3").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Defender4").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Midfielder1").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Midfielder2").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Midfielder3").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Midfielder4").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Forward1").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            fantasyPlayer.setPlayerId(Integer.parseInt(filledForm.field("Forward2").value()));
            fantasyPlayerService.addFantasyPlayer(fantasyPlayer);

            usersFantasyPlayers.clear();
            tournamentFantasyPlayers.clear();
            tournamentFantasyPlayers = fantasyPlayerService.getFantasyPlayersWithTournamentId(tournamentId);
            for(FantasyPlayer fp : tournamentFantasyPlayers){
                if(fp.getUserId() == userId ){
                    for(Player p : players){
                        if(p.getPlayerId() == fp.getPlayerId()) {
                            usersFantasyPlayers.add(p);
                        }
                    }
                }
            }


        }else{
            return badRequest(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams()));
        }


        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().before(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams()));
        }else{
            return redirect("/PageNotFound");
        }
    }
}
