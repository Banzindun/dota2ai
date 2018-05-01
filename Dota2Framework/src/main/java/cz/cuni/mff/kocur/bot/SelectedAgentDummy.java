package cz.cuni.mff.kocur.bot;

/**
 * Simple class that server to deserialize botsselected body that is passed by the game.
 * 
 * After the selection of hero the game responds with id of hero that was created by issuing server/botsselected request.
 * Id of hero and its index (that was send in select) must be deserialized after the data arrive.
 *  
 * @author Banzindun
 *
 */
public class SelectedAgentDummy {
	/**
	 * Index in the array of agents. This is a way how to id a agent before in-game id was issued. See {@link cz.cuni.mff.kocur.server.ServerManager}
	 */
	private String name; 
	
	/**
	 * Id of the agent. This should be in-game id of the hero object (that has corresponding index).
	 */
	private int entid;

	/**
	 * 
	 * @return Returns the name of the agent.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a new name for the agent.
	 * @param name New name of the agent.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
		/**
	 * Returns the id. {@link #entid}
	 * @return id 
	 */
	public int getEntid() {
		return entid;
	}

	/**
	 * Sets the id. {@link #entid}
	 * @param id new id
	 */
	public void setEntid(int id) {
		this.entid = id;
	}	
}
