package ti4.commands.game;

import net.dv8tion.jda.api.entities.User;
import ti4.helpers.Constants;
import ti4.map.Map;

public class Join extends JoinLeave {

    public Join() {
        super(Constants.JOIN, "Join map as player");
    }

    @Override
    protected String getResponseMessage(Map map, User user) {
        return "Joined map: " + map.getName() + " successful";
    }

    @Override
    protected void action(Map map, User user) {
        map.addPlayer(user.getId(), user.getName());
    }
}