package bau5.mods.craftingsuite.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CraftingBlock extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	
	public CraftingBlock(int id, Material mat) {
		super(id, mat);
		setHardness(2.0f);
		setResistance(1.5f);
		setUnlocalizedName("bau5_craftingblock");
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9){
		TileEntity te = world.getBlockTileEntity(x, y, z);
		//TODO add check for upgrade Item
		if(te == null)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta){
		case 0: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 0, world, x, y, z);
			break;
		case 1: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 1, world, x, y, z);
			break;
//		case 0: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 0, world, x, y ,z);
//			break;
//		case 1: if(!player.isSneaking()) player.openGui(CraftingSuite.instance, 1, world, x, y, z);
//			break;
		}
		return true;
		
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata){
		//TODO TE here
		switch(metadata){
		case 0: return new TileEntityModificationTable();
		case 1: return new TileEntityModdedTable();
//		case 0: return new TileEntityCraftingTable();
//		case 1: return new TileEntityProjectBench();
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
		icons[0] = registrar.registerIcon("craftingsuite:pbblock0");
		icons[1] = registrar.registerIcon("craftingsuite:pbblock1");
		icons[2] = registrar.registerIcon("craftingsuite:pbblock2");
	}
	@Override
	public Icon getIcon(int side, int meta) {
		switch(meta){
		case 1:
			switch(side){
				case 0: return icons[2];
				case 1: return icons[1];
				default:return icons[0];
			}
		case 2:
			return Block.bedrock.getIcon(0, 0);
		default: return Block.planks.getIcon(0, 0);
		}
	}
	
	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List tabList) {
		if(tab != null)
			System.out.println();
		tabList.add(new ItemStack(blockID, 1, 0));
		tabList.add(ModificationStackHelper.makeBasicMkICrafter());
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
		return -1;
	}
}
