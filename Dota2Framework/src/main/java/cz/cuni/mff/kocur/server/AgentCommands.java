package cz.cuni.mff.kocur.server;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.base.Utils;

/**
 * Class with AgentCommands. These are implemented as static methods, that
 * create the appropriate AgentCommand. They are deserialized to JSON during
 * run-time and sent to the addon.
 * 
 * @author kocur
 *
 */
public final class AgentCommands {
	/**
	 * Class that represents attack command. Agent can attack a target given
	 * its id.
	 * 
	 * @author kocur
	 *
	 */
	public static class Attack extends AgentCommand {
		/**
		 * Target we want to attack.
		 */
		private int target = -1;

		/**
		 * Constructs the Attack command.
		 * 
		 * @param target
		 *            Id of the target we want to attack.
		 */
		public Attack(int target) {
			this.target = target;
			command = COMMAND_CODE.ATTACK;
		}

		/**
		 * Creates attack from string.
		 * 
		 * @param str
		 *            Arguments from which the Attack should be created. "target_id"
		 * @return Returns new Attack object.
		 */
		public static Attack createFromString(String str) {
			try {
				int t = Integer.parseInt(str);
				return new Attack(t);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		/**
		 * 
		 * @return Returns the id of the target.
		 */
		public int getTarget() {
			return target;
		}

		/**
		 * Sets the id of our target.
		 * 
		 * @param targetindex
		 *            Id of our target.
		 */
		public void setTarget(int target) {
			this.target = target;
		}

	}

	/**
	 * Class that represents the buy command. We can buy item by it's name.
	 * 
	 * @author kocur
	 *
	 */
	public static class Buy extends AgentCommand {
		/**
		 * Name of the item we want to buy.
		 */
		private String item;

		/**
		 * Simple constructor that sets the item name.
		 * 
		 * @param item
		 *            Name of item we want to buy.
		 */
		public Buy(String item) {
			this.item = item;
			command = COMMAND_CODE.BUY;
		}

		/**
		 * Creates buy from string.
		 * 
		 * @param str
		 *            Arguments from which the Buy should be created. "item_name"
		 * @return Returns new Buy object.
		 */
		public static Buy createFromString(String str) {
			return new Buy(str);
		}

		/**
		 * 
		 * @return Returns the name of the item we want to buy.
		 */
		public String getItem() {
			return item;
		}

		/**
		 * 
		 * @param item
		 *            Name of the item we want to buy.
		 */
		public void setItem(String item) {
			this.item = item;
		}

	}

	/**
	 * Class that specifies Cast command. That is a command that is called when hero
	 * wants to cast a spell at the enemy or given location. We can pass target id
	 * or coordinates.
	 * 
	 * @author kocur
	 *
	 */
	public static class Cast extends AgentCommand {
		private double x = -1;
		private double y = -1;
		private double z = -1;

		/**
		 * Id of the target we are casting the ability at.
		 */
		private int target = -1;

		/**
		 * Number of ability we want to cast. This is relative to the hero who is
		 * casting the ability.
		 */
		private int ability = -1;

		/**
		 * Constructor of Cast command.
		 * 
		 * @param ability
		 *            Index of ability we want to use.
		 * @param target
		 *            Id of target we are casting the ability at.
		 */
		public Cast(int ability, int target) {
			System.out.println("Creating ability cast:" + ability + " " + target);
			this.ability = ability;
			this.target = target;
			command = COMMAND_CODE.CAST;
		}

		/**
		 * Constructor for cast command that uses location to setup the params.
		 * 
		 * @param ability
		 *            Index of ability.
		 * @param l
		 *            Location containing either position or id of target.
		 */
		public Cast(int ability, Location l) {
			x = l.getX();
			y = l.getY();
			z = l.getZ();

			this.ability = ability;
			target = l.getEntid();

			command = COMMAND_CODE.CAST;
		}

		/**
		 * Constructor of Cast command. Uses ability index and location specified by
		 * [x,y,z].
		 * 
		 * @param ability
		 *            Index of ability we want to use.
		 * @param crds
		 * @param crds2
		 * @param crds3
		 */
		public Cast(int ability, Double x, Double y, Double z) {
			System.out.println("Creating ability cast:" + ability + " [" + x + ", " + y + ", " + z + "]");
			this.ability = ability;

			// Set the coordinates
			this.x = x;
			this.y = y;
			this.z = z;
			command = COMMAND_CODE.CAST;
		}

		/**
		 * Creates new Cast command for supplied string.
		 * 
		 * @param strs
		 *            Strigns from which it is composed. "entity_id target_id" or
		 *            "entity_id [x, y, z]"
		 * @return
		 */
		public static Cast createFromString(String... strs) {
			// I should have two fields - ability target or ability coordinates
			if (strs.length != 2)
				return null;

			int ability = 0;
			try {
				ability = Integer.parseInt(strs[0]);
			} catch (NumberFormatException e) {
				return null;
			}

			int target = 0;
			try {
				target = Integer.parseInt(strs[1]);
			} catch (NumberFormatException e) {
				target = -1;
			}

			if (target == -1) {
				Double[] crds = Utils.parseDoubleCoordiantes(strs[1]);
				if (crds.length < 3 || crds == null)
					return null;
				return new Cast(ability, crds[0], crds[1], crds[2]);
			} else {
				return new Cast(ability, target);
			}
		}

		/**
		 * 
		 * @return Returns the ability index.
		 */
		public int getAbility() {
			return ability;
		}

		/**
		 * 
		 * @return Returns the target id.
		 */
		public int getTarget() {
			return target;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}

		/**
		 * Sets the ability id.
		 * 
		 * @param ability
		 *            Id of the ability that should be casted.
		 */
		public void setAbility(int ability) {
			this.ability = ability;
		}

		/**
		 * Sets the target id.
		 * 
		 * @param string
		 *            Id of the target, that is the subject of casting.
		 */
		public void setTarget(int string) {
			this.target = string;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void setZ(double z) {
			this.z = z;
		}

		public String toString() {
			return "CAST";
		}

	}

	/**
	 * Levelup command. Will levelup ability on given index.
	 * 
	 * @author kocur
	 *
	 */
	public static class LevelUp extends AgentCommand {
		private int abilityIndex = -1;

		/**
		 * Constructor of LevelUp class.
		 * 
		 * @param abilityIndex
		 *            Index of ability that should be leveled up.
		 */
		public LevelUp(int abilityIndex) {
			this.abilityIndex = abilityIndex;
			command = COMMAND_CODE.LEVELUP;
		}

		/**
		 * Creates new LevelUp object from supplied string.
		 * 
		 * @param str
		 *            "ability_index"
		 * @return
		 */
		public static LevelUp createFromString(String str) {
			int index = 0;
			try {
				index = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				return null;
			}

			return new LevelUp(index);
		}

		/**
		 * Returns the index of ability that should be leveled up.
		 * 
		 * @return
		 */
		public int getAbilityIndex() {
			return abilityIndex;
		}

		/**
		 * Sets the index of ability that should be leveled up.
		 * 
		 * @param abilityIndex
		 *            Index of the ability.
		 */
		public void setAbilityIndex(int abilityIndex) {
			this.abilityIndex = abilityIndex;
		}

	}

	/**
	 * Move command. Will move to specific location or to NPC.
	 * 
	 * @author kocur
	 *
	 */
	public static class Move extends AgentCommand {
		private int target = -1;

		private double x = -1;
		private double y = -1;
		private double z = -1;

		/**
		 * Constructor of Move command. Takes x, y, z as location of where to move.
		 * 
		 * @param crds
		 * @param crds2
		 * @param crds3
		 */
		public Move(Double crds, Double crds2, Double crds3) {
			command = COMMAND_CODE.MOVE;
			this.x = crds;
			this.y = crds2;
			this.z = crds3;
		}

		/**
		 * Constructor of move command.
		 * 
		 * @param target
		 *            Id of target we want to move to.
		 */
		public Move(int target) {
			command = COMMAND_CODE.MOVE;
			this.setTarget(target);

		}

		/**
		 * Constructs move command from given location.
		 * 
		 * @param l
		 *            Location, where should the hero move. (given by id or position)
		 */
		public Move(Location l) {
			this.x = l.getX();
			this.y = l.getY();
			this.z = l.getZ();

			this.target = l.getEntid();

			command = COMMAND_CODE.MOVE;
		}

		/**
		 * Creates new Move command for supplied string.
		 * 
		 * @param str
		 *            String from which the move command is created. "target_id" or
		 *            "[x,y,z]"
		 * @return Returns new Move object.
		 */
		public static Move createFromString(String str) {
			int target = 0;
			try {
				target = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				target = -1;
			}

			if (target == -1) {
				Double[] crds = Utils.parseDoubleCoordiantes(str);
				if (crds.length < 3 || crds == null)
					return null;
				return new Move(crds[0], crds[1], crds[2]);
			} else {
				return new Move(target);
			}
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void setZ(double z) {
			this.z = z;
		}

		/**
		 * 
		 * @return Returns the id of target we are moving to.
		 */
		public int getTarget() {
			return target;
		}

		/**
		 * Sets the target we are moving to.
		 * 
		 * @param target
		 *            Id of target we want to follow.
		 */
		public void setTarget(int target) {
			this.target = target;
		}

	}

	/**
	 * Empty command. The agent does nothing.
	 * 
	 * @author kocur
	 *
	 */
	public static class Noop extends AgentCommand {

		public Noop() {
			command = COMMAND_CODE.NOOP;
		}
	}

	/**
	 * Grab all command. Takes all items from stash to inventory.
	 * 
	 * @author kocur
	 *
	 */
	public static class GrabAll extends AgentCommand {
		public GrabAll() {
			command = COMMAND_CODE.GRAB_ALL;
		}
	}

	/**
	 * Sell command. Sells item from specified slot.
	 * 
	 * @author kocur
	 *
	 */
	public static class Sell extends AgentCommand {

		/**
		 * Inventory slot that holds the item we want to sell.
		 */
		private int slot = -1;

		/**
		 * Constructor of Sell command.
		 * 
		 * @param slot
		 *            Id of slot where the item we want to sell lies.
		 */
		public Sell(int slot) {
			this.slot = slot;
			command = COMMAND_CODE.SELL;
		}

		/**
		 * Creates sell from supplied string.
		 * 
		 * @param str
		 *            "slot_index"
		 * @return New Sell object.
		 */
		public static Sell createFromString(String str) {
			int slot = 0;
			try {
				slot = Integer.parseInt(str);
				return new Sell(slot);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		/**
		 * 
		 * @return Returns the number of slot with item we want to sell.
		 */
		public int getSlot() {
			return slot;
		}

		public void setSlot(int slot) {
			this.slot = slot;
		}

	}

	/**
	 * UseItem command. Uses item in given slot at target or location.
	 * 
	 * @author kocur
	 *
	 */
	public static class UseItem extends AgentCommand {
		/**
		 * Slot where the item is stored.
		 */
		private int slot = -1;

		/**
		 * We use the spell on given target.
		 */
		private int target = -1;

		private double x = -1;
		private double y = -1;
		private double z = -1;

		/**
		 * 
		 * @param slot
		 *            Index of slot with item we want to use.
		 */
		public UseItem(int slot) {
			this.slot = slot;
			command = COMMAND_CODE.USE_ITEM;
		}

		/**
		 * 
		 * @param slot
		 *            Index of slot with item we want to use.
		 * @param target
		 *            Id of target we are using this item on.
		 */
		public UseItem(int slot, int target) {
			this.slot = slot;
			this.target = target;
			command = COMMAND_CODE.USE_ITEM;
		}

		/**
		 * Takes location specified by x, y, z and slot index.
		 * 
		 * @param slot
		 *            Index of the item we want to use at the given location.
		 * @param crds
		 * @param crds2
		 * @param crds3
		 */
		public UseItem(int slot, Double crds, Double crds2, Double crds3) {
			this.slot = slot;

			this.x = crds;
			this.y = crds2;
			this.z = crds3;

			command = COMMAND_CODE.USE_ITEM;
		}

		/**
		 * Creates the UseItem command from supplied string array.
		 * 
		 * @param strs
		 *            Array with fields like: "slot_index", "slot_index target_id" or
		 *            "slot_index [x, y, z]"
		 * @return
		 */
		public static UseItem createFromString(String... strs) {
			if (strs.length == 0)
				return null;

			int slot = 0;
			try {
				slot = Integer.parseInt(strs[0]);

			} catch (NumberFormatException e) {
				return null;
			}

			if (strs.length == 1) {
				return new UseItem(slot);
			}

			int target = 0;
			try {
				target = Integer.parseInt(strs[1]);
			} catch (NumberFormatException e) {
				target = -1;
			}

			if (target == -1) {
				Double[] crds = Utils.parseDoubleCoordiantes(strs[1]);
				if (crds.length < 3 || crds == null)
					return null;
				return new UseItem(slot, crds[0], crds[1], crds[2]);
			} else {
				return new UseItem(slot, target);
			}
		}

		/**
		 * Returns the index of slot with used item.
		 * 
		 * @return
		 */
		public int getSlot() {
			return slot;
		}

		/**
		 * Sets the index of slot that contains the item that will be used.
		 * 
		 * @param slot
		 *            Index of slot.
		 */
		public void setSlot(int slot) {
			this.slot = slot;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		/**
		 * 
		 * @return Returns the id of target that we are using this item on.
		 */
		public int getTarget() {
			return target;
		}

		/**
		 * Sets the id of target we will be using the item at.
		 * 
		 * @param target
		 *            Id of our target.
		 */
		public void setTarget(int target) {
			this.target = target;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getZ() {
			return z;
		}

		public void setZ(double z) {
			this.z = z;
		}
	}

	/**
	 * Command to pickup a rune with given id.
	 * 
	 * @author kocur
	 *
	 */
	public static class PickupRune extends AgentCommand {
		/**
		 * Id of the rune we want to pick up.
		 */
		private int target = -1;

		/**
		 * 
		 * @param target
		 *            Id of rune we want to pickup.
		 */
		public PickupRune(int target) {
			this.target = target;
			command = COMMAND_CODE.PICKUP_RUNE;

		}

		/**
		 * Creates new PickupRune from supplied string.
		 * 
		 * @param str
		 *            "rune_id"
		 * @return
		 */
		public static PickupRune createFromString(String str) {
			int target = 0;
			try {
				target = Integer.parseInt(str);
				return new PickupRune(target);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		/**
		 * 
		 * @return Returns the id of the rune we want to pickup.
		 */
		public int getTarget() {
			return target;
		}

		/**
		 * 
		 * @param target
		 *            Sets the id of the rune we want to pickup.
		 */
		public void setTarget(int target) {
			this.target = target;
		}
	}
}
