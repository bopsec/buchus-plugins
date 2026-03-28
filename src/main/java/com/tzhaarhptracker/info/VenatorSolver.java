package com.tzhaarhptracker.info;

import com.tzhaarhptracker.PluginNPC;
import lombok.AllArgsConstructor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.stream.Collectors;

// ported from my los tool
public class VenatorSolver {
	private static final int RANGE = 2;

	@AllArgsConstructor
	static class ScanTiles {
		WorldPoint sw;
		WorldPoint centerSw;
		WorldPoint center;
	}

	public static List<PluginNPC> solve(PluginNPC mainTarget, List<PluginNPC> allNpcs) {
		List<PluginNPC> result = new ArrayList<>();
		result.add(mainTarget);
		PluginNPC firstBounce = getNextBounceTarget(mainTarget, allNpcs);
		if (firstBounce != null) {
			result.add(firstBounce);
			PluginNPC secondBounce = getNextBounceTarget(firstBounce, allNpcs);
			if (secondBounce != null) {
				result.add(secondBounce);
			}
		}

		return result;
	}

	private static PluginNPC getNextBounceTarget(PluginNPC me, List<PluginNPC> allNpcs) {
		List<Integer> chunkOrder = ChunkUtils.getChunkPriorityGridOrder(me.getChunkId());
		Set<Integer> chunksToCheck = new HashSet<>(chunkOrder);
		List<PluginNPC> sortedNpcs = allNpcs.stream()
			.filter(npc -> npc.getNpc().getWorldLocation().distanceTo(me.getNpc().getWorldLocation()) <= 3)
			.filter(npc -> chunksToCheck.contains(npc.getChunkId()))
			.sorted(
				Comparator
					.comparing((PluginNPC npc) -> chunkOrder.indexOf(npc.getChunkId()))
					.thenComparing(npc -> -npc.getChunkOrder())
			)
			.collect(Collectors.toList());
		PluginNPC result = null;
		for (PluginNPC npc : sortedNpcs) {
			if (npc == me) {
				continue;
			}
			if (npc.isDead() || npc.getQueuedDamage() >= npc.getHp()) {
				continue;
			}
			boolean canBounce = canBounce(me.getNpc(), npc.getNpc());
			if (canBounce && result == null) {
				result = npc;
			}
		}
		return result;
	}

	private static boolean isInRange(WorldPoint wp, WorldPoint wp2) {
		return wp.distanceTo(wp2) <= RANGE;
	}

	private static boolean anyInRange(List<WorldPoint> wps1, List<WorldPoint> wps2) {
		return wps1.stream().anyMatch(wp -> wps2.stream().allMatch(wp2 -> isInRange(wp, wp2)));
	}

	private static boolean canBounce(NPC one, NPC two) {
		ScanTiles oneTiles = getScanTiles(one);
		ScanTiles twoTiles = getScanTiles(two);
		int sizeOne = one.getComposition().getSize();
		int sizeTwo = two.getComposition().getSize();
		switch (sizeOne) {
			case 1:
			case 3:
			case 5:
				return anyInRange(List.of(oneTiles.center), List.of(twoTiles.sw, twoTiles.center));
			case 2:
				List<WorldPoint> allTiles = getAllTiles(one);
				if (sizeTwo <= 3) {
					return anyInRange(allTiles, List.of(twoTiles.center));
				} else if (sizeTwo <= 5) {
					return anyInRange(allTiles, List.of(twoTiles.centerSw, twoTiles.center));
				}
			case 4:
				// not in colo, but anyway...
				List<WorldPoint> middle2x2 = getAllTiles(one.getWorldLocation().dx(1).dy(1), 2);
				return anyInRange(middle2x2, List.of(twoTiles.sw, twoTiles.centerSw));
		}
		return false;
	}

	private static ScanTiles getScanTiles(NPC npc) {
		int size = npc.getComposition().getSize();
		WorldPoint position = npc.getWorldLocation();
		switch (size) {
			case 1:
			case 2:
				return new ScanTiles(position, position, position);
			case 3:
				return new ScanTiles(position, position, position.dx(1).dy(1));
			case 4:
			case 5:
				return new ScanTiles(position, position.dx(1).dy(1), position.dx(2).dy(2));
			default:
				// unsupported size
				return new ScanTiles(position, position, position);
		}
	}

	private static List<WorldPoint> getAllTiles(NPC npc) {
		int size = npc.getComposition().getSize();
		WorldPoint position = npc.getWorldLocation();
		return getAllTiles(position, size);
	}

	private static List<WorldPoint> getAllTiles(WorldPoint position, int size) {
		List<WorldPoint> result = new ArrayList<>();
		for (int dx = 0; dx < size; ++dx) {
			for (int dy = 0; dy < size; ++dy) {
				result.add(position.dx(dx).dy(dy));
			}
		}
		return result;
	}
}
