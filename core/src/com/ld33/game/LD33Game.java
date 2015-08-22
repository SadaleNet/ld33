package com.ld33.game;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LD33Game extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture img;
	BitmapFont bitmapFont;
	Vector<GameObject> objectList = new Vector<GameObject>();
	private Texture sprite;
	private Viewport viewport;
	private Camera camera;

	private Bird bird;
	Boss boss = null;
	ParticleEffectPool poopEffectPool;
	ParticleEffectPool poopedEffectPool;
	Array<PooledEffect> effects = new Array();
	
	Button attackButton, rateButton, flySpeedButton, spawnRateButton, moneyButton;

	int money = 0;
	float damage = 1f;
	int averageSpawnDuration = 10000;
	float bossSpawnProbability = 0.25f;
	int moneyDelta = 1;
	long nextVictimSpawnTick;

	public static final int GAME_WIDTH = 960;
	public static final int GAME_HEIGHT = 500;
	private static final float MIN_VICTIM_SPEED = 50f;
	private static final float MAX_VICTIM_SPEED = 100f;
	
	public static LD33Game instance;

	@Override
	public void create () {
		instance = this;
	    camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
	    camera.position.set(GAME_WIDTH/2, GAME_HEIGHT/2, 0);
	    camera.update();
	    viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
	    sprite = new Texture("sprite.png");
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		//img = new Texture("badlogic.jpg");
		bitmapFont = new BitmapFont(Gdx.files.internal("font.fnt"));
		attackButton = new Button(32, GAME_HEIGHT-32-32, 0, new int[]{3, 10, 50}, new Action(){
			@Override
			void action(int level) {
				switch(level){
					case 1:	damage = 2; break;
					case 2:	damage = 3; break;
					case 3:	damage = 10; break;				
				}
			}
		});
		objectList.add(attackButton);
		rateButton = new Button(32, GAME_HEIGHT-32-32-64, 1, new int[]{3, 10, 50}, new Action(){
			@Override
			void action(int level) {
				switch(level){
					case 1:	bird.poopReloadDuration = 1250; break;
					case 2:	bird.poopReloadDuration = 500; break;
					case 3:	bird.poopReloadDuration = 100; break;				
				}
			}
		});
		objectList.add(rateButton);
		flySpeedButton = new Button(32, GAME_HEIGHT-32-32-64*2, 2, new int[]{3, 10, 20}, new Action(){
			@Override
			void action(int level) {
				switch(level){
					case 1:	bird.speed = 150f; break;
					case 2:	bird.speed = 200f; break;
					case 3:	bird.speed = 300f; break;				
				}
			}
		});
		objectList.add(flySpeedButton);
		spawnRateButton = new Button(32, GAME_HEIGHT-32-32-64*3, 3, new int[]{10, 50, 200}, new Action(){
			@Override
			void action(int level) {
				switch(level){
					case 1:	averageSpawnDuration = 5000; bossSpawnProbability=0f; break;
					case 2:	averageSpawnDuration = 2000; break;
					case 3:	averageSpawnDuration = 1000; bossSpawnProbability=1f; break;				
				}
			}
		});
		objectList.add(spawnRateButton);
		moneyButton = new Button(32, GAME_HEIGHT-32-32-64*4, 4, new int[]{5, 10, 20}, new Action(){
			@Override
			void action(int level) {
				switch(level){
					case 1:	moneyDelta = 2; break;
					case 2:	moneyDelta = 3; break;
					case 3:	moneyDelta = 4; break;				
				}
			}
		});
		objectList.add(moneyButton);

		ParticleEffect poopEffect = new ParticleEffect();
		poopEffect.load(Gdx.files.internal("poopDrop.p"), Gdx.files.internal(""));
		poopEffectPool = new ParticleEffectPool(poopEffect, 8, 128);

		ParticleEffect poopedEffect = new ParticleEffect();
		poopedEffect.load(Gdx.files.internal("pooped.p"), Gdx.files.internal(""));
		poopedEffectPool = new ParticleEffectPool(poopedEffect, 16, 1024);

		objectList.add(bird=new Bird(GAME_WIDTH/2, GAME_HEIGHT/2));

		spawnVictim();
	}

	@Override
	public void render () {
		float deltaTime = Gdx.graphics.getDeltaTime();
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		touchPos = camera.unproject(touchPos);
		Vector<GameObject> objectListClone = new Vector<GameObject>(objectList);

		//Do click handling
		if(Gdx.input.justTouched()){
			boolean handled = false;
			for(GameObject i:objectList){
				if(i instanceof Button){
					if(new Rectangle(i.x-i.w/2, i.y-i.h/2, i.w, i.h).contains(touchPos.x, touchPos.y)){
						((Button)i).action();
						handled = true;
						break;
					}
				}
			}
			if(!handled)
				bird.poop();
		}
		
		//spawn items
		if(TimeUtils.millis()>nextVictimSpawnTick)
			spawnVictim();

		//update the objects
		for(GameObject i:objectListClone)
			i.onStep(deltaTime, (int)touchPos.x, (int)touchPos.y);

		//collision detection
		for(int i=0; i<objectListClone.size(); i++){
			for(int j=i+1; j<objectListClone.size(); j++){
				GameObject a = objectListClone.get(i);
				GameObject b = objectListClone.get(j);
				if(b instanceof Poop && a instanceof Victim){
					GameObject c = a;
					a = b;
					b = c;
				}
				if(a instanceof Poop && b instanceof Victim){
					if(new Rectangle(b.x-b.w/2, b.y, b.w, b.h).contains(a.x, a.y)){
						((Victim)b).hit(damage);
						((Poop)a).effect.setDuration(0);
						objectList.remove(a);
					}
				}
			}
		}

		//do the rendering
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.begin();

		//rendering particles
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(batch, deltaTime);
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		    }
		}
		//render sprite
		for(GameObject i:objectList)
			i.render(batch, sprite);

		//render money
		bitmapFont.getData().setScale(1f);
	    LD33Game.instance.bitmapFont.draw(batch, "$"+Integer.toString(money), 0, GAME_HEIGHT);
		batch.end();

		//render HP bar
		shapeRenderer.begin(ShapeType.Filled);
		for(GameObject i:objectList){
			if(i instanceof Victim){
				if(((Victim)i).hp==((Victim)i).getFullHp())
					continue;
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.rect(i.x-i.w/2, i.y+80, i.w, 10);

				if(((Victim)i).hp>0){
					shapeRenderer.setColor(Color.GREEN);
					shapeRenderer.rect(i.x-i.w/2, i.y+80, i.w*((Victim)i).hp/((Victim)i).getFullHp(), 10);
				}
			}
		}
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {
	    viewport.update(width, height);
	}

	private void spawnVictim() {
		nextVictimSpawnTick = TimeUtils.millis()+(long)(averageSpawnDuration/2+Math.random()*averageSpawnDuration);
		if(boss==null&&Math.random()<bossSpawnProbability){
			objectList.add(boss=new Boss(
					(float)(
						(Math.random()<0.5?1:-1)
						*(MIN_VICTIM_SPEED+Math.random()*(MAX_VICTIM_SPEED-MIN_VICTIM_SPEED))
					)
				));
		}else{
			objectList.add(new Victim(
				(float)(
					(Math.random()<0.5?1:-1)
					*(MIN_VICTIM_SPEED+Math.random()*(MAX_VICTIM_SPEED-MIN_VICTIM_SPEED))
				)
			));
		}
	}
}
