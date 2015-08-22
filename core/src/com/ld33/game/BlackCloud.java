package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class BlackCloud extends GameObject {
	private long nextSpawnTick;
	final int LEFT_BOUND = -64/2;
	final int RIGHT_BOUND = LD33Game.GAME_WIDTH+128/2;
	BlackCloud(float xVel){
		if(xVel<0){
			x = RIGHT_BOUND;
		}else{
			x = LEFT_BOUND;
		}
		this.xVel = xVel;
		y = LD33Game.GAME_HEIGHT-32;
		w = 128; h = 64;
		nextSpawnTick = TimeUtils.millis()+5000+(int)(Math.random()*5000);
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(TimeUtils.millis()>=nextSpawnTick){
			nextSpawnTick = TimeUtils.millis()+5000+(int)(Math.random()*5000);
			LD33Game.instance.objectList.add(new Thunder(x, y));
		}
		if(x<LEFT_BOUND||x>RIGHT_BOUND){
			LD33Game.instance.objectList.remove(this);
		}
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			0*64, 4*64, 64, 64, false, false);
	}
}
