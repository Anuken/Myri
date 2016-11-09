package com.mygdx.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.mygdx.myri.Myri;

import io.anuke.ucore.modules.Module;

public class Input extends Module<Myri>{
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
	}
}
