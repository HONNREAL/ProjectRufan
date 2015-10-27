package is.rufan.winnerselection.process;

import is.rufan.fantasyplayer.domain.FantasyPlayer;
import is.rufan.fantasyplayer.service.FantasyPlayerService;
import is.rufan.fantasypoints.domain.FantasyPoints;
import is.rufan.fantasypoints.service.FantasyPointService;
import is.rufan.fantasyteam.domain.FantasyTeam;
import is.rufan.fantasyteam.service.FantasyTeamService;
import is.rufan.player.domain.Player;
import is.rufan.player.service.PlayerService;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentGameService;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.service.UserService;
import is.ruframework.process.RuAbstractProcess;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by Keli on 27 / 10 / 15.
 */
public class WinnerSelectionProcess extends RuAbstractProcess {
    protected ApplicationContext tctx = new FileSystemXmlApplicationContext("/conf/tournamentapp.xml");
    protected ApplicationContext pctx = new FileSystemXmlApplicationContext("/conf/playerapp.xml");
    protected ApplicationContext fctx = new FileSystemXmlApplicationContext("/conf/fantasyplayerapp.xml");
    protected ApplicationContext uctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    protected ApplicationContext tgctx = new FileSystemXmlApplicationContext("/conf/tournamentgameapp.xml");
    protected ApplicationContext gctx = new FileSystemXmlApplicationContext("/conf/gameapp.xml");
    protected ApplicationContext teamctx = new FileSystemXmlApplicationContext("/conf/teamapp.xml");
    protected ApplicationContext ftctx = new FileSystemXmlApplicationContext("/conf/fantasyteamapp.xml");
    protected ApplicationContext fpctx = new FileSystemXmlApplicationContext("/conf/fantasypointapp.xml");


    private PlayerService playerService;
    private TournamentService tournamentService;
    private FantasyPlayerService fantasyPlayerService;
    private UserService userService;
    private TournamentGameService tournamentGameService;
    private GameService gameService;
    private TeamService teamService;
    private FantasyTeamService fantasyTeamService;
    private FantasyPointService fantasyPointService;


    @Override
    public void beforeProcess() {
        playerService = (PlayerService) pctx.getBean("playerService");
        tournamentService = (TournamentService) tctx.getBean("tournamentService");
        fantasyPlayerService = (FantasyPlayerService) fctx.getBean("fantasyPlayerService");
        userService = (UserService) uctx.getBean("userService");
        tournamentGameService = (TournamentGameService) tgctx.getBean("tournamentGameService");
        gameService = (GameService) gctx.getBean("gameService");
        teamService = (TeamService) teamctx.getBean("teamService");
        fantasyTeamService = (FantasyTeamService) ftctx.getBean("fantasyTeamService");
        fantasyPointService = (FantasyPointService) fpctx.getBean("fantasypointService");

    }

    @Override
    public void afterProcess() {
        super.afterProcess();
    }

    @Override
    public void startProcess() {
        Date today = new Date();
        for(Tournament tournament : tournamentService.getTournaments()){
            if(tournament.getStatus() && tournament.getEndDate() != null && tournament.getEndDate().before(today)){
                List<FantasyTeam> fantasyTeams = fantasyTeamService.getFantasyTeamsByTournamentId(tournament.getId());
                for(FantasyTeam fantasyTeam : fantasyTeams){
                    List<FantasyPlayer> fantasyPlayers = fantasyPlayerService.getFantasyPlayersWithUserId(fantasyTeam.getUserid());
                    for(FantasyPlayer fantasyPlayer : fantasyPlayers){
                        if(fantasyPlayer.getTournamentId() == tournament.getId()) {
                            fantasyTeam.addPoints(fantasyPointService.getPoints(fantasyPlayer.getPlayerId()));
                        }
                    }
                    fantasyTeam.setStatus("WINNER_SELECTED");
                    fantasyTeamService.updateFantasyTeam(fantasyTeam);
                }
                tournament.setStatus(false);
                tournamentService.updateTournament(tournament.getId(), tournament);
            }
        }
    }
}
