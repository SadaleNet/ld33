package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class GameObject {
	public float x,y,w,h,xVel,yVel,xAccel,yAccel;
	protected final long spawnTime = TimeUtils.millis();
	final public void onStep(double deltaTime, int mouseX, int mouseY){
		xVel += xAccel*deltaTime;
		yVel += yAccel*deltaTime;
		x += xVel*deltaTime;
		y += yVel*deltaTime;
		this.onStepHook(deltaTime, mouseX, mouseY);
	}
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){}
	public void render(SpriteBatch batch, Texture sprite){}
}
