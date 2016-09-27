package com.mygdx.myri;

import io.anuke.gdxutils.modules.ModuleController;

import com.mygdx.myri.modules.Editor;
import com.mygdx.myri.modules.Renderer;

public class Myri extends ModuleController<Myri>{

	@Override
	public void init(){
		addModule(Editor.class);
		addModule(Renderer.class);
	}
}
