package io.anuke.myri;

import io.anuke.myri.modules.Input;
import io.anuke.myri.modules.ModelEditor;
import io.anuke.myri.modules.WorldRenderer;
import io.anuke.ucore.modules.ModuleController;

public class Myri extends ModuleController<Myri>{
	int type = 1;

	@Override
	public void init(){
		if(type == 0){
			addModule(ModelEditor.class);
		}else{
			addModule(WorldRenderer.class);
			addModule(Input.class);
		}
	}
}
