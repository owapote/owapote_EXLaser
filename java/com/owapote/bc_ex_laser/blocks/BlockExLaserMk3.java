package com.owapote.bc_ex_laser.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

import com.owapote.bc_ex_laser.tiles.TileExLaserMk3;

import buildcraft.lib.tile.TileBC_Neptune;

public class BlockExLaserMk3 extends BlockExLaserBase {

    public BlockExLaserMk3() {
        super(Material.IRON);
    }

    /** 
     * @param world
     * @param state
     * @return TileBC_Neptune
     */
    @Override
    public TileBC_Neptune createTileEntity(World world, IBlockState state) {
        //BCEXLaserCore.LOGGER.info("createTileEntity:ex_laser_mk3");
        return new TileExLaserMk3();
    }

    //------------------------------------------------
    // 以下、buildcraft.silicon.block.BlockLaserのコピペ
    //------------------------------------------------

    @Override
    public boolean canFaceVertically() {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
