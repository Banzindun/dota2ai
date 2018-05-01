package cz.cuni.mff.kocur.world;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;

public class Ability extends BaseEntity {
	public static final int DAMAGE_TYPE_NONE = 0;
    public static final int DAMAGE_TYPE_PHYSICAL = 1;
    public static final int DAMAGE_TYPE_MAGICAL = 2;
    public static final int DAMAGE_TYPE_PURE = 4;
    public static final int DAMAGE_TYPE_ALL = 7;
    public static final int DAMAGE_TYPE_HP_REMOVAL = 8;

    public static final int DOTA_ABILITY_BEHAVIOR_NONE = 0;
    public static final int DOTA_ABILITY_BEHAVIOR_HIDDEN = 1;
    public static final int DOTA_ABILITY_BEHAVIOR_PASSIVE = 2;
    public static final int DOTA_ABILITY_BEHAVIOR_NO_TARGET = 4;;
    public static final int DOTA_ABILITY_BEHAVIOR_UNIT_TARGET = 8;
    public static final int DOTA_ABILITY_BEHAVIOR_POINT = 16;
    public static final int DOTA_ABILITY_BEHAVIOR_AOE = 32;
    public static final int DOTA_ABILITY_BEHAVIOR_NOT_LEARNABLE = 64;
    public static final int DOTA_ABILITY_BEHAVIOR_CHANNELLED = 128;
    public static final int DOTA_ABILITY_BEHAVIOR_ITEM = 256;
    public static final int DOTA_ABILITY_BEHAVIOR_TOGGLE = 512;
    public static final int DOTA_ABILITY_BEHAVIOR_DIRECTIONAL = 1024;
    public static final int DOTA_ABILITY_BEHAVIOR_IMMEDIATE = 2048;
    public static final int DOTA_ABILITY_BEHAVIOR_AUTOCAST = 4096;
    public static final int DOTA_ABILITY_BEHAVIOR_OPTIONAL_UNIT_TARGET = 8192;
    public static final int DOTA_ABILITY_BEHAVIOR_OPTIONAL_POINT = 16384;
    public static final int DOTA_ABILITY_BEHAVIOR_OPTIONAL_NO_TARGET = 32768;
    public static final int DOTA_ABILITY_BEHAVIOR_AURA = 65536;
    public static final int DOTA_ABILITY_BEHAVIOR_ATTACK = 131072;
    public static final int DOTA_ABILITY_BEHAVIOR_DONT_RESUME_MOVEMENT = 262144;
    public static final int DOTA_ABILITY_BEHAVIOR_ROOT_DISABLES = 524288;
    public static final int DOTA_ABILITY_BEHAVIOR_UNRESTRICTED = 1048576;
    public static final int DOTA_ABILITY_BEHAVIOR_IGNORE_PSEUDO_QUEUE = 2097152;
    public static final int DOTA_ABILITY_BEHAVIOR_IGNORE_CHANNEL = 4194304;
    public static final int DOTA_ABILITY_BEHAVIOR_DONT_CANCEL_MOVEMENT = 8388608;
    public static final int DOTA_ABILITY_BEHAVIOR_DONT_ALERT_TARGET = 16777216;
    public static final int DOTA_ABILITY_BEHAVIOR_DONT_RESUME_ATTACK = 33554432;
    public static final int DOTA_ABILITY_BEHAVIOR_NORMAL_WHEN_STOLEN = 67108864;
    public static final int DOTA_ABILITY_BEHAVIOR_IGNORE_BACKSWING = 134217728;
    public static final int DOTA_ABILITY_BEHAVIOR_RUNE_TARGET = 268435456;
    public static final int DOTA_ABILITY_BEHAVIOR_DONT_CANCEL_CHANNEL = 536870912;
    public static final int DOTA_ABILITY_LAST_BEHAVIOR = 536870912;

    public static final int DOTA_UNIT_TARGET_TEAM_NONE = 0;
    public static final int DOTA_UNIT_TARGET_TEAM_FRIENDLY = 1;
    public static final int DOTA_UNIT_TARGET_TEAM_ENEMY = 2;
    public static final int DOTA_UNIT_TARGET_TEAM_BOTH = 3;
    public static final int DOTA_UNIT_TARGET_TEAM_CUSTOM = 4;

    public static final int DOTA_UNIT_TARGET_NONE = 0;
    public static final int DOTA_UNIT_TARGET_HERO = 1;
    public static final int DOTA_UNIT_TARGET_CREEP = 2;
    public static final int DOTA_UNIT_TARGET_BUILDING = 4;
    public static final int DOTA_UNIT_TARGET_MECHANICAL = 8;
    public static final int DOTA_UNIT_TARGET_COURIER = 16;
    public static final int DOTA_UNIT_TARGET_BASIC = 18;
    public static final int DOTA_UNIT_TARGET_OTHER = 32;
    public static final int DOTA_UNIT_TARGET_ALL = 63;
    public static final int DOTA_UNIT_TARGET_TREE = 64;
    public static final int DOTA_UNIT_TARGET_CUSTOM = 128;

    private int targetFlags;
    private int targetTeam;
    private int targetType;
    private int abilityType;
    private int abilityIndex;
    private int level;
    private int maxLevel;
    private int abilityDamage;
    private int abilityDamageType;
    private int behavior;
    private int castRange;
    
    private float cooldownTime;
    private float cooldownTimeRemaining;

    public int getAbilityDamage() {
        return abilityDamage;
    }

    public int getAbilityDamageType() {
        return abilityDamageType;
    }

    public int getAbilityIndex() {
        return abilityIndex;
    }

    public int getAbilityType() {
        return abilityType;
    }

    public int getBehavior() {
        return behavior;
    }

    public float getCooldownTime() {
        return cooldownTime;
    }

    public float getCooldownTimeRemaining() {
        return cooldownTimeRemaining;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTargetFlags() {
        return targetFlags;
    }

    public int getTargetTeam() {
        return targetTeam;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setAbilityDamage( int abilityDamage ) {
        this.abilityDamage = abilityDamage;
    }

    public void setAbilityDamageType( int abilityDamageType ) {
        this.abilityDamageType = abilityDamageType;
    }

    public void setAbilityIndex( int abilityIndex ) {
        this.abilityIndex = abilityIndex;
    }

    public void setAbilityType( int abilityType ) {
        this.abilityType = abilityType;
    }

    public void setBehavior( int behaviour ) {
        behavior = behaviour;
    }

    public void setCooldownTime( float cooldownTime ) {
        this.cooldownTime = cooldownTime;
    }

    public void setCooldownTimeRemaining( float cooldownTimeRemaining ) {
        this.cooldownTimeRemaining = cooldownTimeRemaining;
    }

    public void setLevel( int level ) {
        this.level = level;
    }

    public void setMaxLevel( int maxLevel ) {
        this.maxLevel = maxLevel;
    }

    public void setTargetFlags( int targetFlags ) {
        this.targetFlags = targetFlags;
    }

    public void setTargetTeam( int targetTeam ) {
        this.targetTeam = targetTeam;
    }

    public void setTargetType( int targetType ) {
        this.targetType = targetType;
    }
    
	public int getCastRange() {
		return castRange;
	}

	public void setCastRange(int castRange) {
		this.castRange = castRange;
	}

    @Override
    public String toString() {
    	IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine(name + " : " + abilityIndex);
		builder.indent();
		
		builder.appendLines(
				"targetFlags:" + targetFlags,
				"targetTeam:" + targetTeam,
				"targetType:" + targetType,
				"abilityType:" + abilityType,
				"abilityIndex:" + abilityIndex,
				"level:" + level,
				"maxLevel:" + maxLevel,
				"abilityDamage:" + abilityDamage,
				"abilityDamageType:" + abilityDamageType,
				"behavior:" + behavior,
				"cooldownTime:" + cooldownTime,
				"cooldownTimeRemaining:" + cooldownTimeRemaining,
				"origin: " + x + ", " + y + ", " + z,
				"name:" + name,
				"health: " + health,
				"maxHealth:" + maxHealth,
				"castRange" + castRange
				);
        
        return builder.toString();
    }



}
