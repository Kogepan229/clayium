package mods.clayium.machine;

import mods.clayium.core.ClayiumCore;
import mods.clayium.machine.ClayBuffer.ClayBuffer;
import mods.clayium.machine.ClayContainer.ClayContainer;
import mods.clayium.machine.ClayCraftingTable.ClayCraftingTable;
import mods.clayium.machine.ClayWorkTable.ClayWorkTable;
import mods.clayium.machine.ClayiumMachine.ClayiumMachine;
import mods.clayium.machine.CobblestoneGenerator.CobblestoneGenerator;
import mods.clayium.util.TierPrefix;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ClayiumMachines {
    public static final Map<EnumMachineKind, Map<TierPrefix, ClayContainer>> machineMap = new EnumMap<>(EnumMachineKind.class);

    public static void registerMachines() {
        add(EnumMachineKind.workTable, 0, new ClayWorkTable());
        add(EnumMachineKind.craftingTable, 0, new ClayCraftingTable());

        add(EnumMachineKind.ECCondenser, 3, "mk1");
        add(EnumMachineKind.ECCondenser, 4, "mk2");

        add(EnumMachineKind.bendingMachine, new int[] { 1, 2, 3, 4, 5, 6, 7, 9 });
        add(EnumMachineKind.wireDrawingMachine, new int[] { 1, 2, 3, 4 });
        add(EnumMachineKind.pipeDrawingMachine, new int[] { 1, 2, 3, 4 });
        add(EnumMachineKind.cuttingMachine, new int[] { 1, 2, 3, 4 });
        add(EnumMachineKind.lathe, new int[] { 1, 2, 3, 4 });

        add(EnumMachineKind.cobblestoneGenerator, new int[] { 1, 2, 3, 4, 5, 6, 7 }, CobblestoneGenerator.class);

        add(EnumMachineKind.condenser, new int[] { 2, 3, 4, 5, 10 });
        add(EnumMachineKind.grinder, new int[]{ 2, 3, 4, 5, 6, 10 });
        add(EnumMachineKind.decomposer, new int[]{ 2, 3, 4 });
        add(EnumMachineKind.millingMachine, new int[] { 1, 3, 4 });

//        add(EnumMachineKind.assembler, new int[] { 3, 4, 6, 10});
//        add(EnumMachineKind.inscriber, new int[] { 3, 4 });
//        add(EnumMachineKind.centrifuge, new int[] { 3, 4, 5, 6 });
        add(EnumMachineKind.smelter, new int[]{ 4, 5, 6, 7, 8, 9 });

        add(EnumMachineKind.buffer, new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 }, ClayBuffer.class);

//        add(EnumMachineKind.creativeCESource, 13);
//
//        add(EnumMachineKind.chemicalReactor, new int[]{ 4, 5, 8 });
    }

    private static void add(EnumMachineKind kind, int tier, ClayContainer block) {
        TierPrefix _tier = TierPrefix.get(tier);

        if (!machineMap.containsKey(kind)) machineMap.put(kind, new EnumMap<>(TierPrefix.class));
        if (machineMap.containsKey(kind) && machineMap.get(kind).containsKey(_tier)) {
            ClayiumCore.logger.error("The machine already exists  [" + kind.getRegisterName() + "] [" + _tier.getPrefix() + "]");
            return;
        }

        machineMap.get(kind).put(_tier, block);
    }

    /**
     * @param blockClass whose constructor should have the (I)tier argument
     */
    private static void add(EnumMachineKind kind, int[] tiers, Class<? extends ClayContainer> blockClass) {
        if (!machineMap.containsKey(kind)) machineMap.put(kind, new EnumMap<>(TierPrefix.class));

        for (int tier : tiers) {
            try {
                ClayContainer block = blockClass.getDeclaredConstructor(int.class).newInstance(tier);

                add(kind, tier, block);
            } catch (Exception ignore) {}
        }
    }

    private static void add(EnumMachineKind kind, int tier) {
        add(kind, tier, new ClayiumMachine(kind, tier));
    }
    private static void add(EnumMachineKind kind, int[] tiers) {
        for (int i : tiers)
            add(kind, i, new ClayiumMachine(kind, i));
    }
    private static void add(EnumMachineKind kind, int tier, String suffix) {
        add(kind, tier, new ClayiumMachine(kind, suffix, tier));
    }

    public static Block get(EnumMachineKind kind, TierPrefix tier) {
        return machineMap.get(kind).get(tier);
    }
    public static Block get(EnumMachineKind kind, int tier) {
        return get(kind, TierPrefix.get(tier));
    }

    public static List<Block> getBlocks() {
        List<Block> res = new ArrayList<>();

        for (Map<TierPrefix, ClayContainer> kind : machineMap.values()) {
            res.addAll(kind.values());
        }

        return res;
    }
}
