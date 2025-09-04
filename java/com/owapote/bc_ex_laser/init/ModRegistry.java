package com.owapote.bc_ex_laser.init;

import com.owapote.bc_ex_laser.BCEXLaserCore;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk2;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk3;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.event.RegistryEvent;

@Mod.EventBusSubscriber(modid = BCEXLaserCore.MODID)
public class ModRegistry {

    // ブロック保持用
    public static BlockExLaserMk2 EX_LASER_MK2;
    public static BlockExLaserMk3 EX_LASER_MK3;

    /** 
     * ブロック登録
     * @param event
     */
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        EX_LASER_MK2 = new BlockExLaserMk2();
        EX_LASER_MK2.setRegistryName(new ResourceLocation(BCEXLaserCore.MODID, "ex_laser_mk2"));
        EX_LASER_MK2.setUnlocalizedName(BCEXLaserCore.MODID + ".ex_laser_mk2");

        event.getRegistry().register(EX_LASER_MK2);

        EX_LASER_MK3 = new BlockExLaserMk3();
        EX_LASER_MK3.setRegistryName(new ResourceLocation(BCEXLaserCore.MODID, "ex_laser_mk3"));
        EX_LASER_MK3.setUnlocalizedName(BCEXLaserCore.MODID + ".ex_laser_mk3");

        event.getRegistry().register(EX_LASER_MK3);
    }

    /** 
     * ItemBlock登録
     * @param event
     */
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ItemBlock itemBlock = new ItemBlock(EX_LASER_MK2);
        itemBlock.setRegistryName(EX_LASER_MK2.getRegistryName());
        event.getRegistry().register(itemBlock);

        ItemBlock itemBlock3 = new ItemBlock(EX_LASER_MK3);
        itemBlock3.setRegistryName(EX_LASER_MK3.getRegistryName());
        event.getRegistry().register(itemBlock3);
    }

    /** 
     * クライアント側モデル登録
     * @param event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Item item = Item.getItemFromBlock(EX_LASER_MK2);
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(
                    item,
                    0,
                    new net.minecraft.client.renderer.block.model.ModelResourceLocation(
                            new ResourceLocation(BCEXLaserCore.MODID, "ex_laser_mk2"), "inventory"
                    )
            );
        }

        Item item3 = Item.getItemFromBlock(EX_LASER_MK3);
        if (item3 != null) {
            ModelLoader.setCustomModelResourceLocation(
                    item3,
                    0,
                    new net.minecraft.client.renderer.block.model.ModelResourceLocation(
                            new ResourceLocation(BCEXLaserCore.MODID, "ex_laser_mk3"), "inventory"
                    )
            );
        }
    }
}
