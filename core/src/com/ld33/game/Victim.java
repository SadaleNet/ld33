package com.ld33.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Victim extends GameObject {
	final int GRAPHIC_INTERVAL = 50; //how long does it take to change a frame
	final int LEFT_BOUND = -64/2;
	final int RIGHT_BOUND = LD33Game.GAME_WIDTH+64/2;
	private boolean spriteFlip = false;
	public PooledEffect effect = null;

	public float hp = 3; 

	Victim(float xVel){
		if(xVel<0){
			spriteFlip = true;
			x = RIGHT_BOUND;
		}else{
			x = LEFT_BOUND;
		}
		this.xVel = xVel;
		y = 64;
		w = 64; h = 128;
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(x<LEFT_BOUND||x>RIGHT_BOUND){
			LD33Game.instance.objectList.remove(this);
			if(effect!=null)
				effect.setDuration(0);
		}
		if(effect!=null)
			effect.setPosition(x, y);
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			(int)(((TimeUtils.millis()-spawnTime)/GRAPHIC_INTERVAL)%8)*64,
			1*64, 64, 128, spriteFlip, false);
	}
	public void hit(float damage){
		if(hp<=0)
			return;
		hp -= damage;
		if(hp<=0){
			effect = LD33Game.instance.poopEffectPool.obtain();
			effect.setPosition(x, y);
			LD33Game.instance.effects.add(effect);
			LD33Game.instance.money++;
			//TODO: create eyes candy for getting money
		}
	}
}
