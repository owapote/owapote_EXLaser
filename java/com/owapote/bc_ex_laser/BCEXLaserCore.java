package com.owapote.bc_ex_laser;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.owapote.bc_ex_laser.proxy.CommonProxy;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk2;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk3;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk4;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = BCEXLaserCore.MODID, 
    name = BCEXLaserCore.NAME, 
    version = BCEXLaserCore.VERSION, 
    dependencies = "required-after:buildcraftcore@[8.0.0,)"
)
public class BCEXLaserCore
{
    public static final String MODID = "bc_ex_laser";
    public static final String NAME = "BuildCraft EXLaser";
    public static final String VERSION = "2.0.0";

    @SidedProxy(clientSide = "com.owapote.bc_ex_laser.proxy.ClientProxy",
                serverSide = "com.owapote.bc_ex_laser.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger("BCEXLaser");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        BCEXLaserCore.LOGGER.info("preInit:create ex_laser");
        // タイルエンティティだけ登録
        GameRegistry.registerTileEntity(TileExLaserMk2.class, "ex_laser_mk2");
        GameRegistry.registerTileEntity(TileExLaserMk3.class, "ex_laser_mk3");
        GameRegistry.registerTileEntity(TileExLaserMk4.class, "ex_laser_mk4");
        GameRegistry.registerTileEntity(TileExLaserMk5.class, "ex_laser_mk5");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
