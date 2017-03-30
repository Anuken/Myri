package io.anuke.myri.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.ui.PartWidget;
import io.anuke.ucore.graphics.Textures;

public class ModelData{
	public String name;
	public Vector2 position, origin;
	public Array<ModelData> children;
	public int vertices = 10;
	public boolean side;

	public ModelData(){

	}
	
	public ModelData(PartWidget p){
		name = p.namefield.getText();
		origin = p.origin;
		side = p.rotation;
		position = new Vector2(p.getX() + p.texture.texture.getWidth()*5 - Gdx.graphics.getWidth()/2, p.getY() + p.texture.texture.getHeight()*5 - Gdx.graphics.getHeight()/2);
	}

	public SoftModel asModel(){
		Texture texture = Textures.get(name);
		SoftModel model = new SoftModel(texture, vertices);
		model.setName(name);
		model.side = side;
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

	public PartWidget asWidget(){
		PartWidget widget = new PartWidget(side);
		widget.namefield.setText(name);
		widget.namefield.fire(new ChangeListener.ChangeEvent());
		//widget.rotation = side;
		if(origin != null)
		widget.origin.set(origin);
		widget.setPosition(Gdx.graphics.getWidth() / 2 + position.x - widget.texture.texture.getWidth()*5, Gdx.graphics.getHeight() / 2 + position.y - widget.texture.texture.getHeight()*5);
		widget.moved(0, 0);
		return widget;
	}
}
