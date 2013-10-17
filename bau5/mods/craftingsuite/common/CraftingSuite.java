package bau5.mods.craftingsuite.common;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import bau5.mods.craftingsuite.common.tileentity.TileEntityCraftingTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false,
			channels = {Reference.CHANNEL}, packetHandler=PBPacketHandler.class)
public class CraftingSuite {
	
	@Instance(Reference.MOD_ID)
	public static CraftingSuite instance;
	@SidedProxy(clientSide = "bau5.mods.craftingsuite.client.ClientProxy",
				serverSide = "bau5.mods.craftingsuite.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static Block craftingBlock;
	public static Item  craftingFrame;
	public static Item  modItems;
	
	private int blockID;
	private int[] itemIDs;
	private int entityID;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev){
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		itemIDs = new int[5];
		try{
			config.load();
			blockID = config.getBlock("Crafting Block", 700).getInt(700);
			itemIDs[0] = config.getItem("Modifications",  18976).getInt(18976);
			itemIDs[1] = config.getItem("Crafting Frame", 18975).getInt(18975);
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "Crafting Suite: Failed loading configuration file.");
		}finally{
			config.save();
		}
		initParts();
	}

	private void initParts() {
		craftingBlock = new CraftingBlock(blockID, Material.wood);
		GameRegistry.registerBlock(craftingBlock, CraftingItemBlock.class, "craftingblock");
		GameRegistry.registerTileEntity(TileEntityModificationTable.class, "bau5csMT");
		GameRegistry.registerTileEntity(TileEntityModdedTable.class, "bau5csMCT");
		GameRegistry.registerTileEntity(TileEntityCraftingTable.class, "bau5csCT");
		GameRegistry.registerTileEntity(TileEntityProjectBench.class,  "bau5csPB");
		modItems = new ItemModifications(itemIDs[0]);
		craftingFrame = new ItemCraftingFrame(itemIDs[1], EntityCraftingFrame.class);
		entityID = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerModEntity(EntityCraftingFrame.class, "craftingframe", entityID++, this, 15, Integer.MAX_VALUE, false);
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		proxy.registerRenderingInformation();
	}
	
	@EventHandler
	public void mainInit(FMLInitializationEvent ev){
		//TODO recipes
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent ev){
	}

	
}
