package io.anuke.myri.animation;

import com.badlogic.gdx.Gdx;

import io.anuke.myri.graphics.SoftModel;

public abstract class ModelAnimation{
	public float time;
	
	public void update(SoftModel model){
		time += Gdx.graphics.getDeltaTime()*60f;
		animate(model);
	}
	protected abstract void animate(SoftModel model);
}
