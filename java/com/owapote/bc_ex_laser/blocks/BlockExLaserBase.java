package com.owapote.bc_ex_laser.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.owapote.bc_ex_laser.BCEXLaserCore;
import com.owapote.bc_ex_laser.tiles.TileExLaserMk2;

import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.lib.block.BlockBCTile_Neptune;
import buildcraft.lib.block.IBlockWithFacing;
import buildcraft.lib.tile.TileBC_Neptune;

public abstract class BlockExLaserBase extends BlockBCTile_Neptune implements IBlockWithFacing {

    public BlockExLaserBase(Material material) {
        super(material, "");
        setCreativeTab(CreativeTabs.REDSTONE);

        setDefaultState(this.blockState.getBaseState()
            .withProperty(BuildCraftProperties.BLOCK_FACING_6, EnumFacing.UP));
    }

    /** 
     * @param world
     * @param state
     * @return TileBC_Neptune
     */
    @Override
    abstract public TileBC_Neptune createTileEntity(World world, IBlockState state);

    /** 
     * @param world
     * @param pos
     * @param facing
     * @param hitX
     * @param hitY
     * @param hitZ
     * @param meta
     * @param placer
     * @return IBlockState
     */
    @Override
    public final IBlockState getStateForPlacement(World world, BlockPos pos,
            EnumFacing facing, float hitX, float hitY, float hitZ,
            int meta, EntityLivingBase placer) {

        //レーザーの照射口の方向を取得
        EnumFacing placeFacing = facing.getOpposite();
        return this.getDefaultState().withProperty(BuildCraftProperties.BLOCK_FACING_6, placeFacing);
    }

    //------------------------------------------------
    // 以下、buildcraft.silicon.block.BlockLaserのコピペ
    //------------------------------------------------

    @Override
    public final boolean hasTileEntity(IBlockState state) {
        return true;
    }

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
