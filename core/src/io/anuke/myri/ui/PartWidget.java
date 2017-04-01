package io.anuke.myri.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;

public class PartWidget extends VisTable{
	public PartTexture texture;
	public Vector2 origin = new Vector2();
	public String name, parentname;
	public boolean rotated, under, edit;
	 
	public PartWidget(){
		bottom().left();
		
		texture = new PartTexture(this, "body");
		
		add(texture).align(Align.bottomLeft).colspan(3);
		
		setShown(false);
	}
	
	public void draw(Batch batch, float alpha){
		super.draw(batch, alpha);
		
		if(!texture.outline) return;
	}
	
	public void updateTexture(){
		texture.setTexture(name);
	}
	
	public void setShown(boolean shown){
		for(Actor child : getChildren()){
			if( !(child instanceof PartTexture)) child.setVisible(shown);
		}

		texture.outline = shown;
	}
	
	public void moved(float x, float y){
		moveBy(x,y);
		setPosition((int)(getX()/10)*10, (int)(getY()/10)*10);
	}
}
