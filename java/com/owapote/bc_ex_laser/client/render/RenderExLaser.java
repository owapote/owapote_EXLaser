/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package com.owapote.bc_ex_laser.client.render;

import javax.annotation.Nonnull;

import com.owapote.bc_ex_laser.tiles.TileExLaserBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.client.model.animation.FastTESR;

import buildcraft.api.properties.BuildCraftProperties;

import buildcraft.lib.client.render.laser.LaserData_BC8;
import buildcraft.lib.client.render.laser.LaserRenderer_BC8;

import buildcraft.core.client.BuildCraftLaserManager;
import buildcraft.core.item.ItemGoggles;
import buildcraft.silicon.BCSiliconConfig;
import buildcraft.silicon.tile.TileLaser;

public class RenderExLaser extends FastTESR<TileExLaserBase> {
    private static final int MAX_POWER = BuildCraftLaserManager.POWERS.length - 1;

    private int laserThickness;

    public RenderExLaser(int laserThickness){
        this.laserThickness = laserThickness;
    }

    /** 
     * @param tile
     * @param x
     * @param y
     * @param z
     * @param partialTicks
     * @param destroyStage
     * @param partial
     * @param buffer
     */
    @Override
    public void renderTileEntityFast(@Nonnull TileExLaserBase tile, double x, double y, double z, float partialTicks, int destroyStage, float partial, @Nonnull BufferBuilder buffer) {

        if (BCSiliconConfig.renderLaserBeams || isPlayerWearingGoggles()) {
            Minecraft.getMinecraft().mcProfiler.startSection("bc");
            Minecraft.getMinecraft().mcProfiler.startSection("laser");

            buffer.setTranslation(x - tile.getPos().getX(), y - tile.getPos().getY(), z - tile.getPos().getZ());

            if (tile.laserPos != null) {
                long avg = tile.getAverageClient();
                if (avg > 200_000) {
                    avg += 200_000;
                    EnumFacing side = tile.getWorld().getBlockState(tile.getPos()).getValue(BuildCraftProperties.BLOCK_FACING_6);
                    Vec3d offset = new Vec3d(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(4 / 16D));
                    int index = (int) (avg * MAX_POWER / tile.getMaxPowerPerTick());
                    if (index > MAX_POWER) {
                        index = MAX_POWER;
                    }
                    LaserData_BC8 laser = new LaserData_BC8(BuildCraftLaserManager.POWERS[index], new Vec3d(tile.getPos()).add(offset), tile.laserPos, this.laserThickness / 16D);
                    LaserRenderer_BC8.renderLaserDynamic(laser, buffer);
                }
            }

            buffer.setTranslation(0, 0, 0);

            Minecraft.getMinecraft().mcProfiler.endSection();
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
    }

    protected boolean isPlayerWearingGoggles() {
        Item headArmor = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
        return headArmor instanceof ItemGoggles;
    }
}