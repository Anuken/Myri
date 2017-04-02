package io.anuke.myri.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.ui.Part;
import io.anuke.ucore.graphics.Textures;

public class ModelData{
	public String name;
	public Vector2 position, origin;
	public Array<ModelData> children;
	public int vertices = 5;
	public boolean rotated, under;

	public ModelData(){

	}
	
	public ModelData(Part p){
		name = p.name;
		origin = p.origin;
		rotated = p.rotated;
		under = p.under;
		position = new Vector2(p.getX() + p.texture.texture.getWidth()*5 - Gdx.graphics.getWidth()/2, p.getY() + p.texture.texture.getHeight()*5 - Gdx.graphics.getHeight()/2);
	}

	public SoftModel asModel(){
		Texture texture = Textures.get(name);
		SoftModel model = new SoftModel(texture, vertices);
		model.setName(name);
		model.side = rotated;
		model.underparent = under;
		Vector2 v = position;
		v.scl(0.1f);
		model.getPosition().set(v);
		if(origin != null)
		model.getOrigin().set(origin).scl(0.1f);

		if(children != null){
			for(ModelData data : children){
				SoftModel child = data.asModel();
				child.updateTransformedPosition();
				child.getPosition().sub(model.getPosition());
				model.addChild(child);
			}
		}
		model.updateTransformedPosition();
		return model;
	}

	public Part asWidget(){
		Part widget = new Part();
		widget.rotated = rotated;
		widget.under = under;
		widget.name = name;
		widget.updateTexture();
		
		if(origin != null)
		widget.origin.set(origin);
		
		widget.setPosition(Gdx.graphics.getWidth() / 2 + position.x - widget.texture.texture.getWidth()*5, Gdx.graphics.getHeight() / 2 + position.y - widget.texture.texture.getHeight()*5);
		widget.moved(0, 0);
		
		return widget;
	}
	
	public String toString(){
		return "Data [" + name + "]";
	}
}
