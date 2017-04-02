package io.anuke.myri.animation;

import static com.badlogic.gdx.math.MathUtils.*;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.myri.graphics.SoftModel;

public class WalkAnimation extends ModelAnimation{
	int i = 0;
	float speed = 1f;
	float tailwag = 1f;
	float bodymove = 1f;
	float legmove = 1f;
	float legraise = 1.5f;
	float earmove = 1f;

	@Override
	public void animate(SoftModel model){
		i = 0;

		float t = time / 10f * speed;

		model.forEachChild("leg", (leg) -> {

			float step = ((i == 2 || i == 1) ? 0 : 10) + i * 3;

			int index = 0;
			float b = 0;
			for(Vector2 bone : leg.getBones()){
				index++;

				float si = (b += 2.1f) / 10f + t + step;
				bone.y = (0.1f + sin(si)) * b / 2f * legmove;

				if(index == leg.getBones().length){
					bone.y = MathUtils.lerp(bone.y, leg.getBones()[index - 2].y, Math.abs(si % PI2 - PI) / (PI * 0.6f));//leg.getBones()[index-2].y;
				}

				bone.x = leg.defaultX(index - 1) + sin(t + step) * legraise;
			}

			i++;
		});

		SoftModel tail = model.getChild("tail");
		int b = 0;
		for(int i = tail.getBones().length - 1; i >= 0; i--){
			tail.getBones()[i].y = (sin((b += 1f) / 10f + t)) * b / 3f * tailwag;
		}

		SoftModel body = model;

		b = 0;
		for(Vector2 bone : body.getBones()){
			bone.y = (sin((b += 2f) / 10f + t)) * b / 9f * bodymove;
		}
		
		SoftModel ear1 = model.findChild("earf");
		b = ear1.getBones().length-1;
		for(Vector2 bone : ear1.getBones()){
			bone.y = sin(t)*b/5f*earmove;
			b --;
		}
		
		SoftModel ear2 = model.findChild("earb");
		b = ear1.getBones().length-2;
		for(Vector2 bone : ear2.getBones()){
			bone.y = -sin(t)*b/5f*earmove;
			b --;
		}

		model.updateTransformedPosition();
		model.updateBonesRecursive();
	}
}
