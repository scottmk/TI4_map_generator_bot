package ti4.commands.agenda;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.map.Map;
import ti4.map.*;
import ti4.message.MessageHelper;

import java.util.*;

public class ListVoteCount extends AgendaSubcommandData {
    public ListVoteCount() {
        super(Constants.VOTE_COUNT, "List Vote count for agenda");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Map map = getActiveMap();
        turnOrder(event, map);
    }

    public static void turnOrder(SlashCommandInteractionEvent event, Map map) {
        StringBuilder msg = new StringBuilder();
        int i = 1;
        List<Player> orderList = new ArrayList<>();
        orderList.addAll(map.getPlayers().values().stream().toList());
        String speakerName = map.getSpeaker();
        Optional<Player> optSpeaker = orderList.stream().filter(player -> player.getUserID().equals(speakerName))
                .findFirst();

        if (optSpeaker.isPresent()) {
            int rotationDistance = orderList.size() - orderList.indexOf(optSpeaker.get()) - 1;
            Collections.rotate(orderList, rotationDistance);
        }

        for (Player player : orderList) {
            List<String> planets = new ArrayList<>(player.getPlanets());
            planets.removeAll(player.getExhaustedPlanets());
            String userName = player.getUserName();
            String color = player.getColor();

            String text = "";
            text += Helper.getFactionIconFromDiscord(player.getFaction());
            text += " " + userName;
            if (color != null) {
                text += " (" + color + ")";
            }
            HashMap<String, UnitHolder> planetsInfo = map.getPlanetsInfo();

            boolean bloodPactPn = false;
            boolean hasXxchaAlliance = false;
            int influenceCount = 0;
            if ("xxcha".equals(player.getFaction())) {
                Leader leader = player.getLeader(Constants.COMMANDER);
                if (leader != null && !leader.isLocked()) {
                    influenceCount += planets.size();
                }
                leader = player.getLeader(Constants.HERO);
                if (leader != null && !leader.isLocked()) {
                    int influenceCountFromPlanetsRes = planets.stream().map(planetsInfo::get).filter(Objects::nonNull)
                            .map(planet -> (Planet) planet).mapToInt(Planet::getResources).sum();
                    influenceCount += influenceCountFromPlanetsRes;
                }
            } else if (!player.getPromissoryNotesInPlayArea().isEmpty()) {
                for (String pn : player.getPromissoryNotesInPlayArea()) {
                    String promissoryNoteOwner = Mapper.getPromissoryNoteOwner(pn);
                    for (Player player_ : map.getPlayers().values()) {
                        if (player_ != player) {
                            String playerColor = player_.getColor();
                            String playerFaction = player_.getFaction();
                            boolean isCorrectPlayer = playerColor != null && playerColor.equals(promissoryNoteOwner) ||
                                    playerFaction.equals(promissoryNoteOwner);
                            if ("xxcha".equals(playerFaction) && pn.endsWith("_an")) {
                                if (isCorrectPlayer) {
                                    Leader leader = player_.getLeader(Constants.COMMANDER);
                                    if (leader != null && !leader.isLocked()) {
                                        influenceCount += planets.size();
                                        hasXxchaAlliance = true;
                                        break;
                                    }
                                }
                            } else if ("empyrean".equals(playerFaction) && "blood_pact".equals(pn)) {
                                if (isCorrectPlayer) {
                                    bloodPactPn = true;
                                }
                            }
                        }
                    }
                }
            }


            int influenceCountFromPlanets = planets.stream().map(planetsInfo::get).filter(Objects::nonNull)
                    .map(planet -> (Planet) planet).mapToInt(Planet::getInfluence).sum();
            influenceCount += influenceCountFromPlanets;

            if (player.getFaction().equals("nekro") && !hasXxchaAlliance) {
                text += " NOT VOTING.: ** 0";
            } else {
                text += " vote Count: **" + influenceCount;
                if ("argent".equals(player.getFaction())) {
                    text += "(+" + map.getPlayers().keySet().size() + ")";
                }
                if (bloodPactPn) {
                    text += "(Blood Pact +4 votes)";
                }
                //Predictive Intelligence
                if (player.getTechs().contains("pi") && !player.getExhaustedTechs().contains("pi")) {
                    text += "(+3 votes for Predictive Intelligence)";
                }
            }

            if (player.getUserID().equals(speakerName)) {
                text += " <:Speakertoken:965363466821050389> ";
            }
            text += "**";
            msg.append(i).append(". ").append(text).append("\n");
            i++;
        }
        MessageHelper.sendMessageToChannel(event.getChannel(), msg.toString());
    }
}
