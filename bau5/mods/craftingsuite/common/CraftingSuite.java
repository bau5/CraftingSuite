package bau5.mods.craftingsuite.common;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
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
	
	public static Block modificationTableBlock;
	public static Block craftingTableBlock;
	public static Item  craftingFrame;
	public static Item  modItems;
	
	private int[] blockIDs;
	private int[] itemIDs;
	private int entityID;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev){
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		itemIDs = new int[5];
		blockIDs = new int[2];
		try{
			config.load();
			blockIDs[0] = config.getBlock("Modification Block", 700).getInt(700);
			blockIDs[1] = config.getBlock("Crafting Block", 701).getInt(701);
			itemIDs[0] = config.getItem("Modifications",  18976).getInt(18976) -256;
			itemIDs[1] = config.getItem("Crafting Frame", 18975).getInt(18975) -256;
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "Crafting Suite: Failed loading configuration file.");
		}finally{
			config.save();
		}
		initParts();
	}

	private void initParts() {
		modificationTableBlock = new BlockModificationTable(blockIDs[0], Material.wood);
		craftingTableBlock = new BlockCrafting(blockIDs[1], Material.wood);
		GameRegistry.registerBlock(modificationTableBlock, ItemBlockModificationTable.class, "modtableblock");
		GameRegistry.registerBlock(craftingTableBlock, ItemBlockCrafting.class, "craftingtableblock");
		GameRegistry.registerTileEntity(TileEntityModificationTable.class, "bau5csMT");
		GameRegistry.registerTileEntity(TileEntityModdedTable.class, "bau5csMCT");
		GameRegistry.registerTileEntity(TileEntityCraftingTable.class, "bau5csCT");
		GameRegistry.registerTileEntity(TileEntityProjectBench.class,  "bau5csPB");
		modItems = new ItemModifications(itemIDs[0]);
//		craftingFrame = new ItemCraftingFrame(itemIDs[1], EntityCraftingFrame.class);
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
		registerRecipes();
	}
	
	public void registerRecipes(){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 1, 0), new Object[]{
			"SSS", "SCS", "SSS", 
			'C', new ItemStack(Block.workbench.blockID, 1, OreDictionary.WILDCARD_VALUE), 
			'S', new ItemStack(Item.stick.itemID, 1, OreDictionary.WILDCARD_VALUE) 
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 1, 1), new Object[]{
			"ICI",
			'C', new ItemStack(modItems.itemID, 1, 0), 
			'I', new ItemStack(Item.ingotIron, 1, OreDictionary.WILDCARD_VALUE)
		}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(modItems, 1, 2), new Object[]{
			new ItemStack(modItems.itemID, 1, 1), 
			new ItemStack(Block.chest, 1, OreDictionary.WILDCARD_VALUE)
		}));
	}

	
}
