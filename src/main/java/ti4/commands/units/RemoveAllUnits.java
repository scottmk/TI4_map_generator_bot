package ti4.commands.units;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ti4.helpers.Constants;
import ti4.map.Tile;
import ti4.map.UnitHolder;

public class RemoveAllUnits extends AddRemoveUnits {

    @Override
    protected void unitParsingForTile(SlashCommandInteractionEvent event, String color, Tile tile) {
        tile.removeAllUnits(color);
        for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
            addPlanetToPlayArea(event, tile, unitHolder.getName());
        }
    }

    @Override
    protected void unitAction(SlashCommandInteractionEvent event, Tile tile, int count, String planetName, String unitID, String color) {
        //No need for this action
    }

    @Override
    public String getActionID() {
        return Constants.REMOVE_ALL_UNITS;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void registerCommands(CommandListUpdateAction commands) {
        // Moderation commands with required options
        commands.addCommands(
                Commands.slash(getActionID(), "Remove units from map")
                        .addOptions(new OptionData(OptionType.STRING, Constants.TILE_NAME, "System/Tile name")
                                .setRequired(true))
                        .addOptions(new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color for unit")
                                .setAutoComplete(true))
        );
    }

    @Override
    protected String getActionDescription() {
        return "";
    }
}
