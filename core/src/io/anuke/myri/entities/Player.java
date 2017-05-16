package io.anuke.myri.entities;

import com.badlogic.gdx.physics.box2d.Body;

import io.anuke.myri.animation.ModelAnimation;
import io.anuke.myri.animation.WalkAnimation;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.util.Mathf;

public class Player extends Entity{
	public SoftModel model;
	public Body body;
	public ModelAnimation animation = new WalkAnimation();
	
	public Player(SoftModel model, Body body){
		this.model = model;
		this.body = body;
	}
	
	public void update(){
		model.getPosition().set(body.getPosition().x - 1, body.getPosition().y + 9);
		
		float t = (float) Math.tan(body.getAngle()) * model.getBoneSpacing();
		t = Mathf.clamp(t, -2f, 2f);
		
		for(int i = 0; i < model.getBones().length; i++){
			model.getBones()[i].y = (i - model.getBones().length/2)*t;
			
			float lengthen = t;
			
			model.getBones()[i].x = i*(float)Math.sqrt(model.getBoneSpacing()*model.getBoneSpacing()-(lengthen*lengthen)) - model.getWidth()/2;
		}
		
		animation.update(model);
		
		model.updateBones();
	}
	
	public void render(SoftModelRenderer rend){
		rend.render(model);
	}
}
