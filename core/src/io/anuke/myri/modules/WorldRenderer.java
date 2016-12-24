package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.anuke.myri.Myri;
import io.anuke.myri.animation.ModelAnimation;
import io.anuke.myri.animation.WalkAnimation;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.Resources;
import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;

public class WorldRenderer extends RendererModule<Myri>{
	Box2DDebugRenderer debug = new Box2DDebugRenderer();
	public OrthogonalTiledMapRenderer trenderer;
	public TiledMap map;
	public SoftModelRenderer srenderer = new SoftModelRenderer();
	public World world = new World(new Vector2(0, -200), true);
	public SoftModel model;
	ModelAnimation animation = new WalkAnimation();
	Body playerbody;
	Body leg1, leg2, leg3;
	float accumulator;

	public WorldRenderer() {
		cameraScale = 2f;
		Textures.load("textures/parts1/");

		srenderer.debug = false;

		model = Resources.loadModel(Gdx.files.local("model1.json"));

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

		/*
		 * { leg1 = body(BodyType.DynamicBody, -17, -16);
		 * 
		 * PolygonShape lshape = new PolygonShape(); lshape.setAsBox(7, 13);
		 * 
		 * leg1.createFixture(fixture(lshape, 2));
		 * 
		 * lshape.dispose();
		 * 
		 * RevoluteJointDef jointDef = new RevoluteJointDef();
		 * jointDef.initialize(playerbody, leg1, new Vector2(-17, -9));
		 * 
		 * jointDef.lowerAngle = -0.5f * MathUtils.PI; // -90 degrees
		 * jointDef.upperAngle = 0.25f * MathUtils.PI; // 45 degrees
		 * 
		 * jointDef.enableLimit = true; jointDef.enableMotor = true;
		 * 
		 * jointDef.maxMotorTorque = 10.0f; jointDef.motorSpeed = 0.0f;
		 * 
		 * world.createJoint(jointDef); }
		 * 
		 * 
		 * { leg2 = body(BodyType.DynamicBody, 4, -16);
		 * 
		 * PolygonShape lshape = new PolygonShape(); lshape.setAsBox(8, 13);
		 * 
		 * leg2.createFixture(fixture(lshape, 2));
		 * 
		 * lshape.dispose();
		 * 
		 * RevoluteJointDef jointDef = new RevoluteJointDef();
		 * jointDef.initialize(playerbody, leg2, new Vector2(4, -9));
		 * 
		 * jointDef.lowerAngle = -0.5f * MathUtils.PI; // -90 degrees
		 * jointDef.upperAngle = 0.25f * MathUtils.PI; // 45 degrees
		 * 
		 * jointDef.enableLimit = true; jointDef.enableMotor = true;
		 * 
		 * jointDef.maxMotorTorque = 10.0f; jointDef.motorSpeed = 0.0f;
		 * 
		 * world.createJoint(jointDef); }
		 * 
		 * {
		 * 
		 * leg3 = body(BodyType.DynamicBody, 22, -16);
		 * 
		 * PolygonShape lshape = new PolygonShape(); lshape.setAsBox(7, 13);
		 * 
		 * leg3.createFixture(fixture(lshape, 2));
		 * 
		 * lshape.dispose();
		 * 
		 * RevoluteJointDef jointDef = new RevoluteJointDef();
		 * jointDef.initialize(playerbody, leg3, new Vector2(22, -9));
		 * 
		 * jointDef.lowerAngle = -0.5f * MathUtils.PI; // -90 degrees
		 * jointDef.upperAngle = 0.25f * MathUtils.PI; // 45 degrees
		 * 
		 * jointDef.enableLimit = true; jointDef.enableMotor = true;
		 * 
		 * jointDef.maxMotorTorque = 10.0f; jointDef.motorSpeed = 0.0f;
		 * 
		 * world.createJoint(jointDef); }
		 */
	}

	public void update(){
		camera.position.set(0, 0, 0);
		camera.update();

		model.getPosition().set(playerbody.getPosition().x - 1, playerbody.getPosition().y + 9);
		
		float t = (float) Math.tan(playerbody.getAngle()) * model.getBoneSpacing();
		t = UCore.clamp(t, -2f, 2f);
		
		for(int i = 0; i < model.getBones().length; i++){
			model.getBones()[i].y = (i - model.getBones().length/2)*t;
			//model.getBones()[i].x = i*model.getBoneSpacing() - model.getWidth()/2;
		}
		
		animation.update(model);
		
		model.updateBones();

		clearScreen();

		srenderer.setProjectionMatrix(camera.combined);

		srenderer.render(model);

		debug.render(world, camera.combined);

		stepWorld(Gdx.graphics.getDeltaTime());
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

}
