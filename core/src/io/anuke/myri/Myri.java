package io.anuke.myri;

import io.anuke.myri.modules.*;
import io.anuke.ucore.modules.ModuleController;

public class Myri extends ModuleController<Myri>{
	int type = 0;

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
