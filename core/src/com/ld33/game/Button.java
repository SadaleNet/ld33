package com.ld33.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

abstract class Action{
	abstract void action(int level);
}
public class Button extends GameObject {
	int level=0;
	int[] costTable;
	int spriteIndex;
	Action action;
	boolean hover = false;
	Button(float x, float y, int spriteIndex, int[] costTable, Action action){
		this.x = x;
		this.y = y;
		w = 64; h = 64;
		this.spriteIndex = spriteIndex;
		this.costTable = costTable;
		this.action = action;
	}
	public void action(){
		if(level>=costTable.length)
			return;
		if(LD33Game.instance.money>=costTable[level]){
			LD33Game.instance.money -= costTable[level];
			level++;
			action.action(level);
		}
	}
	@Override
	protected void onStepHook(double deltaTime, int mouseX, int mouseY){
		if(new Rectangle(x-w/2, y-h/2, w, h).contains(mouseX, mouseY)){
			hover = true;
		}else{
			hover = false;
		}
	}
	@Override
	public void render(SpriteBatch batch, Texture sprite){
		if(!hover)
			batch.setColor(0.5f, 0.5f, 0.5f, 1);
		batch.draw(sprite, x-w/2, y-h/2, w, h,
			spriteIndex*64,
			5*64, 64, 64, false, false);
		if(!hover)
			batch.setColor(1, 1, 1, 1);
		LD33Game.instance.bitmapFont.getData().setScale(0.5f);
		if(level<costTable.length){
			LD33Game.instance.bitmapFont.draw(batch, "$"+Integer.toString(costTable[level]),
	    			x, y-8, 25, Align.right, false);
		}else{
			LD33Game.instance.bitmapFont.draw(batch, "max",
	    			x, y-8, 25, Align.right, false);
		}
	}
}
