package mods.clayium.machine.ClayChemicalReactor;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mods.clayium.machine.ClayiumMachine.TileEntityClayiumMachine;
import mods.clayium.machine.common.ClayiumRecipeProvider;
import mods.clayium.machine.common.IClayEnergyConsumer;
import mods.clayium.machine.common.Machine2To2;
import mods.clayium.machine.crafting.RecipeElement;
import mods.clayium.util.UtilTransfer;

public class TileEntityClayChemicalReactor extends TileEntityClayiumMachine implements Machine2To2 {

    protected static final int resultSlotNum = 2;

    public void initParams() {
        this.containerItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);

        this.setImportRoutes(NONE_ROUTE, 0, NONE_ROUTE, ENERGY_ROUTE, NONE_ROUTE, NONE_ROUTE);
        this.setExportRoutes(0, NONE_ROUTE, NONE_ROUTE, NONE_ROUTE, NONE_ROUTE, NONE_ROUTE);
        this.listSlotsImport.add(new int[] { Machine2To2.MATERIAL_1, Machine2To2.MATERIAL_2 });
        this.listSlotsImport.add(new int[] { Machine2To2.MATERIAL_1 });
        this.listSlotsImport.add(new int[] { Machine2To2.MATERIAL_2 });
        this.listSlotsExport.add(new int[] { Machine2To2.PRODUCT_1, Machine2To2.PRODUCT_2 });
        this.listSlotsExport.add(new int[] { Machine2To2.PRODUCT_1 });
        this.listSlotsExport.add(new int[] { Machine2To2.PRODUCT_2 });
        this.maxAutoExtract = new int[] { -1, -1, -1, 1 };
        this.maxAutoInsert = new int[] { -1, -1, -1 };
        this.slotsDrop = new int[] { Machine2To2.MATERIAL_1, Machine2To2.MATERIAL_2, Machine2To2.PRODUCT_1,
                Machine2To2.PRODUCT_2, this.getEnergySlot() };
        this.autoInsert = true;
        this.autoExtract = true;
    }

    @Override
    public boolean canCraft(List<ItemStack> materials) {
        if (materials.stream().anyMatch(ItemStack::isEmpty))
            return false;

        return this.canCraft(this.getRecipe(materials));
    }

    @Override
    public boolean canCraft(RecipeElement recipe) {
        if (recipe.isFlat()) return false;

        return UtilTransfer.canProduceItemStacks(recipe.getResults(), this.getContainerItemStacks(),
                Machine2To2.PRODUCT_1, Machine2To2.PRODUCT_1 + resultSlotNum, this.getInventoryStackLimit());
    }

    @Override
    public boolean canProceedCraft() {
        return IClayEnergyConsumer.compensateClayEnergy(this, this.debtEnergy, false);
    }

    @Override
    public void proceedCraft() {
        if (!IClayEnergyConsumer.compensateClayEnergy(this, this.debtEnergy)) return;

        ++this.craftTime;
        if (this.craftTime < this.timeToCraft) return;

        UtilTransfer.produceItemStacks(this.doingRecipe.getResults(), this.getContainerItemStacks(),
                Machine2To2.PRODUCT_1, Machine2To2.PRODUCT_1 + resultSlotNum,
                this.getInventoryStackLimit());

        this.craftTime = 0L;
        this.debtEnergy = 0L;
        this.timeToCraft = 0L;
        this.doingRecipe = this.getFlat();
    }

    @Override
    public boolean setNewRecipe() {
        this.doingRecipe = ClayiumRecipeProvider.getCraftPermRecipe(this, this.getStackInSlot(Machine2To2.MATERIAL_1),
                this.getStackInSlot(Machine2To2.MATERIAL_2));
        if (this.doingRecipe.isFlat()) return false;

        this.debtEnergy = this.doingRecipe.getEnergy();
        this.timeToCraft = this.doingRecipe.getTime();

        UtilTransfer.consumeByIngredient(this.doingRecipe.getIngredients(), this.getContainerItemStacks(),
                Machine2To2.MATERIAL_1, Machine2To2.MATERIAL_2 + 1);

        return true;
    }

    @Override
    public int getEnergySlot() {
        return 4;
    }
}
