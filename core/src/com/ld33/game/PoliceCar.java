package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class PoliceCar extends GameObject {
	final int GRAPHIC_INTERVAL = 50; //how long does it take to change a frame
	final int LEFT_BOUND = -128/2;
	final int RIGHT_BOUND = LD33Game.GAME_WIDTH+128/2;
	final float speed;
	boolean activated;
	PoliceCar(float xVel){
		if(xVel<0)
			x = RIGHT_BOUND;
		else
			x = LEFT_BOUND;
		this.xVel = xVel;
		speed = Math.abs(xVel);
		activated = false;
		y = 32;
		w = 128; h = 64;
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(activated){
			double angle = Math.atan2(LD33Game.instance.bird.y-y, LD33Game.instance.bird.x-x);
			xVel = (float) (speed*Math.cos(angle));
			yVel = (float) (speed*Math.sin(angle));
		}else if(x<LEFT_BOUND||x>RIGHT_BOUND){
			LD33Game.instance.objectList.remove(this);
			LD33Game.instance.centerTextDisappearTick = TimeUtils.millis();
			LD33Game.instance.policeCar = null;
			LD33Game.instance.policeWarningTriggered = false;
		}
	}
	protected void activate(){
		activated = true;
	}

	@Override
	public void render(SpriteBatch batch, Texture sprite){
		batch.draw(sprite, x-w/2, y-h/2, w/2, h/2, w, h, //texture, x, y, oriX, oriY, width, height
			1, 1, (float)Math.toDegrees(Math.atan(yVel/xVel)), //scaleX, scaleY, rotation
			(int)(((TimeUtils.millis()-spawnTime)/GRAPHIC_INTERVAL)%4)*128, //srcX
			3*64, 128, 64, xVel<0, false); //srcY, srcW, srcH, flipX, flipY
	}
}
