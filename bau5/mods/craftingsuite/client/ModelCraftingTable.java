package bau5.mods.craftingsuite.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import bau5.mods.craftingsuite.common.Reference;

public class ModelCraftingTable extends ModelBase{
    ModelRenderer base;
    ModelRenderer shaft;
    ModelRenderer brace;
    ModelRenderer brace2;
    ModelRenderer brace3;
    ModelRenderer brace4;
    ModelRenderer top;
    
    public ResourceLocation modelTexture = new ResourceLocation(Reference.TEX_LOC +":" +"textures/models/CraftingTable.png");
    
	public ModelCraftingTable(){
		textureWidth = 64;
		textureHeight = 64;
		base = new ModelRenderer(this, 0, 18);
		base.addBox(-7F, 0F, -7F, 14, 2, 14);
		base.setRotationPoint(0F, 22F, 0F);
		base.setTextureSize(64, 64);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		shaft = new ModelRenderer(this, 0, 34);
		shaft.addBox(-4.5F, 0F, -4.5F, 9, 10, 9);
		shaft.setRotationPoint(0F, 12F, 0F);
		shaft.setTextureSize(64, 64);
		shaft.mirror = true;
		setRotation(shaft, 0F, 0F, 0F);
		brace = new ModelRenderer(this, 36, 34);
		brace.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		brace.setRotationPoint(-4F, 10F, -4F);
		brace.setTextureSize(64, 64);
		brace.mirror = true;
		setRotation(brace, 0F, 0F, 0F);
		brace2 = new ModelRenderer(this, 36, 34);
		brace2.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		brace2.setRotationPoint(-4F, 10F, 4F);
		brace2.setTextureSize(64, 64);
		brace2.mirror = true;
		setRotation(brace2, 0F, 0F, 0F);
		brace3 = new ModelRenderer(this, 36, 34);
		brace3.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		brace3.setRotationPoint(4F, 10F, -4F);
		brace3.setTextureSize(64, 64);
		brace3.mirror = true;
		setRotation(brace3, 0F, 0F, 0F);
		brace4 = new ModelRenderer(this, 36, 34);
		brace4.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		brace4.setRotationPoint(4F, 10F, 4F);
		brace4.setTextureSize(64, 64);
		brace4.mirror = true;
		setRotation(brace4, 0F, 0F, 0F);
		top = new ModelRenderer(this, 0, 0);
		top.addBox(-8F, 0F, -8F, 16, 2, 16);
		top.setRotationPoint(0F, 8F, 0F);
		top.setTextureSize(64, 64);
		top.mirror = true;
		setRotation(top, 0F, 0F, 0F);
	}
	
  	@Override
  	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5){
	    super.render(entity, f, f1, f2, f3, f4, f5);
	    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	    renderDefaultParts();
  	}
  	
  	public void renderDefaultParts(){
  		float f = 0.0625f;
  		base.render(0.0625f);
  		brace.render(0.0625f);
  		brace2.render(0.0625f);
  		brace3.render(0.0625f);
  		brace4.render(0.0625f);
  		top.render(0.0625f);
  	}
  	
  	public void renderCustomParts(){
  		shaft.render(0.0625f);
  	}
  
  	private void setRotation(ModelRenderer model, float x, float y, float z){
	    model.rotateAngleX = x;
	    model.rotateAngleY = y;
	    model.rotateAngleZ = z;
  	}
}
