package com.mygdx.myri.io;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.myri.animation.SoftModel;

import io.anuke.ucore.graphics.PixmapUtils;

public class Resources{
	static private Json json;
	
	public static Json json(){
		if(json == null) json = new Json();
		return json;
	}
	
	public static SoftModel loadModel(FileHandle file){
		SoftModel model = json().fromJson(ModelData.class, file).asModel();
		Array<SoftModel> additions = new Array<SoftModel>();
		Array<SoftModel> removals = new Array<SoftModel>();
		for(SoftModel child : model.getChildren()){
			if(!child.side)
				continue;
			removals.add(child);
			Texture tex = child.getTexture();
			tex.getTextureData().prepare();
			Pixmap pixmap = tex.getTextureData().consumePixmap();
			Pixmap rotated = PixmapUtils.rotate(pixmap, 90);
			tex.dispose();
			pixmap.dispose();

			Texture newtex = new Texture(rotated);
			SoftModel nmodel = new SoftModel(newtex, child.getV());
			nmodel.setName(child.getName());
			nmodel.getOrigin().set(child.getOrigin());
			nmodel.getPosition().set(child.getPosition());
			nmodel.updateTransformedPosition();
			nmodel.rotate = child.rotate;
			nmodel.side = child.side;
			additions.add(nmodel);
		}
		model.getChildren().removeAll(removals, true);
		model.getChildren().addAll(additions);
		return model;
	}
}
