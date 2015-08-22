package com.ld33.game;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LD33Game extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture img;
	BitmapFont bitmapFont;
	Vector<GameObject> objectList;
	private Texture sprite;
	Texture winScene, loseScene, currentScene;
	private Viewport viewport;
	private Camera camera;

	Bird bird;
	Boss boss;
	PoliceCar policeCar;
	ParticleEffectPool poopEffectPool;
	ParticleEffectPool poopedEffectPool;
	Array<PooledEffect> effects;

	Button attackButton, rateButton, flySpeedButton, spawnRateButton, moneyButton;

	int money ;
	float damage;
	int averageSpawnDuration;
	float bossSpawnProbability;
	int moneyDelta;
	long nextVictimSpawnTick;
	long nextBlackCloudSpawnTick;
	long nextWhiteCloudSpawnTick;
	long centerTextDisappearTick; //Long.MAX_VALUE means click to disappear
	boolean policeWarningTriggered;
	long nextPoliceSpawnTick;
	private String centerTextString;
	long endGameTick;

	public static final int GAME_WIDTH = 960;
	public static final int GAME_HEIGHT = 500;
	private static final float MIN_VICTIM_SPEED = 50f;
	private static final float MAX_VICTIM_SPEED = 100f;
	private static final float AVERAGE_WHITE_CLOUD_SPAWN_DURATION = 2500;
	private static final float AVERAGE_BLACK_CLOUD_SPAWN_DURATION = 10000;
	private static final float MIN_CLOUD_SPEED = 25f;
	private static final float MAX_CLOUD_SPEED = 200f;
	private static final float AVERAGE_POLICE_SPAWN_DURATION = 100000;
	private static final float MIN_POLICE_SPEED = 100f;
	private static final float MAX_POLICE_SPEED = 250f;
	private static final float POLICE_WARNING_OFFSET = 5000;
	private static final long TEXT_FADEOUT_DURATION = 1000;
	private static final long GAME_END_SCENE_CROSSFACE_DURATION = 5000;
	
	public static LD33Game instance;

	@Override
	public void create () {
		instance = this;
		objectList = new Vector<GameObject>();
		money = 0;
		damage = 1f;
		averageSpawnDuration = 10000;
		bossSpawnProbability = 0.25f;
		moneyDelta = 1;
		nextBlackCloudSpawnTick = Long.MIN_VALUE;
		centerTextDisappearTick = Long.MAX_VALUE;
		policeWarningTriggered = false;
		nextPoliceSpawnTick = Long.MIN_VALUE;
		centerTextString = "Poopie the Flying Monster\n\n Click to Play";
		endGameTick = Long.MAX_VALUE;
		currentScene=null;
		boss = null;
		policeCar = null;
		effects = new Array<PooledEffect>();

	    camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
	    camera.position.set(GAME_WIDTH/2, GAME_HEIGHT/2, 0);
	    camera.update();
	    viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
	    sprite = new Texture("sprite.png");
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		winScene = new Texture("win.png");
		loseScene = new Texture("lose.png");
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
		spawnBlackCloud();
		spawnWhiteCloud();
	}

	@Override
	public void render () {
		float deltaTime = Gdx.graphics.getDeltaTime();
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		touchPos = camera.unproject(touchPos);
		Vector<GameObject> objectListClone = new Vector<GameObject>(objectList);

		//Do click handling
		if(currentScene==null){
			if(Gdx.input.justTouched()){
				if(policeCar!=null){
					centerTextString = "Oh... Police found you being a monster...";
					policeCar.activate();
				}
				if(nextPoliceSpawnTick==Long.MIN_VALUE)
					spawnPoliceCar();
				if(centerTextDisappearTick==Long.MAX_VALUE)
					centerTextDisappearTick = TimeUtils.millis();
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
			if(TimeUtils.millis()>nextBlackCloudSpawnTick)
				spawnBlackCloud();
			if(TimeUtils.millis()>nextWhiteCloudSpawnTick)
				spawnWhiteCloud();
			if(!policeWarningTriggered&&nextPoliceSpawnTick!=Long.MIN_VALUE&&TimeUtils.millis()+POLICE_WARNING_OFFSET>nextPoliceSpawnTick){
				policeWarningTriggered = true;
				centerTextString = "POLICE!\nSTOP POOPING!";
				LD33Game.instance.centerTextDisappearTick = Long.MAX_VALUE-1;
				//TODO: play warning music here
			}
			if(TimeUtils.millis()>nextPoliceSpawnTick)
				spawnPoliceCar();
	
			//update the objects
			for(GameObject i:objectListClone)
				i.onStep(deltaTime, (int)touchPos.x, (int)touchPos.y);
	
			//collision detection
			for(int i=0; i<objectListClone.size(); i++){
				for(int j=i+1; j<objectListClone.size(); j++){
					GameObject a = objectListClone.get(i);
					GameObject b = objectListClone.get(j);
					if(b instanceof Poop && a instanceof Victim
						||b instanceof Bird && a instanceof Thunder
						||b instanceof Bird && a instanceof PoliceCar){
						GameObject c = a;
						a = b;
						b = c;
					}
					if(a instanceof Poop && b instanceof Victim){
						if(((Victim)b).hp<=0)
							continue;
						if(new Rectangle(b.x-b.w/2, b.y-b.h/2, b.w, b.h).contains(a.x, a.y)){
							((Victim)b).hit(damage);
							((Poop)a).effect.setDuration(0);
							objectList.remove(a);
						}
					}else if(a instanceof Bird && b instanceof Thunder){
						if(new Rectangle(a.x-a.w/2, a.y-a.h/2, a.w, a.h)
							.overlaps(new Rectangle(b.x-b.w/2, b.y-b.h/2, b.w, b.h))){
							objectList.remove(b);
							if(money<=1)
								money = 0;
							else
								money /= 2;
							//TODO: show money lost eyes candy
						}
					}else if(a instanceof Bird && b instanceof PoliceCar){
						if(Math.sqrt((b.x-a.x)*(b.x-a.x)+(b.y-a.y)*(b.y-a.y))<=64){
							currentScene = loseScene;
							endGameTick = TimeUtils.millis();
						}
					}
				}
			}
		}else{
			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
				create(); //restart the game
				return;
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

		//render money string
		bitmapFont.getData().setScale(1f);
	    LD33Game.instance.bitmapFont.draw(batch, "$"+Integer.toString(money), 0, GAME_HEIGHT);
	    //render menu string
	    if(TimeUtils.millis()-TEXT_FADEOUT_DURATION<centerTextDisappearTick){
	    	if(TimeUtils.millis()>centerTextDisappearTick)
	    		LD33Game.instance.bitmapFont.setColor(1, 1, 1, 1f-(float)(TimeUtils.millis()-centerTextDisappearTick)/TEXT_FADEOUT_DURATION);
	    	LD33Game.instance.bitmapFont.draw(batch, centerTextString,
	    			0, 400, GAME_WIDTH, Align.center, false);
	    	LD33Game.instance.bitmapFont.setColor(1, 1, 1, 1);
	    }
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

		if(currentScene!=null){
			batch.setColor(1, 1, 1, (float)Math.min(1, (double)(TimeUtils.millis()-endGameTick)/(double)GAME_END_SCENE_CROSSFACE_DURATION));
			batch.begin();
			batch.draw(currentScene, 0, 0);
			batch.end();
		}
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

	private void spawnWhiteCloud(){
		if(nextWhiteCloudSpawnTick!=Long.MIN_VALUE){
			objectList.add(new Cloud(
				(float)(
					(Math.random()<0.5?1:-1)
					*(MIN_CLOUD_SPEED+Math.random()*(MAX_CLOUD_SPEED-MIN_CLOUD_SPEED))
				)
			));
		}
		nextWhiteCloudSpawnTick = TimeUtils.millis()+(long)(AVERAGE_WHITE_CLOUD_SPAWN_DURATION/2+Math.random()*AVERAGE_WHITE_CLOUD_SPAWN_DURATION);
	}

	private void spawnBlackCloud(){
		if(nextBlackCloudSpawnTick!=Long.MIN_VALUE){
			objectList.add(new BlackCloud(
				(float)(
					(Math.random()<0.5?1:-1)
					*(MIN_CLOUD_SPEED+Math.random()*(MAX_CLOUD_SPEED-MIN_CLOUD_SPEED))
				)
			));
		}
		nextBlackCloudSpawnTick = TimeUtils.millis()+(long)(AVERAGE_BLACK_CLOUD_SPAWN_DURATION/2+Math.random()*AVERAGE_BLACK_CLOUD_SPAWN_DURATION);
	}

	private void spawnPoliceCar(){
		if(nextPoliceSpawnTick!=Long.MIN_VALUE){
			objectList.add(policeCar=new PoliceCar(
				(float)(
					(Math.random()<0.5?1:-1)
					*(MIN_POLICE_SPEED+Math.random()*(MAX_POLICE_SPEED-MIN_POLICE_SPEED))
				)
			));
		}
		nextPoliceSpawnTick = TimeUtils.millis()+(long)(AVERAGE_POLICE_SPAWN_DURATION/2+Math.random()*AVERAGE_POLICE_SPAWN_DURATION);
	}
}
