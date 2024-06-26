package mods.clayium.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public interface IClayiumProxy {

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void serverLoad(FMLServerStartingEvent event);

    EnumFacing getHittingSide(EntityPlayer player);

    EntityPlayer getClientPlayer();

    void registerTileEntity();

    void updateFlightStatus(int mode);

    void overclockPlayer(int delay);
}
