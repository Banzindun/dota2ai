package cz.cuni.mff.kocur.world;

/**
 * Class that represents a ChatEvent. This class stores the chat's message, id
 * of player that sent the message and if the message is meant only for the
 * team.
 * 
 * @author kocur
 *
 */
public class ChatEvent {
	/**
	 * Is this message meant only for the player's team.
	 */
	private boolean teamOnly;

	/**
	 * Id of the player.
	 */
	private int player;

	/**
	 * Text of the chat event.
	 */
	private String text;

	/**
	 * 
	 * @return Returns id of player that sent this message.
	 */
	public int getPlayer() {
		return player;
	}

	/**
	 * 
	 * @return Returns this message's text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @return Returns true, if this is a team message.
	 */
	public boolean isTeamOnly() {
		return teamOnly;
	}

	/**
	 * Sets player id.
	 * 
	 * @param player
	 *            Id of the player.
	 */
	public void setPlayer(int player) {
		this.player = player;
	}

	/**
	 * 
	 * @param teamOnly
	 *            True, if this message is meant only for the team of the player
	 *            that sent the command.
	 */
	public void setTeamOnly(boolean teamOnly) {
		this.teamOnly = teamOnly;
	}

	/**
	 * 
	 * @param text
	 *            New text of this event.
	 */
	public void setText(String text) {
		this.text = text;
	}

}
