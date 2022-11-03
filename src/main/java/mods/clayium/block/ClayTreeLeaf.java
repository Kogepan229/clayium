package mods.clayium.block;

import mods.clayium.block.common.ITieredBlock;
import mods.clayium.core.ClayiumCore;
import mods.clayium.item.ClayiumMaterials;
import mods.clayium.item.common.ClayiumMaterial;
import mods.clayium.item.common.ClayiumShape;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class ClayTreeLeaf extends BlockLeaves implements ITieredBlock {
    public ClayTreeLeaf() {
        super();

        setUnlocalizedName("clay_tree_leaves");
        setRegistryName(ClayiumCore.ModId, "clay_tree_leaves");
        setCreativeTab(ClayiumCore.tabClayium);

        this.leavesFancy = Minecraft.getMinecraft().gameSettings.fancyGraphics;

        setDefaultState(this.getDefaultState().withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ClayiumMaterials.get(ClayiumMaterial.clay, ClayiumShape.dust).getItem();
    }

    @Override
    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
        if (worldIn.rand.nextInt(chance) == 0) {
            spawnAsEntity(worldIn, pos, ClayiumMaterials.get(ClayiumMaterial.denseClay, ClayiumShape.dust));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return ClayiumMaterials.get(ClayiumMaterial.clay, ClayiumShape.dust).getItemDamage();
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.SNOW; // for return 16777215
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return null;
    }

    @Override
    public int getTier(ItemStack stackIn) {
        return 7;
    }

    @Override
    public int getTier(IBlockAccess access, BlockPos posIn) {
        return 7;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CHECK_DECAY, DECAYABLE).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;

        if (!state.getValue(DECAYABLE)) {
            i |= 4;
        }

        if (state.getValue(CHECK_DECAY)) {
            i |= 8;
        }

        return i;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DECAYABLE, (meta & 1) == 0).withProperty(CHECK_DECAY, (meta & 2) > 0);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        if (this.leavesFancy != Minecraft.getMinecraft().gameSettings.fancyGraphics) this.leavesFancy = !this.leavesFancy;
        return super.isOpaqueCube(state);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        if (this.leavesFancy != Minecraft.getMinecraft().gameSettings.fancyGraphics) this.leavesFancy = !this.leavesFancy;
        return super.getBlockLayer();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (this.leavesFancy != Minecraft.getMinecraft().gameSettings.fancyGraphics) this.leavesFancy = !this.leavesFancy;
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}