package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.utils.ShaderLoader;

import io.anuke.gif.GifRecorder;
import io.anuke.myri.Myri;
import io.anuke.myri.animation.ModelAnimation;
import io.anuke.myri.animation.TestAnimation;
import io.anuke.myri.entities.Player;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.Resources;
import io.anuke.myri.shaders.PixelateEffect;
import io.anuke.ucore.graphics.FrameBufferMap;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.Timers;

public class WorldRenderer extends RendererModule<Myri>{
	Box2DDebugRenderer debug = new Box2DDebugRenderer();
	OrthogonalTiledMapRenderer trenderer;
	TiledMap map;
	SoftModelRenderer srenderer = new SoftModelRenderer();
	World world = new World(new Vector2(0, -200), true);
	SoftModel model;
	PostProcessor processor;
	GifRecorder recorder = new GifRecorder(batch);
	PixelateEffect effect;
	ModelAnimation animation = new TestAnimation();
	Body playerbody;
	Player player;
	SoftModel test;
	FrameBufferMap buffers = new FrameBufferMap();
	
	float accumulator;
	
	boolean pixel = true;
	

	public WorldRenderer() {
		ShaderLoader.BasePath = "shaders/";

		cameraScale = 2;
		Textures.load("textures/parts1/");

		test = new SoftModel(Textures.get("head"), 5);

		srenderer.debug = false;
		srenderer.round = false;

		model = Resources.loadModel(Gdx.files.local("model1.json"));

		buffers.add("pixel", 1440 / 4, 849 / 4);

		setupPlayer();

		{
			Body floor = body(BodyType.StaticBody, 0, -40);

			PolygonShape box = new PolygonShape();
			box.setAsBox(1000, 2);
			floor.createFixture(fixture(box, 3));
			box.dispose();
		}

		{
			Body floor = body(BodyType.StaticBody, 0, -40);

			PolygonShape box = new PolygonShape();
			box.setAsBox(1000, 2);
			floor.createFixture(fixture(box, 3));
			box.dispose();
			floor.setTransform(0, -40, 3);
		}
	}

	void setupPlayer(){
		playerbody = body(BodyType.DynamicBody, 0, 0);

		PolygonShape bodys = new PolygonShape();
		bodys.setAsBox(28, 20);

		playerbody.createFixture(fixture(bodys, 1));

		bodys.dispose();

		player = new Player(model, playerbody);
	}

	public void update(){
		if(processor == null)
			return;

		Timers.update(Gdx.graphics.getDeltaTime());

		camera.position.set(0, 0, 0);
		camera.update();

		player.update();

		processor.capture();
		clearScreen();

		Vector3 v = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		test.setPosition((v.x), (v.y));
		test.updateTransformedPosition();

		srenderer.setProjectionMatrix(camera.combined);

		if(pixel)
			buffers.begin("pixel");

		clearScreen(Color.CLEAR);
		player.render(srenderer);

		if(pixel){
			buffers.end("pixel");
			batch.begin();
			batch.draw(buffers.texture("pixel"), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
			batch.end();
		}
		//srenderer.render(test);

		//if(srenderer.debug)
		//debug.render(world, camera.combined);

		processor.render();

		stepWorld(Gdx.graphics.getDeltaTime());

		batch.begin();
		recorder.update();
		batch.end();
	}

	private void stepWorld(float deltaTime){
		// fixed time step
		// max frame time to avoid spiral of death (on slow devices)
		float step = 1 / 300f;
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while(accumulator >= step){
			world.step(step, 6, 2);
			accumulator -= step;
		}
	}

	FixtureDef fixture(Shape shape){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.2f;
		return fixtureDef;
	}

	FixtureDef fixture(Shape shape, int bits){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.2f;
		fixtureDef.filter.categoryBits = (short) bits;
		fixtureDef.filter.maskBits = (short) bits;
		return fixtureDef;
	}

	Body body(BodyType type, float x, float y){
		BodyDef def = new BodyDef();
		def.type = type;
		def.position.set(x, y);
		return world.createBody(def);
	}

	public void resize(int width, int height){
		super.resize(width, height);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		if(processor != null){
			processor.dispose();
			effect.dispose();
		}
		processor = new PostProcessor();
		effect = new PixelateEffect();

		processor.getCombinedBuffer().buffer1.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		processor.getCombinedBuffer().buffer2.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}

}
