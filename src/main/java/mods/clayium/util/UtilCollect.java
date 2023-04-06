package mods.clayium.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class UtilCollect {
    /**
     * Unknown sized TagList -> ItemStack list<br>
     */
    public static List<ItemStack> tagList2ItemList(NBTTagList tagList) {
        if (tagList == null || tagList.getTagType() != Constants.NBT.TAG_COMPOUND) return Collections.emptyList();
        List<ItemStack> res = new ArrayList<>(tagList.tagCount());

        for (NBTBase tag : tagList) {
            if (!(tag instanceof NBTTagCompound)) continue;

            int slot = ((NBTTagCompound) tag).getByte("Slot") & 255;

            if (slot >= res.size())
                res.addAll(Collections.nCopies(slot - res.size() + 1, ItemStack.EMPTY));

            res.set(slot, new ItemStack(((NBTTagCompound) tag)));
        }

        return res;
    }

    public static ItemStack[] tagList2Items(NBTTagList tagList) {
        return tagList2ItemList(tagList).toArray(new ItemStack[0]);
    }

    public static void tagList2Items(NBTTagList tagList, List<ItemStack> itemstacks) {
        if (tagList == null || itemstacks == null) return;

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound1 = tagList.getCompoundTagAt(i);
            short byte0 = tagCompound1.getShort("Slot");
            if (byte0 >= 0 && byte0 < itemstacks.size())
                itemstacks.set(byte0, new ItemStack(tagCompound1));
        }
    }

    /** @see net.minecraft.inventory.ItemStackHelper#saveAllItems(NBTTagCompound, NonNullList) */
    @Deprecated
    public static NBTTagList items2TagList(List<ItemStack> items) {
        NBTTagList tagList = new NBTTagList();
        if (items == null) return tagList;

        for(int i = 0; i < items.size(); ++i) {
            if (items.get(i) != null) {
                NBTTagCompound tagCompound1 = new NBTTagCompound();
                tagCompound1.setShort("Slot", (short) i);
                items.get(i).writeToNBT(tagCompound1);
                tagList.appendTag(tagCompound1);
            }
        }

        return tagList;
    }

    public static <K extends Enum<K>> NBTTagList enumMap2TagList(Map<K, ItemStack> map) {
        NBTTagList list = new NBTTagList();
        if (map.isEmpty()) return list;

        for (Map.Entry<K, ItemStack> entry : map.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("Slot", (byte) entry.getKey().ordinal());
            entry.getValue().writeToNBT(compound);
            list.appendTag(compound);
        }

        return list;
    }

    public static <K extends Enum<K>> void tagList2EnumMap(NBTTagList list, Map<K, ItemStack> map) {
        List<ItemStack> stacks = tagList2ItemList(list);
        if (stacks.isEmpty()) return;

        for (K key : map.keySet()) {
            if (key.ordinal() >= stacks.size()) map.put(key, ItemStack.EMPTY);
            else map.put(key, stacks.get(key.ordinal()));
        }
    }

    public static <E> List<E> slice(List<E> inv, int start, int end) {
        if (start < 0 || start >= end) {
            throw new IndexOutOfBoundsException("Invalid range: [" + start + ", " + end + ")");
        }

        end = Math.min(end, inv.size());

        List<E> result = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            result.add(inv.get(i));
        }
        return result;
    }

    public static List<Integer> makeZeros(int length) {
        return Arrays.stream(new int[length]).boxed().collect(Collectors.toList());
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> enumMapWithFill(K[] keyEnum, V fill) {
        assert keyEnum.length > 0;

        EnumMap<K, V> map = new EnumMap<>(keyEnum[0].getDeclaringClass());

        for (K key : keyEnum) {
            map.put(key, fill);
        }

        return map;
    }
}
