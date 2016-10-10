package com.mygdx.myri;

import com.mygdx.myri.modules.Editor;
import com.mygdx.myri.modules.Input;
import com.mygdx.myri.modules.Renderer;
import com.mygdx.myri.modules.WorldRenderer;

import io.anuke.ucore.modules.ModuleController;

public class Myri extends ModuleController<Myri>{
	boolean edit;

	@Override
	public void init(){
		if(edit){
			addModule(Editor.class);
			addModule(Renderer.class);
		}else{
			addModule(WorldRenderer.class);
			addModule(Input.class);
		}
	}
}
