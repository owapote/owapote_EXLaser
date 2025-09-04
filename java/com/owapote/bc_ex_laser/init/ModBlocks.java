package com.owapote.bc_ex_laser.init;

import com.owapote.bc_ex_laser.blocks.BlockExLaserMk2;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk3;

/**
 * Mod 内ブロックの参照を保持するクラス
 * 実際の生成・登録は RegistryEvent 内で行う
 */
public final class ModBlocks {
    // Block の参照だけ保持
    public static BlockExLaserMk2 EX_LASER_MK2;
    public static BlockExLaserMk3 EX_LASER_MK3;
}
