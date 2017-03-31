package io.anuke.myri.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import io.anuke.ucore.graphics.ShapeUtils;

public class BoneWidget extends Actor{
	public Vector2[] bones = new Vector2[5];
	PartTexture texture;
	
	public BoneWidget(PartTexture texture){
		for(int i = 0; i < bones.length; i ++)
			bones[i] = new Vector2();
		
		this.texture = texture;
		
		addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
				return false;
			}

			public void touchUp (InputEvent event, float x, float y, int pointer, int button){
				
			}

			public void touchDragged (InputEvent event, float x, float y, int pointer){
				
			}
		});
	}
	
	public void draw(Batch batch, float alpha){
		ShapeUtils.thickness = 2;
		
		float ofx = getX() + texture.getWidth()/2 + getParent().getX();
		float ofy = getY() + texture.getHeight()/2 + getParent().getY();
		
		batch.setColor(Color.YELLOW);
		for(int i = 0; i < bones.length-1; i ++){
			ShapeUtils.line(batch, bones[i].x + ofx, bones[i].y + ofy, bones[i+1].x + ofx, bones[i+1].y + ofy);
		}
		
		float s = 6;
		
		batch.setColor(Color.RED);
		for(Vector2 v : bones){
			ShapeUtils.rect(batch, ofx+v.x-s, ofy+v.y-s, s*2, s*2, 2);
		}
		
		batch.setColor(Color.WHITE);
	}
}
