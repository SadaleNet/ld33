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
			LD33Game.instance.currentScene = LD33Game.instance.winScene;
			LD33Game.instance.endGameTick = TimeUtils.millis();
			LD33Game.instance.winSound.play();
		}
	}

	public float getFullHp() {
		return 400; //The max damage dealt on the victim is about 1800
	}
}
