package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Thunder extends GameObject {

	public Thunder(float x, float y) {
		this.x = x;
		this.y = y;
		xVel = (float)(Math.random()*50-25);
		yVel = (float)(-20-Math.random()*100);
		w = 64; h = 64;
	}

	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(y<-h/2){
			LD33Game.instance.objectList.remove(this);
		}
	}

	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			7*64, 4*64, 128, 64, false, false);
	}
}
