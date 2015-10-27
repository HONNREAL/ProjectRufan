package controllers;

import is.rufan.fantasyplayer.domain.FantasyPlayer;
import is.rufan.fantasyplayer.service.FantasyPlayerService;
import is.rufan.fantasyteam.domain.FantasyTeam;
import is.rufan.fantasyteam.service.FantasyTeamService;
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
    protected ApplicationContext ftctx = new FileSystemXmlApplicationContext("/conf/fantasyteamapp.xml");


    private PlayerService playerService;
    private TournamentService tournamentService;
    private FantasyPlayerService fantasyPlayerService;
    private UserService userService;
    private TournamentGameService tournamentGameService;
    private GameService gameService;
    private TeamService teamService;
    private FantasyTeamService fantasyTeamService;
    private List<Player> players;

    final static Form<FantasyPlayer> signupForm = form(FantasyPlayer.class);

    public FantasyTeamController(){
        playerService = (PlayerService) pctx.getBean("playerService");
        tournamentService = (TournamentService) tctx.getBean("tournamentService");
        fantasyPlayerService = (FantasyPlayerService) fctx.getBean("fantasyPlayerService");
        userService = (UserService) uctx.getBean("userService");
        tournamentGameService = (TournamentGameService) tgctx.getBean("tournamentGameService");
        gameService = (GameService) gctx.getBean("gameService");
        teamService = (TeamService) teamctx.getBean("teamService");
        fantasyTeamService = (FantasyTeamService) ftctx.getBean("fantasyTeamService");
        /* Get Playerlist in the constructor for FantasyTeamController
         * so that we only need to fetch the list once, since it takes
         * over 10 seconds to fetch.
         */
        players = playerService.getPlayers();
    }

    public Result tournament(int id){
        Tournament t = tournamentService.getTournamentById(id);
        if(t == null){
            return redirect("/PageNotFound");
        }
        String message = "";
        boolean isFull = false;
        List<FantasyTeam> fantasyTeams = fantasyTeamService.getFantasyTeamsByTournamentId(id);
        if(fantasyTeams.size() >= t.getMaxEntries()){
            message = "Tournament is full " + fantasyTeams.size() + "/" + t.getMaxEntries() + " teams enrolled.";
            isFull = true;
        }
        else{
            isFull = false;
            message = "Tournament is open for enrollment " + fantasyTeams.size() + "/" + t.getMaxEntries() + " teams enrolled.";
        }

        Date today = new Date();
        int userId = userService.getUserByUsername(session().get("username")).getId();
        List<FantasyPlayer> tournamentFantasyPlayers = null;
        List<Player> usersFantasyPlayers = new ArrayList<Player>();
        for(FantasyTeam ft : fantasyTeams){
            if(ft.getUserid() == userId){
                tournamentFantasyPlayers = fantasyPlayerService.getFantasyPlayersWithTournamentId(id);
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
                break;
            }
        }


        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().after(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams(), message, isFull));
        }else{
            return redirect("/PageNotFound");
        }
    }

    public Result buildFantasyTeam(int tournamentId){

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
        List<Integer> playerIds = new ArrayList<Integer>();
        if(filledForm.field("GoalKeeper").value().equals("-")) {
            filledForm.reject("Enroll", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("GoalKeeper").value()));
        }

        if(filledForm.field("Defender1").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Defender1").value()));
        }

        if(filledForm.field("Defender2").value().equals("-")) {
            filledForm.reject("Defender2", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Defender2").value()));
        }

        if(filledForm.field("Defender3").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Defender3").value()));
        }

        if(filledForm.field("Defender4").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Defender4").value()));
        }

        if(filledForm.field("Midfielder1").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Midfielder1").value()));
        }

        if(filledForm.field("Midfielder2").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Midfielder2").value()));
        }

        if(filledForm.field("Midfielder3").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Midfielder3").value()));
        }

        if(filledForm.field("Midfielder4").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Midfielder4").value()));
        }
        if(filledForm.field("Forward1").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Forward1").value()));
        }
        if(filledForm.field("Forward2").value().equals("-")) {
            filledForm.reject("GoalKeeper", "Team Already Built");
        }
        else{
            playerIds.add(Integer.parseInt(filledForm.field("Forward2").value()));
        }

        String message = "";
        boolean isFull = false;


        if(!filledForm.hasErrors()){
            for(int i = 0; i < playerIds.size(); i++){
                for(int j = 0; j < playerIds.size(); j++){
                    if(i != j){
                        if(playerIds.get(i).equals(playerIds.get(j))){
                            return badRequest(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams(), message, isFull));
                        }
                    }
                }
            }


            for(int PID : playerIds){
                fantasyPlayer.setPlayerId(PID);
                fantasyPlayerService.addFantasyPlayer(fantasyPlayer);
            }


            usersFantasyPlayers.clear();
            tournamentFantasyPlayers.clear();
            tournamentFantasyPlayers = fantasyPlayerService.getFantasyPlayersWithTournamentId(tournamentId);
            FantasyTeam fantasyTeam = new FantasyTeam();
            fantasyTeam.setStatus("OPEN");
            fantasyTeam.setTournamentId(tournamentId);
            fantasyTeam.setUserid(userId);
            fantasyTeamService.addFantasyTeam(fantasyTeam);

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
            List<FantasyTeam> fantasyTeams = fantasyTeamService.getFantasyTeamsByTournamentId(tournamentId);
            if(fantasyTeams.size() >= t.getMaxEntries()){
                message = "Tournament is full " + fantasyTeams.size() + "/" + t.getMaxEntries() + " teams enrolled.";
                isFull = true;
            }
            else{
                isFull = false;
                message = "Tournament is open for enrollment " + fantasyTeams.size() + "/" + t.getMaxEntries() + " teams enrolled.";
            }
            return badRequest(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams(), message, isFull));
        }

        List<FantasyTeam> fantasyTeams = fantasyTeamService.getFantasyTeamsByTournamentId(tournamentId);
        if(fantasyTeams.size() >= t.getMaxEntries()){
            message = "Tournament is full " + fantasyTeams.size() + "/" + t.getMaxEntries() + " teams enrolled.";
            isFull = true;
        }
        else{
            isFull = false;
            message = "Tournament is open for enrollment " + fantasyTeams.size() + "/" + t.getMaxEntries() + " teams enrolled.";
        }

        if(t != null && t.getEndDate() != null && t.getStartDate() != null && t.getStartDate().after(today) && t.getEndDate().after(today)) {
            return ok(tournament.render(t, players, usersFantasyPlayers, teamService.getTeams(), message, isFull));
        }else{
            return redirect("/PageNotFound");
        }
    }
}
