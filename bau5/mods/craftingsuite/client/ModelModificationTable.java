package bau5.mods.craftingsuite.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public class ModelModificationTable extends ModelBase{
    ModelRenderer Base1;
    ModelRenderer Base2;
    ModelRenderer Mid;
    ModelRenderer Outside1;
    ModelRenderer Outside2;
    ModelRenderer Outside3;
    ModelRenderer Outside4;
    ModelRenderer TopBase1;
    ModelRenderer Nub1;
    ModelRenderer Nub2;
    ModelRenderer Nub3;
    ModelRenderer Nub4;
    ModelRenderer Spin;
    ModelRenderer TopBase2;
  
    public ModelModificationTable()
  	{
	    textureWidth = 64;
	    textureHeight = 64;
	    textureWidth = 64;
	    textureHeight = 64;
	    
	    Base1 = new ModelRenderer(this, 0, 0);
    	Base1.addBox(0F, 0F, 0F, 16, 1, 16);
    	Base1.setRotationPoint(-8F, 23F, -8F);
    	Base1.setTextureSize(64, 64);
    	Base1.mirror = true;
    	setRotation(Base1, 0F, 0F, 0F);
    	Base2 = new ModelRenderer(this, 0, 48);
    	Base2.addBox(0F, 0F, 0F, 12, 1, 12);
    	Base2.setRotationPoint(-6F, 22F, -6F);
    	Base2.setTextureSize(64, 64);
      	Base2.mirror = true;
      	setRotation(Base2, 0F, 0F, 0F);
      	Mid = new ModelRenderer(this, 0, 34);
      	Mid.addBox(0F, 0F, 0F, 8, 6, 8);
      	Mid.setRotationPoint(-4F, 16F, -4F);
  		Mid.setTextureSize(64, 64);
  		Mid.mirror = true;
      	setRotation(Mid, 0F, 0F, 0F);
      	Outside1 = new ModelRenderer(this, 32, 38);
      	Outside1.addBox(0F, 0F, 0F, 1, 6, 4);
      	Outside1.setRotationPoint(4F, 16F, -2F);
      	Outside1.setTextureSize(64, 64);
      	Outside1.mirror = true;
      	setRotation(Outside1, 0F, 0F, 0F);
        Outside2 = new ModelRenderer(this, 42, 38);
        Outside2.addBox(0F, 0F, 0F, 1, 6, 4);
        Outside2.setRotationPoint(-2F, 16F, -4F);
        Outside2.setTextureSize(64, 64);
        Outside2.mirror = true;
        setRotation(Outside2, 0F, 1.570796F, 0F);
      	Outside3 = new ModelRenderer(this, 42, 38);
      	Outside3.addBox(0F, 0F, 0F, 1, 6, 4);
      	Outside3.setRotationPoint(-2F, 16F, 5F);
      	Outside3.setTextureSize(64, 64);
      	Outside3.mirror = true;
      	setRotation(Outside3, 0F, 1.570796F, 0F);
      	Outside4 = new ModelRenderer(this, 32, 38);
      	Outside4.addBox(0F, 0F, 0F, 1, 6, 4);
      	Outside4.setRotationPoint(-5F, 16F, -2F);
      	Outside4.setTextureSize(64, 64);
      	Outside4.mirror = true;
      	setRotation(Outside4, 0F, 0F, 0F);
      	TopBase1 = new ModelRenderer(this, 0, 17);
      	TopBase1.addBox(-5F, 0F, -5F, 10, 1, 10);
      	TopBase1.setRotationPoint(0F, 15F, 0F);
      	TopBase1.setTextureSize(64, 64);
      	TopBase1.mirror = true;
      	setRotation(TopBase1, 0F, 0F, 0F);
      	Nub1 = new ModelRenderer(this, 0, 28);
      	Nub1.addBox(0F, 0F, 0F, 1, 1, 1);
      	Nub1.setRotationPoint(-5F, 14F, 4F);
      	Nub1.setTextureSize(64, 64);
      	Nub1.mirror = true;
      	setRotation(Nub1, 0F, 0F, 0F);
      	Nub2 = new ModelRenderer(this, 4, 30);
      	Nub2.addBox(9F, 0F, 0F, 1, 1, 1);
      	Nub2.setRotationPoint(-5F, 14F, 4F);
      	Nub2.setTextureSize(64, 64);
      	Nub2.mirror = true;
      	setRotation(Nub2, 0F, 0F, 0F);
      	Nub3 = new ModelRenderer(this, 0, 30);
      	Nub3.addBox(0F, 0F, 0F, 1, 1, 1);
      	Nub3.setRotationPoint(4F, 14F, -5F);
      	Nub3.setTextureSize(64, 64);
      	Nub3.mirror = true;
      	setRotation(Nub3, 0F, 0F, 0F);
      	Nub4 = new ModelRenderer(this, 4, 28);
      	Nub4.addBox(0F, 0F, 0F, 1, 1, 1);
      	Nub4.setRotationPoint(-5F, 14F, -5F);
      	Nub4.setTextureSize(64, 64);
      	Nub4.mirror = true;
      	setRotation(Nub4, 0F, 0F, 0F);
      	Spin = new ModelRenderer(this, 32, 29);
      	Spin.addBox(-4F, 0F, -4F, 8, 1, 8);
      	Spin.setRotationPoint(0F, 14F, 0F);
      	Spin.setTextureSize(64, 64);
      	Spin.mirror = true;
      	setRotation(Spin, 0F, 0F, 0F);
      	TopBase2 = new ModelRenderer(this, 0, 17);
      	TopBase2.addBox(-5F, 0F, -5F, 10, 1, 10);
      	TopBase2.setRotationPoint(0F, 13F, 0F);
      	TopBase2.setTextureSize(64, 64);
      	TopBase2.mirror = true;
      	setRotation(TopBase2, 0F, 0F, 0F);
  	}
  
  	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  	{
	    super.render(entity, f, f1, f2, f3, f4, f5);
	    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	    Base1.render(f5);
	    Base2.render(f5);
	    Mid.render(f5);
	    Outside1.render(f5);
	    Outside2.render(f5);
	    Outside3.render(f5);
	    Outside4.render(f5);
	    TopBase1.render(f5);
	    Nub1.render(f5);
	    Nub2.render(f5);
	    Nub3.render(f5);
	    Nub4.render(f5);
	    Spin.render(f5);
	    TopBase2.render(f5);
  	}
  
  	private void setRotation(ModelRenderer model, float x, float y, float z)
  	{
	    model.rotateAngleX = x;
	    model.rotateAngleY = y;
	    model.rotateAngleZ = z;
  	}

  	@Override
  	public void setRotationAngles(float par1, float par2, float par3,
  			float par4, float par5, float par6, Entity par7Entity) {
  		super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
  	}

	public void renderAll(TileEntityModificationTable tile) {
		float f = 0.0625f;
	    Base1.render(f);
	    Base2.render(f);
	    Mid.render(f);
	    Outside1.render(f);
	    Outside2.render(f);
	    Outside3.render(f);
	    Outside4.render(f);
	    TopBase1.render(f);
	    Nub1.render(f);
	    Nub2.render(f);
	    Nub3.render(f);
	    Nub4.render(f);
	    Spin.render(f);
	    TopBase2.render(f);
	}
	
	public void rotatePiece(float f){
		Spin.rotateAngleZ = 0.5f;
	}
}
