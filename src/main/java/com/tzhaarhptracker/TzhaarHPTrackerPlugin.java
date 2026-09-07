/*
 * Copyright (c) 2023, Buchus <http://github.com/MoreBuchus>
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import com.tzhaarhptracker.info.ColosseumHP;
import com.tzhaarhptracker.info.TzhaarHP;
import com.tzhaarhptracker.info.VenatorSolver;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.tzhaarhptracker.info.DamageHandler;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
@PluginDescriptor(
	name = "Tzhaar/Colo HP Tracker",
	description = "Marks Tzhaar and Colo NPCs and shows their current HP remaining",
	tags = {"inferno", "fight", "cave", "tzhaar", "jad", "zuk", "hp", "tracking", "dead", "npc", "indicator", "colosseum"}
)
public class TzhaarHPTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TzhaarHPTrackerConfig config;

	@Inject
	private NPCManager npcManager;

	@Inject
	private TzhaarHPTrackerOverlay overlay;

	@Inject
	private ReminderOverlay reminderOverlay;

	@Inject
	private ClientThread clientThread;

	@Inject
	private NpcUtil npcUtil;

	@Inject
	private Hooks hooks;

	@Inject
	private DamageHandler handleDamage;

	@Getter
	private InfoHandler[] infoHandlers = null;

	@Inject
	private EventBus eventBus;

	private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(
		MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION, MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION,
		MenuAction.NPC_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.ITEM_USE_ON_NPC
	);

	private static final Collection<Integer> allowedBanks = Set.of(7316, 9808, 9552, 10063, 10064, 10065);

	@Getter
	private final ArrayList<PluginNPC> npcs = new ArrayList<>();

	private final Map<Integer, Integer> chunkIdToOrder = new HashMap<Integer, Integer>();

	@Getter
	private final ArrayList<PluginNPC> hiddenNPCs = new ArrayList<>();

	@Getter
	private static final Set<String> FIGHT_CAVE_NPC = ImmutableSet.of(
		"tz-kih", "tz-kek", "tok-xil", "yt-mejkot", "ket-zek", "yt-hurkot", "tztok-jad"
	);

	@Getter
	private static final Set<String> INFERNO_NPC = ImmutableSet.of(
		"jal-nib", "jal-mejrah", "jal-ak", "jal-akrek-xil", "jal-akrek-mej", "jal-akrek-ket", "jal-imkot", "jal-xil", "jal-zek",
		"jaltok-jad", "yt-hurkot", "tzkal-zuk", "jal-mejjak", "<col=00ffff>rocky support</col>"
	);

	@Getter
	private static final Set<String> COLOSSEUM_NPC = ImmutableSet.of(
		"fremennik warband archer", "fremennik warband seer", "fremennik warband berserker", "serpent shaman", "jaguar warrior",
		"javelin colossus", "manticore", "shockwave colossus", "minotaur"
	);

	@Getter
	private static final Set<String> EXCLUDED_NPC = ImmutableSet.of(
		"yt-hurkot", "tztok-jad", "jaltok-jad", "jal-mejjak", "tzkal-zuk"
	);

	@Getter
	private static final Set<String> REVIVABLE_NPC = ImmutableSet.of(
		"jal-mejrah", "jal-ak", "jal-imkot", "jal-xil", "jal-zek"
	);

	private static final int FIGHT_CAVES_REGION = 9551;
	private static final int INFERNO_REGION = 9043;
	private static final int JAD_CHALLENGE_VAR = 11878; // 0 = out, 1 = in
	private static final int COLOSSEUM_REGION = 7216;

	@Getter
	private String spellbookType = "";

	private boolean waveStarted = false;
	private int waveStartTick = -1;
	@Getter
	private final Map<String, Integer> currentWave = new HashMap<>();

	private static final Pattern WAVE_START_PATTERN = Pattern.compile(".*Wave: (\\d+).*");
	private static final String TZHAAR_WAVE_COMPLETE = "Wave completed!";
	private static final Pattern COLO_WAVE_COMPLETE_PATTERN = Pattern.compile(".*Wave .* completed.*");
	private static final String ZUK_KC_MESSAGE = "Your TzKal-Zuk kill count is:";
	private static final String JAD_KC_MESSAGE = "Your TzTok-Jad kill count is:";
	private static final String SOL_KC_MESSAGE = "Your Sol Heredit kill count is:";
	private static final String DEATH_MESSAGE = "You have been defeated!";

	@Getter
	Font font;

	private long lastTickNS = 0;
	@Getter
	private int lastTickDurMS = 0;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Getter
	final private List<Integer> venatorBounceOrder = new ArrayList<>();


	@Provides
	TzhaarHPTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TzhaarHPTrackerConfig.class);
	}

	protected void startUp() throws Exception
	{
		npcs.clear();
		hiddenNPCs.clear();
		currentWave.clear();
		chunkIdToOrder.clear();
		venatorBounceOrder.clear();
		loadFont();
		overlayManager.add(overlay);
		overlayManager.add(reminderOverlay);
		hooks.registerRenderableDrawListener(drawListener);

		if (infoHandlers == null)
		{
			infoHandlers = new InfoHandler[]{handleDamage};

			for (InfoHandler info : infoHandlers)
			{
				info.init();
			}
		}

		for (InfoHandler info : infoHandlers)
		{
			info.load();
			eventBus.register(info);
		}

		clientThread.invokeLater(this::loadExistingNpcs);
	}

	private void loadExistingNpcs()
	{
		if (client.getGameState() != GameState.LOGGED_IN
			|| !isInAllowedCaves()
			|| !npcs.isEmpty())
		{
			return;
		}

		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			String name = npc.getName();
			if (name == null)
			{
				continue;
			}

			name = name.toLowerCase();
			if (!INFERNO_NPC.contains(name)
				&& !FIGHT_CAVE_NPC.contains(name)
				&& !COLOSSEUM_NPC.contains(name))
			{
				continue;
			}

			try
			{
				if ((INFERNO_NPC.contains(name)
					|| FIGHT_CAVE_NPC.contains(name))
					&& TzhaarHP.getNPC(npc.getId()) != null)
				{
					if (isTracked(npc))
					{
						continue;
					}

					int maxHp = TzhaarHP.getMaxHP(npc.getId()) != 0 ? TzhaarHP.getMaxHP(npc.getId()) : npcManager.getHealth(npc.getId());
					if (maxHp != 0)
					{
						int currentHp = estimateCurrentHp(npc, maxHp);
						TzhaarNPC newNPC = new TzhaarNPC(npc, currentHp, maxHp, client.getTickCount());
						//Set healed to true -> use ratio + scale to estimate NPCs HP who spawned before plugin startup
						newNPC.setHealed(true);
						npcs.add(newNPC);
					}
				}
				else if (COLOSSEUM_NPC.contains(name) && ColosseumHP.getNPC(npc.getId()) != null)
				{
					if (isTracked(npc))
					{
						continue;
					}

					int maxHp = ColosseumHP.getMaxHP(npc.getId()) != 0 ? ColosseumHP.getMaxHP(npc.getId()) : npcManager.getHealth(npc.getId());
					if (maxHp != 0)
					{
						int currentHp = estimateCurrentHp(npc, maxHp);
						ColosseumNPC newNPC = new ColosseumNPC(npc, currentHp, maxHp, client.getTickCount());
						//Set healed to true -> use ratio + scale to estimate NPCs HP who spawned before plugin startup
						newNPC.setHealed(true);
						npcs.add(newNPC);
						insertNpcToChunk(newNPC);
					}
				}
			}
			catch (NullPointerException ignored)
			{
			}
		}
	}

	private boolean isTracked(NPC npc)
	{
		return npcs.stream().anyMatch(n -> n.getNpc().getIndex() == npc.getIndex());
	}

	private int estimateCurrentHp(NPC npc, int maxHp)
	{
		int ratio = npc.getHealthRatio();
		int scale = npc.getHealthScale();
		if (ratio <= 0)
		{
			return maxHp;
		}

		int minHealth = 1;
		int maxHealth;
		if (scale > 1)
		{
			if (ratio > 1)
			{
				minHealth = (maxHp * (ratio - 1) + scale - 2) / (scale - 1);
			}
			maxHealth = (maxHp * ratio - 1) / (scale - 1);
			if (maxHealth > maxHp)
			{
				maxHealth = maxHp;
			}
		}
		else
		{
			maxHealth = maxHp;
		}

		return (minHealth + maxHealth + 1) / 2;
	}

	protected void shutDown() throws Exception
	{
		npcs.clear();
		hiddenNPCs.clear();
		currentWave.clear();
		chunkIdToOrder.clear();
		venatorBounceOrder.clear();
		overlayManager.remove(overlay);
		overlayManager.remove(reminderOverlay);
		hooks.unregisterRenderableDrawListener(drawListener);

		for (InfoHandler info : infoHandlers)
		{
			eventBus.unregister(info);
			info.unload();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned e)
	{
		if (isInAllowedCaves())
		{
			NPC npc = e.getNpc();
			if (isTracked(npc))
			{
				return;
			}

			int tick = client.getTickCount();

			if (npc.getName() != null
				&& (INFERNO_NPC.contains(npc.getName().toLowerCase())
				|| FIGHT_CAVE_NPC.contains(npc.getName().toLowerCase())))
			{
				try
				{
					if (TzhaarHP.getNPC(npc.getId()) != null)
					{
						int hp = TzhaarHP.getMaxHP(npc.getId()) != 0 ? TzhaarHP.getMaxHP(npc.getId()) : npcManager.getHealth(npc.getId());
						if (hp != 0)
						{
							int currentHp = hp;
							//Do not get half HP for Zuk sets
							if (isInInferno() && waveStarted && waveStartTick != -1 && tick > waveStartTick && REVIVABLE_NPC.contains(npc.getName().toLowerCase())
								&& currentWave.containsKey("inferno") && currentWave.get("inferno") != 69)
							{
								currentHp = TzhaarHP.getRespawnedHP(npc.getId()) != 0 ? TzhaarHP.getRespawnedHP(npc.getId()) : (int) Math.ceil((double) hp / 2);
							}
							npcs.add(new TzhaarNPC(npc, currentHp, hp, tick));
						}
					}
				}
				catch (NullPointerException ignored)
				{
				}
			}
			else if (npc.getName() != null
				&& (COLOSSEUM_NPC.contains(npc.getName().toLowerCase())))
			{
				try
				{
					if (ColosseumHP.getNPC(npc.getId()) != null)
					{
						int hp = ColosseumHP.getMaxHP(npc.getId()) != 0 ? ColosseumHP.getMaxHP(npc.getId()) : npcManager.getHealth(npc.getId());
						if (hp != 0)
						{
							PluginNPC newNPC = new ColosseumNPC(npc, hp, hp, tick);
							npcs.add(newNPC);
							insertNpcToChunk(newNPC);
						}
					}
				}
				catch (NullPointerException ignored)
				{
				}
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned e)
	{
		if (venatorBounceOrder.contains(e.getNpc().getIndex()))
		{
			venatorBounceOrder.clear();
		}
		npcs.removeIf(n -> n.getNpc().getIndex() == e.getNpc().getIndex());
		hiddenNPCs.removeIf(h -> h.getNpc().getIndex() == e.getNpc().getIndex());
	}

	@Subscribe
	public void onChatMessage(ChatMessage e)
	{
		if (e.getType() == ChatMessageType.GAMEMESSAGE && isInAllowedCaves())
		{
			final String message = Text.removeTags(e.getMessage());
			if (WAVE_START_PATTERN.matcher(message).matches())
			{
				String cave = isInInferno() ? "inferno" : isInFightCaves() ? "fc" : "colosseum";
				String wave = message.split(": ")[1];
				currentWave.put(cave, Integer.parseInt(wave));

				waveStarted = true;
				waveStartTick = client.getTickCount();
			}
			else if (TZHAAR_WAVE_COMPLETE.equals(message) || COLO_WAVE_COMPLETE_PATTERN.matcher(message).matches())
			{
				waveStarted = false;
				waveStartTick = -1;
			}
			else if (message.startsWith(JAD_KC_MESSAGE)
				|| message.startsWith(ZUK_KC_MESSAGE)
				|| message.startsWith(SOL_KC_MESSAGE)
				|| message.equals(DEATH_MESSAGE))
			{
				waveStarted = false;
				waveStartTick = -1;
				currentWave.clear();
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e)
	{
		final MenuEntry menuEntry = e.getMenuEntry();
		String option = Text.removeTags(e.getOption()).toLowerCase();
		String target = Text.removeTags(e.getTarget()).toLowerCase();

		if (config.recolorMenu() && isInAllowedCaves())
		{
			NPC npc = client.getTopLevelWorldView().npcs().byIndex(e.getIdentifier());
			for (PluginNPC n : npcs)
			{
				if (npc != null && npc.getName() != null && n.getNpc() == npc && !EXCLUDED_NPC.contains(npc.getName().toLowerCase()))
				{
					Color color = null;
					if (npcUtil.isDying(n.getNpc()) || n.isDead())
					{
						color = config.highlightDeadColor();
					}

					if (color == null && (!npcUtil.isDying(npc) || !n.isDead()))
					{
						color = config.highlightAliveColor();
					}

					if (config.dynamicColor() != TzhaarHPTrackerConfig.DynamicColor.OFF)
					{
						color = getDynamicColor(n, true);
					}

					if (color != null)
					{
						final String tzhaar = ColorUtil.prependColorTag(Text.removeTags(e.getTarget()), color);
						menuEntry.setTarget(tzhaar);
					}
				}
			}
		}

		if (isInAllowedBanks() && (config.spellbookWarning() == TzhaarHPTrackerConfig.spellbookWarningMode.REMOVE
			|| config.spellbookWarning() == TzhaarHPTrackerConfig.spellbookWarningMode.BOTH))
		{
			if ((option.contains("jump-in") && target.contains("the inferno"))  // Inferno
				|| (option.contains("enter") && target.contains("cave entrance"))) // fight caves, i'm ignoring colo because you don't waste anything but time by going in and back out, the others you lose task
			{
				if (!config.spellbookCheck().contains(TzhaarHPTrackerConfig.spellbook.NORMAL) && spellbookType.equals("NORMAL"))
				{
					client.getMenu().setMenuEntries(Arrays.copyOf(client.getMenu().getMenuEntries(), client.getMenu().getMenuEntries().length - 1));
				}
				else if (!config.spellbookCheck().contains(TzhaarHPTrackerConfig.spellbook.ANCIENT) && spellbookType.equals("ANCIENT"))
				{
					client.getMenu().setMenuEntries(Arrays.copyOf(client.getMenu().getMenuEntries(), client.getMenu().getMenuEntries().length - 1));
				}
				else if (!config.spellbookCheck().contains(TzhaarHPTrackerConfig.spellbook.LUNAR) && spellbookType.equals("LUNAR"))
				{
					client.getMenu().setMenuEntries(Arrays.copyOf(client.getMenu().getMenuEntries(), client.getMenu().getMenuEntries().length - 1));
				}
				else if (!config.spellbookCheck().contains(TzhaarHPTrackerConfig.spellbook.ARCEUUS) && spellbookType.equals("ARCEUUS"))
				{
					client.getMenu().setMenuEntries(Arrays.copyOf(client.getMenu().getMenuEntries(), client.getMenu().getMenuEntries().length - 1));
				}
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() != GameState.LOGGED_IN && e.getGameState() != GameState.LOADING)
		{
			npcs.clear();
			hiddenNPCs.clear();
			clearVenatorState();
			chunkIdToOrder.clear();
		}
		else
		{
			if (!isInAllowedCaves())
			{
				if (!currentWave.isEmpty())
				{
					currentWave.clear();
				}

				if (!npcs.isEmpty())
				{
					npcs.clear();
				}
				clearVenatorState();
				chunkIdToOrder.clear();

				if (!hiddenNPCs.isEmpty())
				{
					hiddenNPCs.clear();
				}
			}
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged e)
	{
		if (e.getGroup().equals(config.GROUP))
		{
			switch (e.getKey())
			{
				case "overlayFontType":
				case "overlayFontName":
				case "overlayFontSize":
				case "overlayFontWeight":
					loadFont();
					break;
			}
		}
	}

	public Color getDynamicColor(PluginNPC n, boolean line)
	{
		if (n.getHp() <= 0)
		{
			return line ? config.highlightDeadColor() : config.fillDeadColor();
		}

		double healthRatio = Math.min(1.0, (double) n.getHp() / n.getMaxHp());
		return ColorUtil.colorLerp(line ? config.highlightDeadColor() : config.fillDeadColor(), line ? config.highlightAliveColor() : config.fillAliveColor(), healthRatio);
	}

	public boolean shouldShowHighlight(PluginNPC npc)
	{
		return getHighlightMode(npc.getNpc().getId()).shouldShow(npc.isDead());
	}

	private TzhaarHPTrackerConfig.NpcHighlightMode getHighlightMode(int npcId)
	{
		TzhaarHP tzhaarNpc = TzhaarHP.getNPC(npcId);
		if (tzhaarNpc != null)
		{
			switch (tzhaarNpc)
			{
				case FC_BAT:
					return config.highlightFcBat();
				case FC_BIG_BLOB:
					return config.highlightFcBigBlob();
				case FC_BLOBLETS:
					return config.highlightFcBloblets();
				case FC_RANGE:
					return config.highlightFcRanger();
				case FC_MELEE:
					return config.highlightFcMelee();
				case FC_MAGE:
					return config.highlightFcMage();
				case TZTOK_JAD:
					return config.highlightFcJad();
				case FC_JAD_HEALER:
					return config.highlightFcJadHealer();
				case NIBBLER:
					return config.highlightInfernoNibbler();
				case BAT:
					return config.highlightInfernoBat();
				case BLOB:
					return config.highlightInfernoBlob();
				case BLOBLETS:
					return config.highlightInfernoBloblets();
				case MELEE:
					return config.highlightInfernoMelee();
				case RANGE:
					return config.highlightInfernoRanger();
				case MAGE:
					return config.highlightInfernoMage();
				case INFERNO_JAD:
					return config.highlightInfernoJad();
				case TZKAL_ZUK:
					return config.highlightInfernoZuk();
				case ZUK_HEALER:
					return config.highlightInfernoZukHealer();
				case INFERNO_JAD_HEALERS:
					return config.highlightInfernoJadHealer();
				case PILLAR:
					return TzhaarHPTrackerConfig.NpcHighlightMode.BOTH;
			}
		}

		ColosseumHP colosseumNpc = ColosseumHP.getNPC(npcId);
		if (colosseumNpc != null)
		{
			switch (colosseumNpc)
			{
				case _FREMENNIK_WARBAND_ARCHER:
				case _FREMENNIK_WARBAND_SEER:
				case _FREMENNIK_WARBAND_BERSERKER:
					return config.highlightFremenniks();
				case _SERPENT_SHAMAN:
					return config.highlightSerpentShaman();
				case _JAGUAR_WARRIOR:
					return config.highlightJaguarWarrior();
				case _JAVELIN_COLOSSUS:
					return config.highlightJavelinColossus();
				case _MANTICORE:
					return config.highlightManticore();
				case _SHOCKWAVE_COLOSSUS:
					return config.highlightShockwaveColossus();
				case _MINOTAUR:
					return config.highlightMinotaur();
			}
		}

		return TzhaarHPTrackerConfig.NpcHighlightMode.BOTH;
	}

	public void loadFont()
	{
		switch (config.overlayFontType())
		{
			case SMALL:
				font = FontManager.getRunescapeSmallFont();
				break;
			case REGULAR:
				font = FontManager.getRunescapeFont();
				break;
			case BOLD:
				font = FontManager.getRunescapeBoldFont();
				break;
			case CUSTOM:
				if (!config.overlayFontName().equals(""))
				{
					font = new Font(config.overlayFontName(), config.overlayFontWeight().getWeight(), config.overlayFontSize());
				}
				break;
		}
	}

	public Collection<GameObject> getCaveEntrances()
	{
		Collection<GameObject> objects = new ArrayList<>();
		WorldView wv = client.getTopLevelWorldView();
		Tile[][] tiles = wv.getScene().getTiles()[wv.getPlane()];
		for (Tile[] tile : tiles)
		{
			for (Tile t : tile)
			{
				if (t != null)
				{
					GameObject[] gameObjects = t.getGameObjects();
					if (gameObjects != null)
					{
						objects.addAll(Arrays.stream(gameObjects).filter(o -> o != null && (o.getId() == 11833 || o.getId() == 30352 || o.getId() == 50751)
							&& o.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()) <= 30 && !objects.contains(o)).collect(Collectors.toList()));
					}
				}
			}
		}
		return objects;
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (isInAllowedCaves())
		{
			long time = System.nanoTime();
			lastTickDurMS = (int) ((time - lastTickNS) / 1000000L);
			lastTickNS = time;

			//Clear Hidden NPCs if lag spike over the set amount
			if (lastTickDurMS >= config.lagProtection())
			{
				hiddenNPCs.clear();
			}
			updateChunks();
			if (!config.showVenatorBounce() || !isVenatorEquipped())
			{
				clearVenatorState();
			}
			else if (client.getLocalPlayer() != null && client.getLocalPlayer().getInteracting() instanceof NPC)
			{
				NPC interacting = (NPC) client.getLocalPlayer().getInteracting();
				this.npcs.stream().filter(npc -> npc.getNpc() == interacting).findFirst().ifPresentOrElse(this::updateHoveredNpc, this::clearVenatorState);
			}
			else
			{
				clearVenatorState();
			}
		}

		if (isInAllowedBanks())
		{
			int spellbook = client.getVarbitValue(4070);
			if (spellbook == 0)
			{
				spellbookType = "NORMAL";
			}
			else if (spellbook == 1 && !spellbookType.equals("ANCIENT"))
			{
				spellbookType = "ANCIENT";
			}
			else if (spellbook == 2 && !spellbookType.equals("LUNAR"))
			{
				spellbookType = "LUNAR";
			}
			else if (spellbook == 3 && !spellbookType.equals("ARCEUUS"))
			{
				spellbookType = "ARCEUUS";
			}
		}
	}

	private void updateChunks() {
		npcs.sort(Comparator.comparing(a -> a.getNpc().getIndex()));
		for (PluginNPC npc : npcs) {
			insertNpcToChunk(npc);
		}
	}

	private void insertNpcToChunk(PluginNPC npc) {
		int currentChunkId = npc.getChunkId();
		if (npc.getChunkOrder() < 0 || currentChunkId != npc.getLastChunk()) {
			int nextOrder = this.chunkIdToOrder.getOrDefault(currentChunkId, 0) + 1;
			npc.setChunkOrder(nextOrder);
			npc.setLastChunk(currentChunkId);
			this.chunkIdToOrder.put(currentChunkId, nextOrder);
		}
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof NPC)
		{
			NPC renderedNpc = (NPC) renderable;

			for (PluginNPC npc : hiddenNPCs)
			{
				if (npc.getNpc().getIndex() == renderedNpc.getIndex() && npc.isDead())
				{
					boolean normalHide = config.hideDead()
						&& !EXCLUDED_NPC.contains(Objects.requireNonNull(npc.getNpc().getName()).toLowerCase());

					boolean forceHideNpc = shouldForceHide(npc);

					if (normalHide || forceHideNpc)
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	public boolean isInAllowedCaves()
	{
		return isInFightCaves() || isInInferno() || isInColosseum();
	}

	public boolean isInFightCaves()
	{
		return ArrayUtils.contains(client.getTopLevelWorldView().getMapRegions(), FIGHT_CAVES_REGION);
	}

	public boolean isInInferno()
	{
		return ArrayUtils.contains(client.getTopLevelWorldView().getMapRegions(), INFERNO_REGION);
	}

	public boolean isInColosseum()
	{
		return ArrayUtils.contains(client.getTopLevelWorldView().getMapRegions(), COLOSSEUM_REGION);
	}

	//10063-10065 is inferno bank region
	//9808 is fight caves bank area, 9552 is fight pits area
	public boolean isInAllowedBanks()
	{
		int[] regions = client.getTopLevelWorldView().getMapRegions();
		return regions != null && regions.length > 0 && Arrays.stream(regions).anyMatch(allowedBanks::contains);
	}

	public boolean isInJadChallenge()
	{
		return ArrayUtils.contains(client.getTopLevelWorldView().getMapRegions(), INFERNO_REGION) && client.getVarbitValue(JAD_CHALLENGE_VAR) == 1;
	}

	@Subscribe
	private void onInteractingChanged(InteractingChanged e)
	{
		if (e.getSource() != client.getLocalPlayer())
		{
			return;
		}

		clearVenatorState();
		if (!isInAllowedCaves() || !config.showVenatorBounce() || !isVenatorEquipped() || !(e.getTarget() instanceof NPC))
		{
			return;
		}

		this.npcs.stream().filter(npc -> npc.getNpc() == e.getTarget()).findFirst().ifPresent(this::updateHoveredNpc);
	}

	private void updateHoveredNpc(PluginNPC npc) {
		this.venatorBounceOrder.clear();
		if (!config.showVenatorBounce() || !isVenatorEquipped()) return;
		this.venatorBounceOrder.addAll(VenatorSolver.solve(npc, this.npcs).stream().map(t -> t.getNpc().getIndex()).collect(Collectors.toList()));
	}

	private boolean isVenatorEquipped()
	{
		if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null)
		{
			return false;
		}

		Integer weapon = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
		return weapon != null && (weapon == net.runelite.api.gameval.ItemID.VENATOR_BOW
			|| weapon == net.runelite.api.gameval.ItemID.VENATOR_BOW_ORNAMENT);
	}

	private void clearVenatorState()
	{
		venatorBounceOrder.clear();
	}

	private boolean shouldForceHide(PluginNPC npc)
	{
		int id = npc.getNpc().getId();
		if (isInInferno() && config.hideInfernoNibblers() && id == NpcID.INFERNO_NIBBLER)
		{
			return true;
		}

		if (!isInColosseum() || !config.hideWarbands())
		{
			return false;
		}

		return id == net.runelite.api.gameval.NpcID.COLOSSEUM_WARBANDER_RANGED_FEMALE
			|| id == net.runelite.api.gameval.NpcID.COLOSSEUM_WARBANDER_MAGE_MALE
			|| id == NpcID.COLOSSEUM_WARBANDER_MELEE_MALE;
	}

	public void debugPrint(String msg) {
		if (config.debug()) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
		}
	}

}
