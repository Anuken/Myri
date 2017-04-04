package io.anuke.myri.animation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.myri.graphics.SoftModel;
import io.anuke.ucore.util.Timers;

public class TestAnimation extends ModelAnimation{

	@Override
	protected void animate(SoftModel model){
		Vector2[] bones = model.getChild("legfr").getBones();
		
		for(int i = 0; i < bones.length; i ++){
			bones[i].y = MathUtils.sin(Timers.time()*10f);
		}
		model.updateAll();
	}

}
