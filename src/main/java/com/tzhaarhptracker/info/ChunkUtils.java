package com.tzhaarhptracker.info;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ChunkUtils {
	private static Pair<Integer, Integer> chunkIdToXY(int chunkId) {
		return Pair.of(chunkId & ((1 << 16) - 1), (chunkId >> 16));
	}

	private static int XYtoChunkId(int x, int y) {
		return x | (y << 16);
	}

	public static int getChunkForNpc(NPC npc) {
		return XYtoChunkId(npc.getWorldLocation().getX() >> 3, npc.getWorldLocation().getY() >> 3);
	}

	public static int getChunkForLocation(WorldPoint p)
	{
		return XYtoChunkId(p.getX() >> 3, p.getY() >> 3);
	}

	public static List<Integer> getChunkPriorityGridOrder(int centerGridId) {
		// NE->SW, column major order
		Pair<Integer, Integer> centerXY = chunkIdToXY(centerGridId);
		int x = centerXY.getLeft();
		int y = centerXY.getRight();
		return List.of(
			XYtoChunkId(x + 1, y + 1),
			XYtoChunkId(x + 1, y),
			XYtoChunkId(x + 1, y - 1),
			XYtoChunkId(x, y + 1),
			XYtoChunkId(x, y),
			XYtoChunkId(x, y - 1),
			XYtoChunkId(x - 1, y + 1),
			XYtoChunkId(x - 1, y),
			XYtoChunkId(x - 1, y - 1)
		);
	}
}
