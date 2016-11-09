package com.mygdx.myri;

import com.mygdx.myri.modules.ModelEditor;
import com.mygdx.myri.modules.Input;
import com.mygdx.myri.modules.LevelEditor;
import com.mygdx.myri.modules.PreviewRenderer;
import com.mygdx.myri.modules.WorldRenderer;

import io.anuke.ucore.modules.ModuleController;

public class Myri extends ModuleController<Myri>{
	int type = 1;

	@Override
	public void init(){
		if(type == 0){
			addModule(ModelEditor.class);
			addModule(PreviewRenderer.class);
		}else if(type == 1){
			addModule(LevelEditor.class);
		}else{
			addModule(WorldRenderer.class);
			addModule(Input.class);
		}
	}
}
