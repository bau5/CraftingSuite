package bau5.mods.craftingsuite.client;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.ContainerCraftingFrame;
import bau5.mods.craftingsuite.common.EntityCraftingFrame;
import bau5.mods.craftingsuite.common.tileentity.ContainerCraftingTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityCraftingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiCraftingFrame extends GuiContainer {

	private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");

    public GuiCraftingFrame(InventoryPlayer playerInventory, EntityCraftingFrame ent)
    {
        super(new ContainerCraftingFrame(playerInventory, ent));
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(I18n.getString("container.crafting"), 28, 6, 4210752);
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

}
