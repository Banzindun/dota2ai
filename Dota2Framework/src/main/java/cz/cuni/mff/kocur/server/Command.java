package cz.cuni.mff.kocur.server;

/**
 * Interface that represents commands coming out from server or agent. 
 * @author kocur
 *
 */
public interface Command {
	/**
	 * Types of issuers. The issuer of this command can be Server or Agent
	 * @author kocur
	 *
	 */
	enum Issuer {
		SERVER,
		AGENT
	}

	/**
	 * 
	 * @return Returns a type of issuer that issued this command.
	 */
	Issuer getIssuer();
	
	/**
	 * Sets the type of issuer of this command.
	 * @param i New type of issuer.
	 */
	void setIssuer(Issuer i);
}
