package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import io.anuke.myri.Myri;
import io.anuke.myri.animation.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.Resources;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;

public class WorldRenderer extends RendererModule<Myri>{
	Box2DDebugRenderer debug = new Box2DDebugRenderer();
	public OrthogonalTiledMapRenderer trenderer;
	public TiledMap map;
	public SoftModelRenderer srenderer = new SoftModelRenderer();
	public World world = new World(new Vector2(0, -10), true);
	public SoftModel model;

	public WorldRenderer(){
		maximize();
		cameraScale = 5f;
		Textures.load("textures/parts1/");
		
		model = Resources.loadModel(Gdx.files.local("model1.json"));
		
		Body floor = body(BodyType.StaticBody, 0, -100);
		
		PolygonShape box = new PolygonShape();  
		box.setAsBox(1000, 2);
		floor.createFixture(fixture(box));
		box.dispose();
	}
	
	public void update(){
		camera.position.set(0, 0, 0);
		camera.update();
		
		clearScreen();
		
		srenderer.setProjectionMatrix(camera.combined);
		
		srenderer.render(model);
		
		debug.render(world, camera.combined);
	}
	
	FixtureDef fixture(Shape shape){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		return fixtureDef;
	}
	
	Body body(BodyType type, float x, float y){
		BodyDef def = new BodyDef();
		def.type = type;
		def.position.set(x, y);
		return world.createBody(def);
	}
	
}
