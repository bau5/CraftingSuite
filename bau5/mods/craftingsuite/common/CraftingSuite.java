package bau5.mods.craftingsuite.common;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import bau5.mods.craftingsuite.common.tileentity.TileEntityCraftingTable;
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
	
	public Block craftingBlock;
	
	private int blockID;
	private int[] itemIDs;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev){
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		itemIDs = new int[5];
		try{
			config.load();
			blockID = config.getBlock("Crafting Block", 700).getInt(700);
			
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
		GameRegistry.registerTileEntity(TileEntityCraftingTable.class, "bau5csCT");
		GameRegistry.registerTileEntity(TileEntityProjectBench.class,  "bau5csPB");
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
	}
	
	@EventHandler
	public void mainInit(FMLInitializationEvent ev){
		//TODO recipes
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent ev){
		//TODO Check NEI
	}

	
}
