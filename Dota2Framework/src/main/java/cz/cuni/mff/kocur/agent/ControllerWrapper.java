package cz.cuni.mff.kocur.agent;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.configuration.HeroConfiguration.TYPE;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.console.ConsoleManager;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.server.AgentCommands.Attack;
import cz.cuni.mff.kocur.server.AgentCommands.Buy;
import cz.cuni.mff.kocur.server.AgentCommands.Cast;
import cz.cuni.mff.kocur.server.AgentCommands.LevelUp;
import cz.cuni.mff.kocur.server.AgentCommands.Move;
import cz.cuni.mff.kocur.server.AgentCommands.PickupRune;
import cz.cuni.mff.kocur.server.AgentCommands.Sell;
import cz.cuni.mff.kocur.server.AgentCommands.UseItem;
import cz.cuni.mff.kocur.server.RequestHandler;
import cz.cuni.mff.kocur.server.Response;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.WorldUpdate;

/**
 * Wraps around the Agent's Controller to create some more functionality and
 * control. Can be used by console to e.g. turn the agent off. This class also
 * registers custom logging appender.
 * 
 * @author kocur
 *
 */
public class ControllerWrapper implements Controllable {
	/**
	 * Logger for ControllerWrapper class.
	 */
	private static final Logger logger = LogManager.getLogger(ControllerWrapper.class.getName());

	/**
	 * The controller.
	 */
	private AgentController controller;

	/**
	 * Is the wrapper all set?
	 */
	private boolean set = false;

	/**
	 * BotCommand we can schedule to test the agent in some way.
	 */
	private AgentCommand scheduledBotCommand = null;

	/**
	 * We can stop and start the agent.
	 */
	private boolean working = true;

	/**
	 * Name of the agent.
	 */
	private String name;

	/**
	 * Id of this agent.
	 */
	private Integer id = 0;

	/**
	 * Initializes CW with name and Agent's Controller.
	 * @param name Name of the agent.
	 * @param bot The controller.
	 */
	public ControllerWrapper(String name, AgentController bot) {
		this.controller = bot;
		this.name = name;
		this.id = 0;

		// Register this wrapper to ConsoleManager
		ConsoleManager.register(this);
	}

	/**
	 * 
	 * @return Returns id of this agent.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the hero id.
	 * 
	 * @param id
	 *            In-game id of the hero.
	 */
	public void initializeBotsId(int id) {
		// Set the id
		this.id = id;

		// Set the id for the bot. This will change its context too.
		controller.setId(id);

		set = true;
	}

	/**
	 * 
	 * @return Returns the agent's controller.
	 */
	public AgentController getController() {
		return controller;
	}

	/**
	 * Sets the agent controller.
	 * @param cnt New controller.
	 */
	public void setController(AgentController cnt) {
		this.controller = cnt;
	}

	/**
	 * Handles the levelup.
	 * @return Returns a Response to levelup request.
	 */
	private Response levelup() {
		int index = controller.getLevelUpIndex();

		AgentCommand l;
		if (index == -1)
			l = new AgentCommands.Noop();
		else
			l = new LevelUp(index);

		return new Response(l);
	}

	/**
	 * Resets the agent.
	 * @return Returns a response. 
	 */
	private Response reset() {
		controller.reset();

		return new Response();
	}

	/**
	 * Updates the agent.
	 * @return Returns new response.
	 */
	private Response update() {
		Response res = new Response();

		AgentCommand c = null;
		if (scheduledBotCommand != null) {
			c = scheduledBotCommand;
			scheduledBotCommand = null;
		} else if (working) {
			c = controller.update();
		}

		// If null we set c to Noop(), so that the user doesn't need to do it everywhere
		// (null is better)
		if (c == null)
			c = new AgentCommands.Noop();

		res.setObject(c);

		return res;
	}

	/**
	 * Handles the incoming request.
	 * @param code CODE of the request == method's name.
	 * @return Returns a new response.
	 */
	public Response handle(RequestHandler.CODE code) {
		// logger.debug("Parsing command inside bot with id:" + bot.getId());

		Response res = new Response();
		switch (code) {
		case RESET:
			res = reset();
			break;
		case LEVELUP:
			res = levelup();
			break;
		case UPDATE:
			res = update();
			break;
		default:
			logger.error("Code not found!!");

			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.setMsg("Method not found.");
			break;
		}

		return res;
	}

	/**
	 * Reacts on chat event.
	 * @param e Chat event.
	 */
	public void onChat(ChatEvent e) {
		// logger.debug("Handling chat event inside bot wrapper with id:" +
		// bot.getId());

		controller.onChat(e);
	}

	/**
	 * 
	 * @return Returns true if this CW is set.
	 */
	public boolean isSet() {
		return set;
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		String commandName = cmd.peekField();
		return handleCommand(commandName.toLowerCase(), cmd);
	}

	/**
	 * Handles the command. Can be used to schedule some bot's action or to stop the bot.
	 * @param commandName Name of the command.
	 * @param cmd ConsoleCommand.
	 * @return Returns response for this command.
	 */
	private CommandResponse handleCommand(String commandName, ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();

		switch (commandName) {
		case "move":
			cmd.getField();
			scheduledBotCommand = Move.createFromString(cmd.getField());
			break;
		case "attack":
			cmd.getField();
			scheduledBotCommand = Attack.createFromString(cmd.getField());
			break;
		case "cast":
			cmd.getField();
			scheduledBotCommand = Cast.createFromString(cmd.getFields());
			break;
		case "buy":
			cmd.getField();
			scheduledBotCommand = Buy.createFromString(cmd.getField());
			break;
		case "sell":
			cmd.getField();
			scheduledBotCommand = Sell.createFromString(cmd.getField());
			break;
		case "use_item":
			cmd.getField();
			scheduledBotCommand = UseItem.createFromString(cmd.getFields());
			break;
		case "noop":
			break;
		case "pickup_rune":
			cmd.getField();
			scheduledBotCommand = PickupRune.createFromString(cmd.getField());
			break;
		case "grab_all":
			cmd.getField();
			scheduledBotCommand = new AgentCommands.GrabAll();
			break;
		case "stop":
			working = false;
			break;
		case "resume":
			working = true;
			break;
		case "teamContext":
			res.setMsg(controller.getTeamContext().toString());
			break;
		case "agentContext":
			res.setMsg(controller.getContext().toString());
			break;
		default:
			res = controller.command(cmd);
			break;
		}

		return res;
	}

	@Override
	public String getHelp() {
		IndentationStringBuilder b = new IndentationStringBuilder();
		b.append(getControllableName() + ":");
		b.indent();

		b.appendLines("move [x,y,z]",
				"move target_id",
				"attack target_id",
				"cast entity_id target_id",
				"cast entity_id [x, y, z]",
				"buy item_name",
				"sell slot_index", 
				"use_item slot_index",
				"use_item slot_index target_id",
				"use_item slot_index [x,y,z]",
				"noop", 
				"pickup_rune rune_id", 
				"grab_all", 
				"stop -> Stops the agent control.",
				"resume -> Resumes the agent control.",
				"teamContext -> Prints the team context.",
				"agentContext -> Prints the agent's context."
				);

		b.appendLines(controller.getHelp());

		return b.toString();
	}

	@Override
	public String getControllableName() {
		return name;
	}

	/**
	 * 
	 * @return Returns true if this controller is AI.
	 */
	public boolean isArtificialPlayer() {
		return controller.getConfiguration().getType() == TYPE.AI;
	}

	/**
	 * 
	 * @return Returns name of the controller.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a new name for this controller.
	 * @param name New name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return Returns string representation of this controllers lane.
	 */
	public String getLane() {
		return controller.getConfiguration().getConfigValue("lane");
	}

	/**
	 * Updates this agent's context.
	 * 
	 * @param u
	 *            World containing all the entities that we update from.
	 */
	public void updateAgentContext(WorldUpdate u) {
		AgentController bc = this.getController();
		bc.updateHero((Hero) u.getEntity(id));
		bc.getContext().update(u);
	}

	/**
	 * Called when framework was stopped and controllers should be removed.
	 */
	public void destroy() {
		ConsoleManager.unregister(this);

		// Unregister the appender (stop the logging)
		String loggerClassPath = controller.getConfiguration().getConfigValue("logger_classpath");
		unregisterAppender(loggerClassPath);

		controller.destroy();
	}

	/**
	 * Removes appender with given className.
	 * 
	 * @param className
	 *            Class name of the appender that should be removed.
	 */
	protected void unregisterAppender(String className) {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();
		config.removeAppender("QA_" + className);
	}

}
