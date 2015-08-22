package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class MoneyEyesCandy extends GameObject{
	int VISIBLE_DURATION = 2000;
	float alpha;
	MoneyEyesCandy(float x, float y, float yVel){
		this.yVel = yVel;
		this.x = x; this.y = y;
		w = 64; h = 64;
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(TimeUtils.millis()-spawnTime>VISIBLE_DURATION){
			LD33Game.instance.objectList.remove(this);
			return;
		}
		alpha = 1f-(float)(TimeUtils.millis()-spawnTime)/VISIBLE_DURATION;
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.setColor(1, 1, 1, alpha);
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			7*64, 5*64, 64, 64, false, false);
		batch.setColor(1, 1, 1, 1);
	}
}
