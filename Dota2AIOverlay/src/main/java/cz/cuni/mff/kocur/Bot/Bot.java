package cz.cuni.mff.kocur.Bot;

import cz.cuni.mff.kocur.Bot.BotCommands.LevelUp;
import cz.cuni.mff.kocur.Bot.BotCommands.Select;
import cz.cuni.mff.kocur.Server.FrameListener;
import cz.cuni.mff.kocur.World.ChatEvent;

public interface Bot extends FrameListener {
    public interface Command {
        enum COMMAND_CODE {
            NOOP, MOVE, ATTACK, CAST, BUY, SELL, USE_ITEM, SELECT
        }

        COMMAND_CODE getCommand();
    }

    LevelUp levelUp();

    void onChat( ChatEvent e );

    void reset();

    Select select();

}
