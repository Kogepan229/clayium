package mods.clayium.item.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FilterDuplicator extends FilterTemp {
    public FilterDuplicator() {
        super("filter_duplicator", 7);
    }

    @Override
    public boolean test(NBTTagCompound filterTag, ItemStack input) {
        return false;
    }

    public boolean isCopy(ItemStack filter) {
        return true;
    }

    public ItemStack clearCopyFlag(ItemStack filter) {
        return filter;
    }

    public ItemStack setCopyFlag(ItemStack filter) {
        return filter;
    }
}
