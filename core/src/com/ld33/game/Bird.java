package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Bird extends GameObject {
	final int GRAPHIC_INTERVAL = 50; //how long does it take to change a frame
	final int STOP_MOVEMENT_DISTANCE = 20;
	public float speed = 100f;
	Bird(float x, float y){
		this.x = x; this.y = y;
		w = 64; h = 64;
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(Math.sqrt((mouseX-x)*(mouseX-x)+(mouseY-y)*(mouseY-y))<=STOP_MOVEMENT_DISTANCE){
			xVel = 0;
			yVel = 0;
		}else{
			double angle = Math.atan2(mouseY-y, mouseX-x);
			xVel = (float) (speed*Math.cos(angle));
			yVel = (float) (speed*Math.sin(angle));
		}
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			(int)(((TimeUtils.millis()-spawnTime)/GRAPHIC_INTERVAL)%8)*64,
			0*64, 64, 64, false, false);
	}
}
