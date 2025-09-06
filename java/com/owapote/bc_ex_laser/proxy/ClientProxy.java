package com.owapote.bc_ex_laser.proxy;

import com.owapote.bc_ex_laser.client.render.RenderExLaser;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk2;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk3;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk4;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk5;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    /** 
     * @param event
     */
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientRegistry.bindTileEntitySpecialRenderer(TileExLaserMk2.class, (TileEntitySpecialRenderer)new RenderExLaser(2));
        ClientRegistry.bindTileEntitySpecialRenderer(TileExLaserMk3.class, (TileEntitySpecialRenderer)new RenderExLaser(3));
        ClientRegistry.bindTileEntitySpecialRenderer(TileExLaserMk4.class, (TileEntitySpecialRenderer)new RenderExLaser(4));
        ClientRegistry.bindTileEntitySpecialRenderer(TileExLaserMk5.class, (TileEntitySpecialRenderer)new RenderExLaser(5));
    }
}
