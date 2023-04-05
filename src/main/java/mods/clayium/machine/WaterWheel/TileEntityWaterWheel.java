package mods.clayium.machine.WaterWheel;

import mods.clayium.block.tile.TileGeneric;
import mods.clayium.machine.ClayContainer.TileEntityClayContainer;
import mods.clayium.machine.ClayiumMachine.TileEntityClayiumMachine;
import mods.clayium.util.UtilTier;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class TileEntityWaterWheel extends TileEntityClayContainer {
    public int progress = 0;
    public int progressEfficiency = 1000;
    protected static int progressMax = 20000;

    public void initParams() {
        this.progressEfficiency = (int)((double)this.progressEfficiency * Math.pow(Math.max(this.tier, 1.0D), 3.0D));
        this.containerItemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
        this.listSlotsImport.add(new int[]{0});
        this.listSlotsExport.add(new int[]{0});
        this.slotsDrop = new int[]{0};
        this.autoInsert = false;
        this.autoExtract = false;
    }

    @Override
    public void readMoreFromNBT(NBTTagCompound tagCompound) {
        super.readMoreFromNBT(tagCompound);
        this.progress = tagCompound.getInteger("Progress");
        this.progressEfficiency = tagCompound.getInteger("ProgressEfficiency");
    }

    @Override
    public NBTTagCompound writeMoreToNBT(NBTTagCompound tagCompound) {
        super.writeMoreToNBT(tagCompound);
        tagCompound.setInteger("Progress", this.progress);
        tagCompound.setInteger("ProgressEfficiency", this.progressEfficiency);
        return tagCompound;
    }

    public void update() {
        if (this.world.isRemote || TileGeneric.random.nextInt(40) >= this.countSurroundingWater()) {
            return;
        }

        this.progress = (int)((double)this.progress + (double)this.progressEfficiency * Math.pow(Math.max(this.tier, 1.0D), 3.0D));
        if (this.progress < progressMax) {
            return;
        }

        this.progress -= progressMax;
        this.progressEfficiency -= TileGeneric.random.nextInt(5) == 0 ? 1 : 0;
        this.emitEnergy();
    }

    public int getProgressIcon() {
        return this.progress * 10 / progressMax / 2 == 0 ? 0 : 1;
    }

    public double getProgress() {
        return (double)this.progress / (double)progressMax;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public void emitEnergy() {
        for (EnumFacing direction : EnumFacing.VALUES) {
            TileEntity te = this.world.getTileEntity(this.pos.offset(direction));
            if (te != null && te instanceof TileEntityClayiumMachine
                    && UtilTier.acceptWaterWheel(((TileEntityClayiumMachine) te).getTier())
                    && (double) ((TileEntityClayiumMachine) te).getContainEnergy() < 5.0D * Math.pow(Math.max(this.tier, 1.0D), 8.0D)) {
                ((TileEntityClayiumMachine) te).setContainEnergy(((TileEntityClayiumMachine) te).getContainEnergy() + (long) Math.pow(Math.max(this.tier, 1.0D), 8.0D));
                te.markDirty();
            }
        }
    }

    public int countSurroundingWater() {
        int count = 0;

        for (BlockPos pos : BlockPos.getAllInBox(-1, -1, -1, 1, 1, 1)) {
            IBlockState state = this.getWorld().getBlockState(this.pos.add(pos));

            // TODO 拡張性
            if (state.getMaterial() == Material.WATER && state.getBlock() instanceof BlockDynamicLiquid) {
                count++;
            }
        }

        return count;
    }
}
