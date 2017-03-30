package io.anuke.myri.animation;

import io.anuke.myri.graphics.SoftModel;

public class WalkAnimation extends ModelAnimation{

	@Override
	public void animate(SoftModel model){
		// int b = 0;
		// for(Vector2 bone : model.getBones())
		// bone.y = (float)Math.sin((b++)/5f +time/30f)*8f;

		SoftModel leg = model.getChild("legb");

		//int b = 0;
		//for(Vector2 bone : leg.getBones()){
			//bone.y = (float) Math.sin((b++) / 10f + time / 40f*(b/10f)) * b/5f;
		//}

		leg.updateBones();
	}
}
