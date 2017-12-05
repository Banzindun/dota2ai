package cz.cuni.mff.kocur.Server;

import cz.cuni.mff.kocur.Bot.Bot.Command;
import cz.cuni.mff.kocur.World.*;

@FunctionalInterface
public interface FrameListener {
    Command update( World w );
}
