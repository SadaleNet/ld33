package com.ld33.game;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LD33Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	BitmapFont bitmapFont;
	Vector<GameObject> objectList = new Vector<GameObject>();
	private Texture sprite;
	private Viewport viewport;
	private Camera camera;

	private Bird bird;
	ParticleEffectPool poopEffectPool;
	Array<PooledEffect> effects = new Array();

	float money = 0f;

	public static final int GAME_WIDTH = 960;
	public static final int GAME_HEIGHT = 500;
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
		//img = new Texture("badlogic.jpg");
		bitmapFont = new BitmapFont(Gdx.files.internal("font.fnt"));

		ParticleEffect poopEffect = new ParticleEffect();
		poopEffect.load(Gdx.files.internal("poopDrop.p"), Gdx.files.internal(""));
		poopEffectPool = new ParticleEffectPool(poopEffect, 8, 128);

		objectList.add(bird=new Bird(100, 100));
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
			bird.poop();
		}

		//update the objects
		for(GameObject i:objectListClone)
			i.onStep(deltaTime, (int)touchPos.x, (int)touchPos.y);

		/*TODO: do collision detection here*/

		//do the rendering
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//bitmapFont.draw(batch, "meow", 200, 200);
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
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	    viewport.update(width, height);
	}
}
