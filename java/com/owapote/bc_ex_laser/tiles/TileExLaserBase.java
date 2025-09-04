package com.owapote.bc_ex_laser.tiles;

import buildcraft.silicon.BCSiliconBlocks;
import buildcraft.lib.block.ILocalBlockUpdateSubscriber;
import buildcraft.lib.block.LocalBlockUpdateNotifier;
import buildcraft.lib.misc.LocaleUtil;
import buildcraft.lib.misc.MessageUtil;
import buildcraft.lib.misc.NBTUtilBC;
import buildcraft.lib.misc.VolumeUtil;
import buildcraft.lib.misc.data.AverageLong;
import buildcraft.lib.misc.data.Box;
import buildcraft.lib.net.PacketBufferBC;
import buildcraft.lib.tile.TileBC_Neptune;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.owapote.bc_ex_laser.BCEXLaserCore;
import com.owapote.bc_ex_laser.init.ModBlocks;

import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.mj.ILaserTarget;
import buildcraft.api.mj.ILaserTargetBlock;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.mj.MjCapabilityHelper;
import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.lib.mj.MjBatteryReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileExLaserBase extends TileBC_Neptune 
                implements ITickable, IDebuggable, ILocalBlockUpdateSubscriber{
    private static final int TARGETING_RANGE = 6;

    /**
     * レーザ出力倍率(TileLaserは４MJ)、これの倍率を指定する
     */
    protected long powerPerTick;

    /**
        バッテリー容量(TileLaserは1024MJ)、これの倍率を指定する
     */
    protected long batteryCapacity;

    //以下、修飾子のみを変更
    protected SafeTimeTracker clientLaserMoveInterval = new SafeTimeTracker(5, 10);
    protected SafeTimeTracker serverTargetMoveInterval = new SafeTimeTracker(10, 20);

    protected List<BlockPos> targetPositions = new ArrayList<>();
    protected BlockPos targetPos;
    public Vec3d laserPos;

    protected MjBattery battery = new MjBattery(0);
    protected AverageLong avgPower = new AverageLong(100);

    protected long averageClient;
    protected boolean worldHasUpdated = true;

    public TileExLaserBase(long batteryCapacity, long powerPerTick) {
        this.batteryCapacity = batteryCapacity * 1024 * MjAPI.MJ;
        this.powerPerTick = powerPerTick * 4 * MjAPI.MJ;
        this.battery = new MjBattery(this.batteryCapacity);
        caps.addProvider(new MjCapabilityHelper(new MjBatteryReceiver(this.battery)));
    }

    private void findPossibleTargets() {
        targetPositions.clear();

        //if (state.getBlock() != BCSiliconBlocks.laser)の除外

        IBlockState state = world.getBlockState(pos);
        EnumFacing face = state.getValue(BuildCraftProperties.BLOCK_FACING_6);

        VolumeUtil.iterateCone(world, pos, face, TARGETING_RANGE, true, (w, s, p, visible) -> {
            if (!visible) {
                return;
            }
            IBlockState stateAt = world.getBlockState(p);
            if (stateAt.getBlock() instanceof ILaserTargetBlock) {
                TileEntity tileAt = world.getTileEntity(p);
                if (tileAt instanceof ILaserTarget) {
                    targetPositions.add(p);
                }
            }
        });
    }


    //------------------------------------------------
    // 以下、buildcraft.silicon.tile.TileLaserのコピペ
    //------------------------------------------------

    @Override
    public int getUpdateRange() {
        return TARGETING_RANGE;
    }

    @Override
    public BlockPos getSubscriberPos() {
        return getPos();
    }

    /** 
     * @param world
     * @param eventPos
     * @param oldState
     * @param newState
     * @param flags
     */
    @Override
    public void setWorldUpdated(World world, BlockPos eventPos, IBlockState oldState, IBlockState newState, int flags) {
        this.worldHasUpdated = true;
    }

    public long getMaxPowerPerTick() {
        return powerPerTick;
    }

    protected long getBatteryCapacity() {
        return batteryCapacity;
    }

    private void randomlyChooseTargetPos() {
        List<BlockPos> targetsNeedingPower = new ArrayList<>();
        for(BlockPos position: targetPositions) {
            if (isPowerNeededAt(position)) {
                targetsNeedingPower.add(position);
            }
        }
        if (targetsNeedingPower.isEmpty()) {
            targetPos = null;
            BCEXLaserCore.LOGGER.info("No targets needing power found.");
            return;
        }
        targetPos = targetsNeedingPower.get(world.rand.nextInt(targetsNeedingPower.size()));
        BCEXLaserCore.LOGGER.info("Selected target at {}", targetPos);
    }

    /** 
     * @param position
     * @return boolean
     */
    private boolean isPowerNeededAt(BlockPos position) {
        if (position != null) {
            TileEntity tile = world.getTileEntity(position);
            if (tile instanceof ILaserTarget) {
                ILaserTarget target = (ILaserTarget) tile;
                return target.getRequiredLaserPower() > 0;
            }
        }
        return false;
    }

    private ILaserTarget getTarget() {
        if (targetPos != null) {
            if (world.getTileEntity(targetPos) instanceof ILaserTarget) {
                return (ILaserTarget) world.getTileEntity(targetPos);
            }
        }
        return null;
    }

    private void updateLaser() {
        if (targetPos != null) {
            laserPos = new Vec3d(targetPos)
                .addVector(
                    (5 + world.rand.nextInt(6) + 0.5) / 16D,
                    9 / 16D,
                    (5 + world.rand.nextInt(6) + 0.5) / 16D
                );
        } else {
            laserPos = null;
        }
    }

    public long getAverageClient() {
        return averageClient;
    }

    @Override
    public void update() {
        if (world == null) return;

        if (world.isRemote) {
            // クライアント側: レーザー描画位置だけ更新
            if (clientLaserMoveInterval.markTimeIfDelay(world)) {
                updateLaser();
            }
            return;
        } else {
            // [GPT]サーバーで向きを取得してログ出力（例）
            IBlockState state = world.getBlockState(pos);
            EnumFacing facing = EnumFacing.UP; // デフォルト
            try {
                if (state.getProperties().containsKey(BuildCraftProperties.BLOCK_FACING_6)) {
                    facing = state.getValue(BuildCraftProperties.BLOCK_FACING_6);
                } else {
                    BCEXLaserCore.LOGGER.warn("Block state for pos {} has no BLOCK_FACING_6 property: {}", pos, state);
                }
            } catch (Exception e) {
                BCEXLaserCore.LOGGER.warn("Failed to read BLOCK_FACING_6 at {} : {}", pos, e.toString());
            }
            BCEXLaserCore.LOGGER.info("MK2 Facing = {}", facing);
        }

        //サーバー側処理
        avgPower.tick();

        if (worldHasUpdated) {
            findPossibleTargets();
            worldHasUpdated = false;
            BCEXLaserCore.LOGGER.info("found targets: {}", targetPositions.size());
        }

        if (!isPowerNeededAt(targetPos)) {
            targetPos = null;
        }

        if (serverTargetMoveInterval.markTimeIfDelay(world) || !isPowerNeededAt(targetPos)) {
            randomlyChooseTargetPos();
        }

        ILaserTarget target = getTarget();
        if (target != null) {
            long max = getMaxPowerPerTick();
            max *= battery.getStored() + max;
            max /= battery.getCapacity() / 2;
            max = Math.min(Math.min(max, getMaxPowerPerTick()), target.getRequiredLaserPower());

            long power = battery.extractPower(0, max);
            long excess = target.receiveLaserPower(power);
            if (excess > 0) {
                battery.addPowerChecking(excess, false);
            }
            avgPower.push(power - excess);
        } else {
            avgPower.clear();
        }

        sendNetworkUpdate(NET_RENDER_DATA);

        markChunkDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("battery", battery.serializeNBT());
        if (laserPos != null) {
            nbt.setTag("laser_pos", NBTUtilBC.writeVec3d(laserPos));
        }
        if (targetPos != null) {
            nbt.setTag("target_pos", NBTUtilBC.writeBlockPos(targetPos));
        }
        avgPower.writeToNbt(nbt, "average_power");
        return nbt;
    }

    //--------------------
    //以下、NBTやDebugなど
    //--------------------

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        // TODO: remove in next version
        if (nbt.hasKey("mj_battery")) {
            nbt.setTag("battery", nbt.getTag("mj_battery"));
        }
        battery.deserializeNBT(nbt.getCompoundTag("battery"));
        targetPos = NBTUtilBC.readBlockPos(nbt.getTag("target_pos"));
        laserPos = NBTUtilBC.readVec3d(nbt.getTag("laser_pos"));
        avgPower.readFromNbt(nbt, "average_power");
    }

    @Override
    public void writePayload(int id, PacketBufferBC buffer, Side side) {
        super.writePayload(id, buffer, side);
        if (side == Side.SERVER) {
            if (id == NET_RENDER_DATA) {
                battery.writeToBuffer(buffer);
                buffer.writeBoolean(targetPos != null);
                if (targetPos != null) {
                    MessageUtil.writeBlockPos(buffer, targetPos);
                }
                buffer.writeLong((long) avgPower.getAverage());
            }
        }
    }

    @Override
    public void readPayload(int id, PacketBufferBC buffer, Side side, MessageContext ctx) throws IOException {
        super.readPayload(id, buffer, side, ctx);
        if (side == Side.CLIENT) {
            if (id == NET_RENDER_DATA) {
                battery.readFromBuffer(buffer);
                if (buffer.readBoolean()) {
                    targetPos = MessageUtil.readBlockPos(buffer);
                } else {
                    targetPos = null;
                }
                averageClient = buffer.readLong();
            }
        }
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        left.add("battery = " + battery.getDebugString());
        left.add("target = " + targetPos);
        left.add("laser = " + laserPos);
        left.add("average = " + LocaleUtil.localizeMjFlow(averageClient == 0 ? (long) avgPower.getAverage() : averageClient));
    }

    @Override
    public void validate() {
        super.validate();
        if (!world.isRemote) {
            LocalBlockUpdateNotifier.instance(world).registerSubscriberForUpdateNotifications(this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote) {
            LocalBlockUpdateNotifier.instance(world).removeSubscriberFromUpdateNotifications(this);
        }
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new Box(this).extendToEncompass(targetPos).getBoundingBox();
    }
}
