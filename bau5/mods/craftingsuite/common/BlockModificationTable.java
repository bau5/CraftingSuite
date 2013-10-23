package bau5.mods.craftingsuite.common;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.client.ClientProxy;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockModificationTable extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	
	public BlockModificationTable(int id, Material mat) {
		super(id, mat);
		setHardness(2.0f);
		setResistance(1.5f);
		setUnlocalizedName("bau5_moddingblock");
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9){
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta){
		case 0: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 0, world, x, y, z);
			break;
		}
		return true;
		
	}
	
	@Override
	public Icon getIcon(int par1, int par2) {
		return Block.cobblestone.getIcon(par1, par2);
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata){
		switch(metadata){
		case 0: return new TileEntityModificationTable();
		}
		return null;
    }

	@Override
	public TileEntity createNewTileEntity(World var1){
		return null;
	}
	
	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List tabList) {
		tabList.add(new ItemStack(blockID, 1, 0));
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
