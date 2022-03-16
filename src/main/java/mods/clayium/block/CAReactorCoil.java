package mods.clayium.block;

import mods.clayium.block.common.BlockTiered;
import mods.clayium.util.UtilLocale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CAReactorCoil extends BlockTiered {
    public CAReactorCoil(int tier) {
        super(Material.IRON, "ca_reactor_coil", tier);
        setHardness(8.0F);
        setResistance(5.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        UtilLocale.localizeTooltip(tooltip, "tooltip.CAReactorCoil");
        super.addInformation(stack, player, tooltip, advanced);
    }
}
