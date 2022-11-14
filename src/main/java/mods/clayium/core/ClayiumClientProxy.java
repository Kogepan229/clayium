package mods.clayium.core;

import mods.clayium.block.ClayiumBlocks;
import mods.clayium.block.SiliconeColored;
import mods.clayium.item.ClayiumMaterials;
import mods.clayium.machine.ClayAssembler.TileEntityClayAssembler;
import mods.clayium.machine.ClayBuffer.TileEntityClayBuffer;
import mods.clayium.machine.ClayContainer.TESRClayContainer;
import mods.clayium.machine.ClayCraftingTable.TileEntityClayCraftingTable;
import mods.clayium.machine.ClayEnergyLaser.TESRClayEnergyLaser;
import mods.clayium.machine.ClayEnergyLaser.TileEntityClayEnergyLaser;
import mods.clayium.machine.ClayWorkTable.TileEntityClayWorkTable;
import mods.clayium.machine.ClayiumMachine.TileEntityClayiumMachine;
import mods.clayium.machine.CobblestoneGenerator.TileEntityCobblestoneGenerator;
import mods.clayium.machine.LaserReflector.TESRLaserReflector;
import mods.clayium.machine.LaserReflector.TileEntityLaserReflector;
import mods.clayium.machine.MultitrackBuffer.TileEntityMultitrackBuffer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClayiumClientProxy implements IClayiumProxy {
	float mode1velocity = 0.7F;
	float mode2acceleration = 0.9F;
	float mode2division = 1.1F;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain("clayium");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		registerColors();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {}

	public EnumFacing getHittingSide(EntityPlayer player) {
//		return Minecraft.getMinecraft().getRenderViewEntity().rayTrace(9999.0D, 0.0F).sideHit;
		RayTraceResult rtr = net.minecraftforge.common.ForgeHooks.rayTraceEyes(player, 9999.0D);
		return rtr == null ? EnumFacing.NORTH : rtr.sideHit;
	}

	public EntityPlayer getClientPlayer() {
		return (Minecraft.getMinecraft()).player;
	}

	@SideOnly(Side.CLIENT)
	public void registerColors() {
		ClayiumMaterials.requestTint(Minecraft.getMinecraft().getItemColors());

		for (Block coloredSilicone : ClayiumBlocks.siliconeColored) {
			if (coloredSilicone instanceof SiliconeColored) {
				Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor) coloredSilicone, coloredSilicone);
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor) coloredSilicone, coloredSilicone);
			}
		}
	}

	public void registerTileEntity() {
		GameRegistry.registerTileEntity(TileEntityClayWorkTable.class, new ResourceLocation(ClayiumCore.ModId, "clay_work_table"));
		GameRegistry.registerTileEntity(TileEntityClayCraftingTable.class, new ResourceLocation(ClayiumCore.ModId, "clay_crafting_table"));

		ClientRegistry.registerTileEntity(TileEntityClayBuffer.class, "clayium:buffer", new TESRClayContainer());
		ClientRegistry.registerTileEntity(TileEntityClayiumMachine.class, "clayium:machine", new TESRClayContainer());
		ClientRegistry.registerTileEntity(TileEntityCobblestoneGenerator.class, "clayium:cobblestone_generator", new TESRClayContainer());
		ClientRegistry.registerTileEntity(TileEntityClayAssembler.class, "clayium:assembler", new TESRClayContainer());
		ClientRegistry.registerTileEntity(TileEntityMultitrackBuffer.class, "clayium:multitrack_buffer", new TESRClayContainer());
		ClientRegistry.registerTileEntity(TileEntityClayEnergyLaser.class, "clayium:laser", new TESRClayEnergyLaser());
		ClientRegistry.registerTileEntity(TileEntityLaserReflector.class, "clayium:laser_reflector", new TESRLaserReflector());
	}

	public void updateFlightStatus(int mode) {
		EntityPlayerSP player = (EntityPlayerSP) this.getClientPlayer();
		if (mode == 0 || !player.capabilities.isFlying) return;

		MovementInput mi = player.movementInput;
		GameSettings settings = Minecraft.getMinecraft().gameSettings;

		double sin = Math.sin(Math.toRadians(player.rotationYaw));
		double cos = Math.cos(Math.toRadians(player.rotationYaw));
		mi.moveForward = (float)(player.motionZ * cos - player.motionX * sin);
		mi.moveStrafe = (float)(player.motionZ * sin + player.motionX * cos);

		if (mi.sneak) {
			if (mode >= 2) {
				player.motionY -= this.mode2acceleration;
				player.motionY /= this.mode2division;
			} else {
				player.motionY = -this.mode1velocity;
			}
		}

		if (mi.jump) {
			if (mode >= 2) {
				player.motionY += this.mode2acceleration;
				player.motionY /= this.mode2division;
			} else {
				player.motionY = this.mode1velocity;
			}
		}

		if (mi.jump == mi.sneak) {
			player.motionY = 0.0D;
		}

		if (settings.keyBindForward.isKeyDown()) {
			if (mode >= 2) {
				mi.moveForward += this.mode2acceleration;
				mi.moveForward /= this.mode2division;
			} else {
				mi.moveForward = this.mode1velocity;
			}
		}

		if (settings.keyBindBack.isKeyDown()) {
			if (mode >= 2) {
				mi.moveForward -= this.mode2acceleration;
				mi.moveForward /= this.mode2division;
			} else {
				mi.moveForward = -this.mode1velocity;
			}
		}

		if (!settings.keyBindForward.isKeyDown() && !settings.keyBindBack.isKeyDown()) {
			mi.moveForward = 0.0F;
		}

		if (settings.keyBindLeft.isKeyDown()) {
			if (mode >= 2) {
				mi.moveStrafe += this.mode2acceleration;
				mi.moveStrafe /= this.mode2division;
			} else {
				mi.moveStrafe = this.mode1velocity;
			}
		}

		if (settings.keyBindRight.isKeyDown()) {
			if (mode >= 2) {
				mi.moveStrafe -= this.mode2acceleration;
				mi.moveStrafe /= this.mode2division;
			} else {
				mi.moveStrafe = -this.mode1velocity;
			}
		}

		if (!settings.keyBindLeft.isKeyDown() && !settings.keyBindRight.isKeyDown()) {
			mi.moveStrafe = 0.0F;
		}

		player.motionX = mi.moveStrafe * cos - mi.moveForward * sin;
		player.motionZ = mi.moveForward * cos + mi.moveStrafe * sin;
	}

	public void overclockPlayer(int delay) {
		int i;
		try {
			i = ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, Minecraft.getMinecraft().playerController, "field_78781_i");
//			i = this.blockHitDelay.getInt(Minecraft.getMinecraft().playerController);
			if (i >= delay) {
				ObfuscationReflectionHelper.setPrivateValue(PlayerControllerMP.class, Minecraft.getMinecraft().playerController,  delay, "field_78781_i");
//				this.blockHitDelay.setInt(Minecraft.getMinecraft().playerController, delay);
			}
		} catch (IllegalArgumentException var7) {
			ClayiumCore.logger.catching(var7);
		}

		try {
			i = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_71467_ac");
//			i = this.rightClickDelayTimer.getInt(Minecraft.getMinecraft());
			if (i >= delay + 1) {
				ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(),  delay + 1, "field_71467_ac");
//				this.rightClickDelayTimer.setInt(Minecraft.getMinecraft(), delay + 1);
			}
		} catch (IllegalArgumentException var5) {
			ClayiumCore.logger.catching(var5);
		}
	}
}
