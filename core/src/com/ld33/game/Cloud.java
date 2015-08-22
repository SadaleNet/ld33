package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cloud extends GameObject {
	final int LEFT_BOUND = -64/2;
	final int RIGHT_BOUND = LD33Game.GAME_WIDTH+128/2;

	Cloud(float xVel){
		if(xVel<0){
			x = RIGHT_BOUND;
		}else{
			x = LEFT_BOUND;
		}
		this.xVel = xVel;
		y = LD33Game.GAME_HEIGHT-32-64-(float)(Math.random()*200);
		w = 128; h = 64;
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			1*128, 4*64, 128, 64, false, false);
	}
}
