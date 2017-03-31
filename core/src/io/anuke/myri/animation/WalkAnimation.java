package io.anuke.myri.animation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.myri.graphics.SoftModel;

public class WalkAnimation extends ModelAnimation{
	int i = 0;

	@Override
	public void animate(SoftModel model){
		// int b = 0;
		// for(Vector2 bone : model.getBones())
		// bone.y = (float)Math.sin((b++)/5f +time/30f)*8f;
		i = 0;
		
		float t = time / 5f;
		
		model.forEachChild((leg)->{
			if(!leg.getName().contains("leg")) return;
			
			
			
			
			float step = ((i == 2 || i == 1 )? 0 : 10);
			
			int index = 0;
			float b = 0;
			for(Vector2 bone : leg.getBones()){
				index ++;
				
				float si = (b+=2.1f) / 10f + t + step;
				bone.y = (float) (0.1f+Math.sin(si)) * b/2f;
				
				if(index == leg.getBones().length){
					bone.y = leg.getBones()[index-2].y;
				}
				
				bone.x = leg.defaultX(index-1) + MathUtils.sin(si);
			}
			
			i++;
		});
		
		SoftModel body = model;
		
		int b = 0;
		for(Vector2 bone : body.getBones()){
			bone.y = (float) (Math.sin((b+=2f) / 10f + t)) * b/5f;
		}
		
		for(SoftModel m : model.getChildren()){
			m.updateBones();
		}
		
		model.updateBones();
		
	}
}
