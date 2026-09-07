/*
 * Copyright (c) 2023, Buchus <http://github.com/MoreBuchus>
 * Copyright (c) 2021, InfernoStats <http://github.com/InfernoStats>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tzhaarhptracker;

import com.google.common.collect.ImmutableSet;
import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

@ConfigGroup(TzhaarHPTrackerConfig.GROUP)
public interface TzhaarHPTrackerConfig extends Config
{
	String GROUP = "TzhaarHPTracker";

	//------------------------------------------------------------//
	// Highlight Settings
	//------------------------------------------------------------//
	@ConfigSection(
		name = "Highlight Settings",
		description = "Settings for highlighting",
		position = 1,
		closedByDefault = true
	)
	String highlightSection = "highlightSection";

	@ConfigItem(
		position = 1,
		keyName = "highlightStyle",
		name = "Highlight Style",
		description = "Picks the highlight style you want for selected NPCs",
		section = highlightSection
	)
	default Set<HighlightStyle> highlightStyle()
	{
		return Collections.emptySet();
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "highlightAliveColor",
		name = "Alive NPC Color",
		description = "Sets color of alive npc tile outline",
		section = highlightSection
	)
	default Color highlightAliveColor()
	{
		return new Color(0, 255, 48, 255);
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "fillAliveColor",
		name = "Alive NPC Fill",
		description = "Sets the fill color of alive npc hightlights",
		section = highlightSection
	)
	default Color fillAliveColor()
	{
		return new Color(0, 255, 48, 20);
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "highlightDeadColor",
		name = "Dead NPC Color",
		description = "Sets color of dead npc tile outline",
		section = highlightSection
	)
	default Color highlightDeadColor()
	{
		return new Color(224, 0, 0, 255);
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "fillDeadColor",
		name = "Dead NPC Fill",
		description = "Sets the fill color of dead npc hightlights",
		section = highlightSection
	)
	default Color fillDeadColor()
	{
		return new Color(255, 0, 0, 60);
	}

	@Range(min = 0, max = 50)
	@ConfigItem(
		position = 6,
		keyName = "highlightThiCC",
		name = "Highlight Width",
		description = "Sets the width of npc highlights",
		section = highlightSection
	)
	default double highlightThiCC()
	{
		return 2;
	}

	@ConfigItem(
		position = 7,
		keyName = "tileLines",
		name = "Tile Line Type",
		description = "Sets the tile outline to regular, dashed, or corners only",
		section = highlightSection
	)
	default lineType tileLines()
	{
		return lineType.REG;
	}

	@ConfigItem(
		position = 8,
		keyName = "antiAlias",
		name = "Anti-Aliasing",
		description = "Turns on anti-aliasing for the overlays. Makes them smoother.",
		section = highlightSection
	)
	default boolean antiAlias()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "showHp",
		name = "Show HP",
		description = "Show hp above npc heads",
		section = highlightSection
	)
	default HpLocation showHp()
	{
		return HpLocation.HP_BAR;
	}

	@ConfigItem(
		position = 10,
		keyName = "showPillarHp",
		name = "Show Pillar HP",
		description = "Show hp for Inferno pillars",
		section = highlightSection
	)
	default HpLocation showPillarHp()
	{
		return HpLocation.OFF;
	}

	@ConfigItem(
		position = 11,
		keyName = "dynamicHpColor",
		name = "Dynamic HP Color",
		description = "Changes the color of the HP dynamically",
		section = highlightSection
	)
	default DynamicColor dynamicColor()
	{
		return DynamicColor.OFF;
	}

	@ConfigItem(
		position = 12,
		keyName = "recolorMenu",
		name = "Recolor Menu",
		description = "Recolors the right click menus",
		section = highlightSection
	)
	default boolean recolorMenu()
	{
		return true;
	}

	@ConfigItem(
		position = 13,
		keyName = "hideDead",
		name = "Hide Dead NPCs",
		description = "Hides NPCs that are predicted to die",
		section = highlightSection
	)
	default boolean hideDead()
	{
		return false;
	}

	@Units(" ms")
	@ConfigItem(
		position = 14,
		keyName = "lagProtection",
		name = "Lag Protection",
		description = "Unhides hidden NPCs after a big lag spike",
		section = highlightSection
	)
	default int lagProtection()
	{
		return 1000;
	}

	@ConfigItem(
		position = 15,
		keyName = "showVenatorBounce",
		name = "Show Venator Bounce",
		description = "Show predicted targets for Venator Bounce",
		section = highlightSection
	)
	default boolean showVenatorBounce() {
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 16,
		keyName = "venatorBounceLineColor",
		name = "Venator bounce highlight color",
		description = "Sets the line color of venator npc highlights",
		section = highlightSection
	)
	default Color lineVenatorColor()
	{
		return new Color(255, 64, 255, 160);
	}
	@ConfigItem(
		position = 17,
		keyName = "hideWarbands",
		name = "Hide dead fremenniks",
		description = "Instantly hide Warbands on killing xp drop, overrides Hide Dead NPCs",
		section = highlightSection
	)
	default boolean hideWarbands() { return false; }
	@ConfigItem(
		position = 18,
		keyName = "hideInfernoNibblers",
		name = "Hide dead nibblers",
		description = "Instantly hide Inferno nibblers on killing xp drop, overrides Hide Dead NPCs",
		section = highlightSection
	)
	default boolean hideInfernoNibblers() { return false; }

	//------------------------------------------------------------//
	// NPC Highlight Settings
	//------------------------------------------------------------//
	@ConfigSection(
		name = "NPC Highlight Settings",
		description = "Choose which NPC types should be highlighted",
		position = 2,
		closedByDefault = true
	)
	String npcHighlightSection = "npcHighlightSection";

	@ConfigItem(
		position = 0,
		keyName = "highlightFcBat",
		name = "Fight Cave Bat",
		description = "Highlights for Tz-Kih",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcBat() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 1,
		keyName = "highlightFcBigBlob",
		name = "Fight Cave Big Blob",
		description = "Highlights for Tz-Kek",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcBigBlob() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 2,
		keyName = "highlightFcBloblets",
		name = "Fight Cave Bloblets",
		description = "Highlights for small Tz-Kek spawns",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcBloblets() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 3,
		keyName = "highlightFcRanger",
		name = "Fight Cave Ranger",
		description = "Highlights for Tok-Xil",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcRanger() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 4,
		keyName = "highlightFcMelee",
		name = "Fight Cave Melee",
		description = "Highlights for Yt-MejKot",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcMelee() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 5,
		keyName = "highlightFcMage",
		name = "Fight Cave Mage",
		description = "Highlights for Ket-Zek",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcMage() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 6,
		keyName = "highlightFcJad",
		name = "Fight Cave Jad",
		description = "Highlights for TzTok-Jad",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcJad() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 7,
		keyName = "highlightFcJadHealer",
		name = "Fight Cave Jad Healer",
		description = "Highlights for Yt-HurKot",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFcJadHealer() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 8,
		keyName = "highlightInfernoNibbler",
		name = "Inferno Nibbler",
		description = "Highlights for Jal-Nib",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoNibbler() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 9,
		keyName = "highlightInfernoBat",
		name = "Inferno Bat",
		description = "Highlights for Jal-MejRah",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoBat() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 10,
		keyName = "highlightInfernoBlob",
		name = "Inferno Blob",
		description = "Highlights for Jal-Ak",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoBlob() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 11,
		keyName = "highlightInfernoBloblets",
		name = "Inferno Bloblets",
		description = "Highlights for Jal-AkRek spawns",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoBloblets() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 12,
		keyName = "highlightInfernoMelee",
		name = "Inferno Melee",
		description = "Highlights for Jal-ImKot",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoMelee() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 13,
		keyName = "highlightInfernoRanger",
		name = "Inferno Ranger",
		description = "Highlights for Jal-Xil",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoRanger() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 14,
		keyName = "highlightInfernoMage",
		name = "Inferno Mage",
		description = "Highlights for Jal-Zek",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoMage() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 15,
		keyName = "highlightInfernoJad",
		name = "Inferno Jad",
		description = "Highlights for JalTok-Jad",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoJad() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 16,
		keyName = "highlightInfernoZuk",
		name = "Inferno Zuk",
		description = "Highlights for TzKal-Zuk",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoZuk() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 17,
		keyName = "highlightInfernoZukHealer",
		name = "Inferno Zuk Healer",
		description = "Highlights for Jal-MejJak",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoZukHealer() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 18,
		keyName = "highlightInfernoJadHealer",
		name = "Inferno Jad Healer",
		description = "Highlights for Yt-HurKot",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightInfernoJadHealer() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 19,
		keyName = "highlightFremenniks",
		name = "Fremenniks",
		description = "Highlights for all Fremennik warband NPCs",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightFremenniks() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 20,
		keyName = "highlightSerpentShaman",
		name = "Serpent Shaman",
		description = "Highlights for Serpent Shamans",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightSerpentShaman() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 21,
		keyName = "highlightJaguarWarrior",
		name = "Jaguar Warrior",
		description = "Highlights for Jaguar Warriors",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightJaguarWarrior() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 22,
		keyName = "highlightJavelinColossus",
		name = "Javelin Colossus",
		description = "Highlights for Javelin Colossi",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightJavelinColossus() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 23,
		keyName = "highlightManticore",
		name = "Manticore",
		description = "Highlights for Manticores",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightManticore() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 24,
		keyName = "highlightShockwaveColossus",
		name = "Shockwave Colossus",
		description = "Highlights for Shockwave Colossi",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightShockwaveColossus() { return NpcHighlightMode.BOTH; }

	@ConfigItem(
		position = 25,
		keyName = "highlightMinotaur",
		name = "Minotaur",
		description = "Highlights for Minotaurs",
		section = npcHighlightSection
	)
	default NpcHighlightMode highlightMinotaur() { return NpcHighlightMode.BOTH; }

	//------------------------------------------------------------//
	// Font Settings
	//------------------------------------------------------------//
	@ConfigSection(
		name = "Font Settings",
		description = "Settings for font",
		position = 3,
		closedByDefault = true
	)
	String fontSection = "fontSection";

	@ConfigItem(
		position = 0,
		keyName = "overlayFontType",
		name = "Font Type",
		description = "",
		section = fontSection
	)
	default FontType overlayFontType()
	{
		return FontType.BOLD;
	}

	@ConfigItem(
		position = 1,
		keyName = "overlayFontName",
		name = "Font Name",
		description = "Custom font override for overlays",
		section = fontSection
	)
	default String overlayFontName()
	{
		return "";
	}

	@ConfigItem(
		position = 2,
		keyName = "overlayFontSize",
		name = "Font Size",
		description = "",
		section = fontSection
	)
	default int overlayFontSize()
	{
		return 11;
	}

	@ConfigItem(
		position = 3,
		keyName = "overlayFontWeight",
		name = "Font Weight",
		description = "Sets the custom font weight for overlays",
		section = fontSection
	)
	default FontWeight overlayFontWeight()
	{
		return FontWeight.PLAIN;
	}

	@ConfigItem(
		position = 4,
		name = "Font Background",
		keyName = "fontBackground",
		description = "Background of the XP drop text",
		section = fontSection
	)
	default Background fontBackground()
	{
		return Background.SHADOW;
	}

	@Range(min = 0, max = 255)
	@ConfigItem(
		position = 5,
		keyName = "hpFontAlpha",
		name = "HP Font Alpha",
		description = "Sets the alpha for text overlays <br>0 will use the alpha of the Alive/Dead or Dynamic colors",
		section = fontSection
	)
	default int hpFontAlpha()
	{
		return 0;
	}

	@ConfigItem(
		position = 6,
		keyName = "hpTextColorMode",
		name = "HP Text Color Mode",
		description = "Select how HP text colors should be chosen",
		section = fontSection
	)
	default HpTextColorMode hpTextColorMode()
	{
		return HpTextColorMode.DEFAULT;
	}

	@Alpha
	@ConfigItem(
		position = 7,
		keyName = "hpTextColor",
		name = "HP Text Color",
		description = "Sets the HP text color when HP Text Color Mode is Single Color",
		section = fontSection
	)
	default Color hpTextColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		position = 8,
		keyName = "hpAliveTextColor",
		name = "Alive HP Text Color",
		description = "Sets the HP text color for alive NPCs when HP Text Color Mode is Alive/Dead",
		section = fontSection
	)
	default Color hpAliveTextColor()
	{
		return new Color(0, 255, 48, 255);
	}

	@Alpha
	@ConfigItem(
		position = 9,
		keyName = "hpDeadTextColor",
		name = "Dead HP Text Color",
		description = "Sets the HP text color for dead NPCs when HP Text Color Mode is Alive/Dead",
		section = fontSection
	)
	default Color hpDeadTextColor()
	{
		return new Color(224, 0, 0, 255);
	}

	//------------------------------------------------------------//
	// XP Settings
	//------------------------------------------------------------//
	@ConfigSection(
		name = "Xp Settings",
		description = "Settings for xp",
		position = 4,
		closedByDefault = true
	)
	String xpSection = "xpSection";

	@ConfigItem(
		position = 0,
		keyName = "xpMultiplier",
		name = "Xp multiplier",
		description = "The bonus xp multiplier (from season game mode for example) that should be factored when calculating the hit",
		section = xpSection
	)
	default double xpMultiplier()
	{
		return 1;
	}

	// For Raging Echoes League
	@ConfigItem(
		position = 1,
		keyName = "equilibriumRelic",
		name = "Equilibrium",
		description = "The bonus flat xp obtained from the Equilibrium relic in the Raging Echoes League that should be factored when calculating the hit",
		section = xpSection
	)
	default boolean equilibriumRelic() { return false; }

	// For Raging Echoes League
	@Range(min = 32, max = 2277)
	@ConfigItem(
			position = 2,
			keyName = "totalLevel",
			name = "Total Level",
			description = "Total level used to calculate equilibrium xp drops",
			section = xpSection
	)
	default int totalLevel() { return 2277; }

	//------------------------------------------------------------//
	// Reminder Settings
	//------------------------------------------------------------//
	@ConfigSection(
		name = "Reminder Settings",
		description = "Settings for reminders",
		position = 5,
		closedByDefault = true
	)
	String reminderSection = "reminderSection";

	@ConfigItem(
		position = 0,
		keyName = "spellbookWarning",
		name = "Spellbook Warning",
		description = "Warning if the local player is on the wrong spellbook " +
			"<br>Colors the entrances to inferno and fight caves the warning color if the player is on the wrong spellbook" +
			"<br>'Remove Enter' removes options to enter if the player is on the wrong spellbook",
		section = reminderSection
	)
	default spellbookWarningMode spellbookWarning()
	{
		return spellbookWarningMode.OFF;
	}

	@ConfigItem(
		position = 1,
		keyName = "spellbookCheck",
		name = "Allowed Spellbooks",
		description = "Spellbooks the player wishes to enter the inferno with.",
		section = reminderSection
	)
	default Set<spellbook> spellbookCheck()
	{
		return ImmutableSet.of(spellbook.NORMAL, spellbook.ANCIENT, spellbook.LUNAR, spellbook.ARCEUUS);
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "spellbookColor",
		name = "Warning Color",
		description = "The color to highlight cave entrances with on the wrong spellbook.",
		section = reminderSection
	)
	default Color spellbookColor()
	{
		return new Color(255, 0, 0, 50);
	}

	//
 	// Debug
	//
	@ConfigSection(
		name = "Debug",
		description = "Debug Settings",
		position = 99,
		closedByDefault = true
	)
	String debugSection = "debug";

	@ConfigItem(
		keyName = "debug",
		name = "Debug",
		description = "",
		position = 1,
		section = debugSection
	)
	default boolean debug() { return false; }

	//------------------------------------------------------------//
	// Highlight Enums
	//------------------------------------------------------------//
	@Getter
	@RequiredArgsConstructor
	enum HighlightStyle
	{
		TILE("Tile"),
		TRUE_TILE("True Tile"),
		SW_TILE("SW Tile"),
		SW_TRUE_TILE("SW True Tile"),
		HULL("Hull"),
		OUTLINE("Outline");

		@Getter
		private final String group;

		@Override
		public String toString()
		{
			return group;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum lineType
	{
		REG("Regular"),
		DASH("Dashed"),
		CORNER("Corners"),
		;

		@Getter
		private final String group;

		@Override
		public String toString()
		{
			return group;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum HpLocation
	{
		OFF("Off"),
		HP_BAR("Above HP Bar"),
		CENTER("Center of NPC"),
		FEET("Bottom of NPC");

		@Getter
		private final String group;

		@Override
		public String toString()
		{
			return group;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum DynamicColor
	{
		OFF("Off"),
		HIGHLIGHT("Highlight"),
		HP("Hitpoints"),
		BOTH("Both");

		@Getter
		private final String group;

		@Override
		public String toString()
		{
			return group;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum HpTextColorMode
	{
		DEFAULT("Default"),
		SINGLE("Single Color"),
		ALIVE_DEAD("Alive/Dead");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum NpcHighlightMode
	{
		BOTH("Both"),
		ALIVE("Alive"),
		DEAD("Dead"),
		NEITHER("Neither");

		private final String name;

		public boolean shouldShow(boolean dead)
		{
			return this == BOTH || (this == ALIVE && !dead) || (this == DEAD && dead);
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	//------------------------------------------------------------//
	// Font Enums
	//------------------------------------------------------------//
	@Getter
	@RequiredArgsConstructor
	enum FontType
	{
		SMALL("RS Small"),
		REGULAR("RS Regular"),
		BOLD("RS Bold"),
		CUSTOM("Custom");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	enum FontWeight
	{
		PLAIN(Font.PLAIN),
		BOLD(Font.BOLD),
		ITALIC(Font.ITALIC),
		BOLD_ITALIC(Font.BOLD | Font.ITALIC);

		@Getter
		private final int weight;

		FontWeight(int i)
		{
			weight = i;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum Background
	{
		OFF("Off"),
		SHADOW("Shadow"),
		OUTLINE("Outline"),
		;

		@Getter
		private final String group;

		@Override
		public String toString()
		{
			return group;
		}
	}

	//------------------------------------------------------------//
	// Reminder Enums
	//------------------------------------------------------------//
	@Getter
	@RequiredArgsConstructor
	enum spellbookWarningMode
	{
		OFF("Off"),
		OVERLAY("Overlay"),
		REMOVE("Remove Enter"),
		BOTH("Both");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum spellbook
	{
		NORMAL("Normal"),
		ANCIENT("Ancient"),
		LUNAR("Lunar"),
		ARCEUUS("Arceuus");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}
}
