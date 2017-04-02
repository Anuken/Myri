package io.anuke.myri.io;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;

import io.anuke.myri.graphics.SoftModel;
import io.anuke.ucore.graphics.PixmapUtils;

public class Resources{
	static private Json json;
	
	public static Json json(){
		if(json == null) json = new Json();
		return json;
	}
	
	static void fixTextures(SoftModel model, boolean dispose){
		for(SoftModel child : model.getChildren()){
			fixTextures(child, dispose);
			if(child.side){
				Texture tex = child.getTexture();
				tex.getTextureData().prepare();
				Pixmap pixmap = tex.getTextureData().consumePixmap();
				Pixmap rotated = PixmapUtils.rotate(pixmap, 90);
				
				if(dispose) tex.dispose();
				
				pixmap.dispose();
				
				Texture newtex = new Texture(rotated);
				
				child.resetTexture(newtex);
			}
		}
	}
	
	//TODO make this less painful to look at
	public static SoftModel loadModel(FileHandle file, boolean dispose){
		SoftModel model = json().fromJson(ModelData.class, file).asModel();
		
		fixTextures(model, dispose);
		return model;
	}
	
	public static SoftModel loadModel(FileHandle file){
		return loadModel(file, true);
	}
	
	
}
