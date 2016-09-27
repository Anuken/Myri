package com.mygdx.myri.io;

import io.anuke.gdxutils.graphics.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.myri.animation.SoftModel;
import com.mygdx.myri.ui.PartWidget;

public class ModelData{
	public String name;
	public Vector2 position, origin;
	public Array<ModelData> children;
	public int vertices = 10;

	public ModelData(){

	}

	public SoftModel asModel(){
		Texture texture = Textures.get(name);
		SoftModel model = new SoftModel(texture, vertices, 1);
		model.setName(name);
		Vector2 v = position;
		v.scl(0.1f).sub(0, 10);
		//v.x = (int)(v.x+0.5f);
		//v.y = (int)v.y;
		model.getPosition().set(v);
		if(origin != null)
		model.getOrigin().set(origin).scl(0.1f);

		if(children != null){
			for(ModelData data : children){
				SoftModel child = data.asModel();
				child.getPosition().y += 10;
				child.updateTransformedPosition();
				model.addChild(child);
			}
		}
		model.updateTransformedPosition();
		return model;
	}

	public PartWidget asWidget(){
		PartWidget widget = new PartWidget();
		widget.field.setText(name);
		widget.field.fire(new ChangeListener.ChangeEvent());
		if(origin != null)
		widget.origin.set(origin);
		widget.setPosition(Gdx.graphics.getWidth() / 2 + position.x - widget.texture.texture.getWidth()*5, Gdx.graphics.getHeight() / 2 + position.y - widget.texture.texture.getHeight()*5);
		widget.moved(0, 0);
		return widget;
	}
}
