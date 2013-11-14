package bau5.mods.craftingsuite.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public interface IGuiBridge {
	public void setZLevel(float f);
	public FontRenderer getFontRenderer();
	public RenderItem getItemRenderer();
	public Minecraft	getMinecraft();
}
