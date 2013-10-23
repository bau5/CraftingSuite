package bau5.mods.craftingsuite.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrafting extends BlockContainer {
	public Icon[] icons;
	
	public static ArrayList<CachedUpgrade> cache = new ArrayList<CachedUpgrade>();
	public class CachedUpgrade{
		public final int x, y, z;
		public final NBTTagCompound modList;
		public CachedUpgrade(TileEntityProjectBench tile, int x1, int y1, int z1){
			modList = tile.getModifiers();
			x = x1;
			y = y1;
			z = z1;
		}
	}

	protected BlockCrafting(int id, Material material) {
		super(id, material);
		setHardness(2.0f);
		setResistance(1.5f);
		setUnlocalizedName("bau5_craftingblock");
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world,
			int x, int y, int z) {
		ItemStack vanillaStack = super.getPickBlock(target, world, x, y, z);
		vanillaStack = ModificationStackHelper.makeStackFromInfo(vanillaStack, world.getBlockTileEntity(x, y, z));
		return vanillaStack;
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y,
			int z, int metadata, int fortune) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		ArrayList<ItemStack> list = super.getBlockDropped(world, x, y, z, metadata, fortune);
		for(ItemStack stack : list){
			if(stack.itemID == this.blockID){
				if(tile != null){
					stack = ModificationStackHelper.makeStackFromInfo(stack, tile);
				}else{
					if(cache.size() > 0){
						int index = -1;
						for(int i = 0; i < cache.size(); i++){
							CachedUpgrade item = cache.get(i);
							if(item.x == x && item.y == y && item.z == z){
								stack = ModificationStackHelper.makeStackFromInfo(stack, item.modList);
								index = i;
								break;
							}
						}
						if(index != -1)
							cache.remove(index);
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9){
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta){
		case 1: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 1, world, x, y, z);
			return true;
		case 2: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 2, world, x, y, z);
			return true;
		}
		return false;
		
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata){
		//TODO TE here
		switch(metadata){
		case 1: return new TileEntityModdedTable();
		case 2: return new TileEntityProjectBench();
		}
		return null;
    }

	@Override
	public TileEntity createNewTileEntity(World var1){
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister registrar) {
		icons = new Icon[6];
		icons[0] = registrar.registerIcon("craftingsuite:parts/rim");
		icons[1] = registrar.registerIcon("craftingsuite:pbblock1");
		icons[2] = registrar.registerIcon("craftingsuite:pbblock2");
		icons[3] = registrar.registerIcon("craftingsuite:parts/overlay_sides");
		icons[4] = registrar.registerIcon("craftingsuite:parts/top");
	}
	@Override
	public Icon getIcon(int side, int meta) {
		switch(meta){
		case 2:
			switch(side){
			case 0: return icons[2];
			case 1: return icons[4];
			default:return icons[0];
		}
		default: return Block.planks.getIcon(0, 0);
		}
	}
	
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs,
			List list) {
		list.add(ModificationStackHelper.makeBasicProjectBench());
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		return CraftingSuite.proxy.getRenderID();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z,
			int par5, int par6) {
		Random rand = new Random();	
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if(te instanceof TileEntityProjectBench)
			cache.add(new CachedUpgrade((TileEntityProjectBench)te, x, y, z));
		
		if(!(te instanceof IInventory))
			return;
		IInventory inv = (IInventory)te;
		int i = 0; 
		int size = inv.getSizeInventory();
		for(; i < size; i++)
		{
			ItemStack item = inv.getStackInSlot(i);
			if(item != null && item.stackSize > 0)
			{
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;
				EntityItem ei = new EntityItem(world, x + rx, y + ry, z + rz,
						new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));
				if(item.hasTagCompound())
					ei.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				float factor = 0.05f;
				ei.motionX = rand.nextGaussian() * factor;
				ei.motionY = rand.nextGaussian() * factor + 0.2F;
				ei.motionZ = rand.nextGaussian() * factor;
				if(!world.isRemote)
					world.spawnEntityInWorld(ei);
				item.stackSize = 0;
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
}
