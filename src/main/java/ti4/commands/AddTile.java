package ti4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ti4.ResourceHelper;
import ti4.message.MessageHelper;

import java.io.File;
import java.util.StringTokenizer;

public class AddTile implements Command {
    @Override
    public boolean accept(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        return msg.getContentRaw().startsWith(":add_tile");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if (CreateMap.generateMapInstance == null)
        {
            MessageHelper.replyToMessage(event.getMessage(),"Start map creation with :create_map");
        }
        else {
            Message msg = event.getMessage();
            String message = msg.getContentRaw();
            StringTokenizer tokenizer = new StringTokenizer(message, " ");
            if (tokenizer.countTokens() == 3)
            {
                String command = tokenizer.nextToken();//Left command parsing as we need to remove it for code
                String planetTileName = tokenizer.nextToken();
                String position = tokenizer.nextToken();

                String tilePath = ResourceHelper.getInstance().getTileFile(planetTileName);
                if (tilePath == null)
                {
                    MessageHelper.replyToMessage(msg, "Could not find tile");
                    return;
                }
                File planet = new File(tilePath);
                CreateMap.generateMapInstance.addTile(planet, position);
                File file = CreateMap.generateMapInstance.saveImage();
                MessageHelper.replyToMessage(event.getMessage(), file);
            }
        }
    }
}
