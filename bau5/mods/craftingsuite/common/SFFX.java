package bau5.mods.craftingsuite.common;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class SFFX extends EntityFX {
	
	private double maxX;
	private double minX;
	private double maxZ;
	private double minZ;
	
	public SFFX(World world, double x, double y, double z){
		super(world, x, y, z);
		particleGravity = 0.02F;
		particleMaxAge  = 80;
		maxX = Math.ceil(x);
		minX = Math.floor(x);
		maxZ = Math.ceil(z);
		minZ = Math.floor(z);
	}
	
	@Override
	public void onUpdate() {
		if(!this.isDead){
			if(!onGround){
				this.motionX += rand.nextDouble() /1000 * (rand.nextDouble() > 0.5 ? -1 : 1);
				this.motionZ += rand.nextDouble() /1000 * (rand.nextDouble() > 0.5 ? -1 : 1);
			}
			if(posX > maxX){
				posX = maxX;
				if(motionX > 0)
					motionX *= -1 /10;
			}
			if(posX < minX){
				posX = minX;
				if(motionX < 0)
					motionX *= -1 /10;
			}
			if(posZ > maxZ){
				posZ = maxZ;
				if(motionZ > 0)
					motionZ *= -1 /10;
			}
			if(posZ < minZ){
				posZ = minZ;
				if(motionZ < 0)
					motionZ *= -1 /10;
			}
		}
		super.onUpdate();
	}
}
