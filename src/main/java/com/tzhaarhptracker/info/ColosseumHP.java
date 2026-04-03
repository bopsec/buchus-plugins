package com.tzhaarhptracker.info;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import static net.runelite.api.NpcID.FREMENNIK_WARBAND_SEER;
import net.runelite.api.gameval.NpcID;


public enum ColosseumHP
{
	_FREMENNIK_WARBAND_ARCHER(50, 100, 1, NpcID.COLOSSEUM_WARBANDER_RANGED_FEMALE),
	_FREMENNIK_WARBAND_SEER(50, 100, 1, NpcID.COLOSSEUM_WARBANDER_MAGE_MALE),
	_FREMENNIK_WARBAND_BERSERKER(48, 100, 1, NpcID.COLOSSEUM_WARBANDER_MELEE_MALE),
	_SERPENT_SHAMAN(125, 100, 1, NpcID.COLOSSEUM_STANDARD_MAGER),
	_JAGUAR_WARRIOR(125, 100, 1, NpcID.COLOSSEUM_JAGUAR_WARRIOR),
	_JAVELIN_COLOSSUS(220, 100, 1, NpcID.COLOSSEUM_JAVELIN_COLOSSUS),
	_MANTICORE(250, 100, 1, NpcID.COLOSSEUM_MANTICORE),
	_SHOCKWAVE_COLOSSUS(125, 100, 1, NpcID.COLOSSEUM_SHOCKWAVE_COLOSSUS),
	_MINOTAUR(225, 20, 5, NpcID.COLOSSEUM_MINOTAUR, NpcID.COLOSSEUM_MINOTAUR_ROUTEFIND);

	@Getter
	private final int maxHP;

	@Getter
	private final int regenInterval;

	@Getter
	private final int regenAmount;

	@Getter
	private final Set<Integer> ids;

	ColosseumHP(int maxHP, int regenInterval, int regenAmount, Integer... ids)
	{
		this.maxHP = maxHP;
		this.regenAmount = regenAmount;
		this.regenInterval = regenInterval;
		this.ids = Sets.newHashSet(ids);
	}

	public static ColosseumHP getNPC(int id)
	{
		for (ColosseumHP npc : values())
		{
			if (npc.ids.stream().anyMatch(i -> i == id))
			{
				return npc;
			}
		}
		return null;
	}

	public static int getMaxHP(int id)
	{
		ColosseumHP npc = getNPC(id);
		if (npc != null)
		{
			return npc.maxHP;
		}
		return 0;
	}
}
