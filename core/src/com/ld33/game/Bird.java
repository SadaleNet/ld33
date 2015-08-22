package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Bird extends GameObject {
	//constants
	final int GRAPHIC_INTERVAL = 50; //how long does it take to change a frame
	final int STOP_MOVEMENT_DISTANCE = 20;
	final int POOP_EMIT_OFFSET_X = 16;
	final int POOP_EMIT_OFFSET_Y = 32;
	final int BOTTOM_BOUND = 250;
	final int TOP_BOUND = LD33Game.GAME_HEIGHT-64;
	final int LEFT_BOUND = 64;
	final int RIGHT_BOUND = LD33Game.GAME_WIDTH-64;

	//states
	private boolean spriteFlip = false;
	private long lastPoopReloadTick = 0;

	//upgradables
	public float speed = 100f;
	public int poopReloadDuration = 2000;
	Bird(float x, float y){
		this.x = x; this.y = y;
		w = 64; h = 64;
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(Math.sqrt((mouseX-x)*(mouseX-x)+(mouseY-y)*(mouseY-y))<=STOP_MOVEMENT_DISTANCE
			||x<LEFT_BOUND||x>RIGHT_BOUND
			||y<BOTTOM_BOUND||y>TOP_BOUND){
			x = Math.max(LEFT_BOUND, Math.min(x, RIGHT_BOUND));
			y = Math.max(BOTTOM_BOUND, Math.min(y, TOP_BOUND));
		}else{
			double angle = Math.atan2(mouseY-y, mouseX-x);
			xVel = (float) (speed*Math.cos(angle));
			yVel = (float) (speed*Math.sin(angle));
			spriteFlip = (xVel<0);
		}
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			(int)(((TimeUtils.millis()-spawnTime)/GRAPHIC_INTERVAL)%8)*64,
			0*64, 64, 64, spriteFlip, false);
	}
	public void poop() {
		if(TimeUtils.millis()-lastPoopReloadTick>poopReloadDuration){
			LD33Game.instance.objectList.add(new Poop(spriteFlip?x+POOP_EMIT_OFFSET_X:x-POOP_EMIT_OFFSET_X, y-POOP_EMIT_OFFSET_Y));
			lastPoopReloadTick = TimeUtils.millis();
		}
	}
}
