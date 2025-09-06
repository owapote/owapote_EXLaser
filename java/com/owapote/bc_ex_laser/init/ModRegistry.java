package com.owapote.bc_ex_laser.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.owapote.bc_ex_laser.BCEXLaserCore;
import com.owapote.bc_ex_laser.blocks.BlockExLaserBase;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk2;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk3;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk4;
import com.owapote.bc_ex_laser.blocks.BlockExLaserMk5;
import com.owapote.bc_ex_laser.items.ItemExLaserMaterialBase;
import com.owapote.bc_ex_laser.items.ItemHeatproofDiamondPowder;

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

    //使うものを予め格納
    private static final List<Pair<Supplier<BlockExLaserBase>, String>> EX_LASER_DEFINES =
    Arrays.asList(
        Pair.of(BlockExLaserMk2::new, "ex_laser_mk2"),
        Pair.of(BlockExLaserMk3::new, "ex_laser_mk3"),
        Pair.of(BlockExLaserMk4::new, "ex_laser_mk4"),
        Pair.of(BlockExLaserMk5::new, "ex_laser_mk5")
    );
    // private static final List<Pair<Supplier<ItemExLaserMaterialBase>, String>> EX_LASER_MATERIAL_DEFINES =
    // Arrays.asList(
    //     Pair.of(ItemHeatproofDiamondPowder::new, "heatproof_diamond_powder")
    // );

    //ブロックを保持する
    private static final HashMap<BlockExLaserBase, String> EX_LASERS = new HashMap<>();
    //アイテムを保持する
    //private static final Map<ItemExLaserMaterialBase, String> EX_LASER_MATERIALS = new HashMap<>();

    /** 
     * ブロック(実体)登録
     * @param event
     */
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Pair<Supplier<BlockExLaserBase>, String> definePair : EX_LASER_DEFINES) {
            //中身だけ作成
            BlockExLaserBase block = createExLaserBlock(definePair.getLeft(), definePair.getRight());
            //実際に登録
            event.getRegistry().register(block);
            EX_LASERS.put(block, definePair.getRight());
        }
    }

    private static BlockExLaserBase createExLaserBlock(Supplier<BlockExLaserBase> exLaser, String name ) {
        BlockExLaserBase block = exLaser.get();
        block.setRegistryName(new ResourceLocation(BCEXLaserCore.MODID, name));
        block.setUnlocalizedName(BCEXLaserCore.MODID + "." + name);
        return block;
    }

    /** 
     * アイテム(手持ち)登録
     * @param event
     */
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Map.Entry<BlockExLaserBase, String> exLaserEntry : EX_LASERS.entrySet()) {
            event.getRegistry().register(createExLaserItems(exLaserEntry.getKey()));
        }

        // // 素材アイテム登録
        // for (Pair<Supplier<ItemExLaserMaterialBase>, String> definePair : EX_LASER_MATERIAL_DEFINES) {
        //     ItemExLaserMaterialBase item = definePair.getLeft().get();
        //     item.setRegistryName(new ResourceLocation(BCEXLaserCore.MODID, definePair.getRight()));
        //     item.setUnlocalizedName(BCEXLaserCore.MODID + "." + definePair.getRight());
        //     event.getRegistry().register(item);
        //     EX_LASER_MATERIALS.put(item, definePair.getRight());
        // }
    }

    private static ItemBlock createExLaserItems(BlockExLaserBase blockExLaser){
        ItemBlock itemBlock = new ItemBlock(blockExLaser);
        itemBlock.setRegistryName(blockExLaser.getRegistryName());

        return itemBlock;
    }

    private static Item createExLaserMaterialItems(ItemExLaserMaterialBase itemExLaserMaterial){
        itemExLaserMaterial.setRegistryName(itemExLaserMaterial.getRegistryName());
        return itemExLaserMaterial;
    }

    /** 
     * クライアント側モデル登録
     * @param event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Map.Entry<BlockExLaserBase, String> entry : EX_LASERS.entrySet()) {
            createExLaserModels(entry);
        }
    }

    private static void createExLaserModels(Map.Entry<BlockExLaserBase, String> entry){
        Item item = Item.getItemFromBlock(entry.getKey());
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new net.minecraft.client.renderer.block.model.ModelResourceLocation(
                    new ResourceLocation(BCEXLaserCore.MODID, entry.getValue()), 
                    entry.getValue()
                )
            );
        }
    }
}
