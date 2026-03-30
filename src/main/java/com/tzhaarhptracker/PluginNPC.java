package com.tzhaarhptracker;

import com.tzhaarhptracker.info.ChunkUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;

@Slf4j
@Data
@AllArgsConstructor
@Getter
@Setter
public abstract class PluginNPC
{
	private NPC npc;
	private int hp;
	private boolean dead = false;
	private int maxHp;
	private int spawnTick;
	private int deathTick;
	private boolean healed = false;
	private int queuedDamage = 0;
	private int chunkOrder = -1;
	private int lastChunk;

	public PluginNPC(NPC npc, int currentHp, int maxHp, int tick)
	{
		this.npc = npc;
		this.hp = currentHp;
		this.spawnTick = tick;
		this.maxHp = maxHp;
		this.lastChunk = getChunkId();
	}

	public void addHp(int hp)
	{
		this.hp += hp;
	}

	public void removeHp(int hp)
	{
		this.hp -= hp;
	}

	public int getChunkId()
	{
		return ChunkUtils.getChunkForNpc(this.npc);
	}
}

