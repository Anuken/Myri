package com.mygdx.myri.animation;

import com.badlogic.gdx.Gdx;

public abstract class ModelAnimation{
	public float time;
	
	public void update(){
		time += Gdx.graphics.getDeltaTime()*60f;
	}
	
	public abstract void animate(SoftModel model);
}
