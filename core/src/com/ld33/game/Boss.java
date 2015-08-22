package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Boss extends Victim {
	Boss(float xVel) {
		super(xVel);
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			(int)(((TimeUtils.millis()-spawnTime)/GRAPHIC_INTERVAL)%8)*64,
			6*64, 64, 128, spriteFlip, false);
	}
	public void hit(float damage){
		super.hit(damage);
		if(hp<=0){
			//TODO: do the win logic
		}
	}

	public float getFullHp() {
		return 1500; //The max damage dealt on the victim is about 1800
	}
}
