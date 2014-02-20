package bau5.mods.craftingsuite.common;

import java.util.Calendar;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
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
import cpw.mods.fml.common.registry.LanguageRegistry;


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
	public static Item  planItem;
	
	public static boolean VERBOSE;
	public static boolean VERSION_CHECK;
	public static boolean EE_ENABLED;
	public static boolean cmas;
	
	private int[] blockIDs;
	private int[] itemIDs;
	private int entityID;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev){
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		itemIDs = new int[2];
		blockIDs = new int[2];
		try{
			config.load();
			blockIDs[0] = config.getBlock("Modification Block", 700).getInt(700);
			blockIDs[1] = config.getBlock("Crafting Block", 701).getInt(701);
			itemIDs[0] = config.getItem("Modifications",  18976).getInt(18976) -256;
			itemIDs[1] = config.getItem("Plan",  18977).getInt(18977) -256;
			VERBOSE = config.get(Configuration.CATEGORY_GENERAL, "Verbose Logging", false).getBoolean(false);
			VERSION_CHECK = config.get(Configuration.CATEGORY_GENERAL, "Version Check", true).getBoolean(true);
			EE_ENABLED = config.get(Configuration.CATEGORY_GENERAL, "Easter Eggs Enabled", true).getBoolean(true);
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "Crafting Suite: Failed loading configuration file.");
		}finally{
			config.save();
		}
		if(VERSION_CHECK)
			VersionChecker.go();
		dc();
		initParts();
	}

	private void initParts() {
		modificationTableBlock = new BlockModificationTable(blockIDs[0], Material.wood);
		craftingTableBlock = new BlockCrafting(blockIDs[1], Material.wood);
		GameRegistry.registerBlock(modificationTableBlock, ItemBlockModificationTable.class, "modtableblock");
		GameRegistry.registerBlock(craftingTableBlock, ItemBlockCrafting.class, "craftingtableblock");
		GameRegistry.registerTileEntity(TileEntityModificationTable.class, "bau5csMT");
		GameRegistry.registerTileEntity(TileEntityModdedTable.class, "bau5csMCT");
//		GameRegistry.registerTileEntity(TileEntityCraftingTable.class, "bau5csCT");
		GameRegistry.registerTileEntity(TileEntityProjectBench.class,  "bau5csPB");
		modItems = new ItemModifications(itemIDs[0]);
		planItem = new ItemPlan(itemIDs[1]);
		entityID = EntityRegistry.findGlobalUniqueEntityId();
//		EntityRegistry.registerModEntity(EntityCraftingFrame.class, "craftingframe", entityID++, this, 15, Integer.MAX_VALUE, false);
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		proxy.registerRenderingInformation();
	}
	
	@EventHandler
	public void mainInit(FMLInitializationEvent ev){
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent ev){
		registerRecipes();
		loadLanguages();
	}
	
	private void loadLanguages() {
		for(String lang : Reference.LANGUAGES){
			LanguageRegistry.instance().loadLocalization(new ResourceLocation("/bau5/mods/craftingsuite/langs/" +lang +".xml").getResourcePath(), lang, true);
		}
	}

	private void dc(){
		int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		boolean m = Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;
		if(EE_ENABLED && m && d > 20)
			cmas = true;
	}

	public void registerRecipes(){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 1, 0), new Object[]{
			" S ", "SCS", " S ", 		//Crafting Modifier
			'C', new ItemStack(Block.workbench.blockID, 1, OreDictionary.WILDCARD_VALUE), 
			'S', new ItemStack(Item.stick.itemID, 1, OreDictionary.WILDCARD_VALUE) 
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 1, 1), new Object[]{
			"ICI",						//Holding Modifier
			'C', new ItemStack(modItems.itemID, 1, 0), 
			'I', new ItemStack(Item.ingotIron, 1, OreDictionary.WILDCARD_VALUE)
		}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(modItems, 2, 2), new Object[]{
										//Storing Modifier
			new ItemStack(modItems.itemID, 1, 1), 
			new ItemStack(Block.chest, 1, OreDictionary.WILDCARD_VALUE)
		}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(modItems, 2, 2), new Object[]{
			new ItemStack(modItems.itemID, 1, 0), 
			new ItemStack(Item.ingotIron, 1, 0),
			new ItemStack(Block.chest, 1, OreDictionary.WILDCARD_VALUE)
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modificationTableBlock.blockID, 1, 0), new Object[]{
			" C ", "PRP", "SSS",
			'C', new ItemStack(Block.workbench.blockID, 1, 0),
			'P', new ItemStack(Block.planks, 1, OreDictionary.WILDCARD_VALUE),
			'R', new ItemStack(Item.redstone, 1, 0),
			'S', new ItemStack(Block.stone.blockID, 1, 0)
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 2, 3), new Object[]{
			" I ", "IBI", "III",
			'I', new ItemStack(Item.ingotIron, 1, 0),
			'B', new ItemStack(Item.bucketEmpty, 1, 0)
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 2, 4), new Object[]{
			"P P", "PIP", " P ", 'P', Block.planks, 'I', new ItemStack(Item.ingotIron, 1, 0)
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(modItems, 2, 5), new Object[]{
			"SIS", "IPI", "SIS", 'S', Block.stone, 'I', Item.ingotIron, 'P', planItem
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(planItem, 8, 0), new Object[]{
			" PS", "PNP", "SP ", 'P', Item.paper, 'S', Item.stick, 'N', Item.goldNugget
		}));
	}
}