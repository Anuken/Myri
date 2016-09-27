package com.mygdx.myri.ui;

import io.anuke.gdxutils.graphics.Textures;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class PartWidget extends VisTable{
	public PartTexture texture;
	public VisTextField namefield;
	public Vector2 origin = new Vector2();
	public boolean rotation;
	 
	public PartWidget(){
		bottom().left();
		
		
		texture = new PartTexture(this,"body");
		
		
		//VisTextButton load = new VisTextButton("Load");
		//add(load).row();
		
		namefield = new VisTextField("body");
		namefield.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				if(Textures.get(namefield.getText())!=null){
					texture.setTexture(namefield.getText());
					pack();
					
				}
			}
		});
		VisCheckBox rotated = new VisCheckBox("Rotated");
		rotated.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				rotation = rotated.isChecked();
			}
		});
		VisImageButton up = new VisImageButton(VisUI.getSkin().getDrawable("icon-arrow-right"));
		up.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				toFront();
			}
		});
		
		VisImageButton down = new VisImageButton(VisUI.getSkin().getDrawable("icon-arrow-left"));
		down.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				toBack();
			}
		});
		
		add(down).size(namefield.getPrefHeight()).growX();
		add(up).size(namefield.getPrefHeight()).growX();		
		add(namefield).growX().row();
		add(rotated).growX().colspan(3).row();
		
		add(texture).align(Align.bottomLeft).colspan(3);
		
		setShown(false);
	}
	
	public void setShown(boolean shown){
		for(Actor child : getChildren()){
			if( !(child instanceof PartTexture)) child.setVisible(shown);
		}

		texture.outline = shown;
	}
	
	public void moved(float x, float y){
		moveBy(x,y);
		setPosition((int)(getX()/10)*10, (int)(getY()/10)*10);
	}
}
