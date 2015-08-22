package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Poop extends GameObject {
	final int POOP_VEL = -200;
	final int POOP_ACCEL = -300;
	final int FALL_OUT_BOUNDARY = -200;
	double deltaTime;
	public final PooledEffect effect;
	Poop(float x, float y){
		this.x = x; this.y = y;
		w = 16; h = 16;
		yAccel = POOP_ACCEL;
		yVel = POOP_VEL;
		effect = LD33Game.instance.poopEffectPool.obtain();
		effect.setPosition(x, y);
		LD33Game.instance.effects.add(effect);
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		this.deltaTime = deltaTime;
		effect.setPosition(x, y);
		if(y<FALL_OUT_BOUNDARY)
			LD33Game.instance.objectList.remove(this);
	}
}
