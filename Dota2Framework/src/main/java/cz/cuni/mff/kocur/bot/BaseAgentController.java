package cz.cuni.mff.kocur.Bot;

import cz.cuni.mff.kocur.Bot.BotCommands.*;
import cz.cuni.mff.kocur.World.ChatEvent;

public abstract class BaseBot implements Bot {
    protected Buy BUY = new Buy();
    protected Sell SELL = new Sell();
    protected Select SELECT = new Select();
    protected UseItem USE_ITEM = new UseItem();
    protected Move MOVE = new Move();
    protected Noop NOOP = new Noop();
    protected Attack ATTACK = new Attack();
    protected LevelUp LEVELUP = new LevelUp();
    protected Cast CAST = new Cast();

    @Override
    public void onChat( ChatEvent e ) {
        System.out.println( e.getPlayer() + " said: " + e.getText() );

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

}
