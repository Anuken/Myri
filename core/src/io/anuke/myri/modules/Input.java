package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import io.anuke.myri.Myri;
import io.anuke.ucore.modules.Module;

public class Input extends Module<Myri>{
	private MouseJoint mouseJoint = null;
	float clickrad = 0.01f;
	Body ground;
	Vector3 mouse = new Vector3();
	WorldRenderer rend;
	
	public void init(){
		rend = getModule(WorldRenderer.class);
		ground = rend.body(BodyType.StaticBody, 0, 0);
		
		Gdx.input.setInputProcessor(this);
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button){

		rend.world.QueryAABB(new QueryCallback(){
			@Override
			public boolean reportFixture(Fixture fixture){
				MouseJointDef def = new MouseJointDef();
				def.bodyA = ground;
				def.bodyB = fixture.getBody();
				def.collideConnected = true;
				def.target.set(mouse.x,  mouse.y);
				def.maxForce = 1000.0f * fixture.getBody().getMass();

				mouseJoint = (MouseJoint)rend.world.createJoint(def);
				fixture.getBody().setAwake(true);
				
				return false;
			}
		}, mouse.x - clickrad, mouse.y - clickrad, mouse.x + clickrad, mouse.y + clickrad);
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		if (mouseJoint != null) {
			rend.world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer){
		if(mouseJoint != null)
		mouseJoint.setTarget(mouseJoint.getTarget().set(mouse.x, mouse.y));
		return false;
	}
	
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		
		mouse = rend.camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		
	}
}
