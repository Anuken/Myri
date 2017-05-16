package io.anuke.myri.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.ui.layout.Table;

public class Part extends Table{
	public PartTexture texture;
	public Vector2 origin = new Vector2();
	public String name = "body", parentname = "body";
	public boolean rotated, under, edit;
	 
	public Part(){
		bottom().left();
		
		texture = new PartTexture(this, name);
		
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
		for(Element child : getChildren()){
			if( !(child instanceof PartTexture)) child.setVisible(shown);
		}

		texture.outline = shown;
	}
	
	public void moved(float x, float y){
		moveBy(x,y);
		setPosition((int)(getX()/10)*10, (int)(getY()/10)*10);
	}
}
