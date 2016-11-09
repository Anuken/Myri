package io.anuke.myri;

import io.anuke.myri.modules.Input;
import io.anuke.myri.modules.LevelEditor;
import io.anuke.myri.modules.ModelEditor;
import io.anuke.myri.modules.PreviewRenderer;
import io.anuke.myri.modules.WorldRenderer;
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
