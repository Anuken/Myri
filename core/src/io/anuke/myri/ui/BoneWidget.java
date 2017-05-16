package io.anuke.myri.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.scene.Element;

public class BoneWidget extends Element{
	public Vector2[] bones = new Vector2[5];
	PartTexture texture;
	
	public BoneWidget(PartTexture texture){
		for(int i = 0; i < bones.length; i ++)
			bones[i] = new Vector2();
		
		this.texture = texture;
	}
	
	public void draw(Batch batch, float alpha){
		Draw.thick(2);
		
		float ofx = getX() + texture.getWidth()/2 + getParent().getX();
		float ofy = getY() + texture.getHeight()/2 + getParent().getY();
		
		Draw.color(Color.YELLOW);
		for(int i = 0; i < bones.length-1; i ++){
			Draw.line(bones[i].x + ofx, bones[i].y + ofy, bones[i+1].x + ofx, bones[i+1].y + ofy);
		}
		
		float s = 6;
		
		batch.setColor(Color.RED);
		for(Vector2 v : bones){
			Draw.linerect(ofx+v.x-s, ofy+v.y-s, s*2, s*2, 2);
		}
		
		batch.setColor(Color.WHITE);
	}
}
