package io.anuke.myri.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import io.anuke.myri.modules.ModelEditor;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;

public class PartTexture extends Actor{
	public Texture texture;
	public boolean outline = true;
	private float scale = 10;
	float lx, ly;
	private PartWidget part;
	
	public PartTexture(PartWidget part, String name){
		setTexture(name);
		this.part = part;
		addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
				if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
					part.origin.set(x - getWidth()/2,y - getHeight()/2);
					return true;
				}
				lx = x;
				ly = y;
				part.toFront();
				if(ModelEditor.i.selected != null && !ModelEditor.i.vbox.isChecked()){
					ModelEditor.i.selected.setShown(false);
				}
				
				ModelEditor.i.selected = part;
				part.setShown(true);
				
				return true;
			}

			public void touchUp (InputEvent event, float x, float y, int pointer, int button){
				
			}

			public void touchDragged (InputEvent event, float x, float y, int pointer){
				if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
					part.origin.set(x - getWidth()/2,y - getHeight()/2);
				}else{
					part.moved(x - lx,y  - ly);
				}
			}
		});
	}
	
	public void setTexture(String name){
		texture = Textures.get(name);
		setSize(texture.getWidth()*scale, texture.getHeight()*scale);
	}
	
	public void draw(Batch batch, float alpha){
		
		batch.draw(texture, getX(), getY(), getWidth(), getHeight());
		batch.setColor(Color.CORAL);
		if(outline){
			ShapeUtils.rect(batch, getX(), getY(), getWidth(), getHeight(), 2);
			int s = 4;
			batch.setColor(Color.GREEN);
			ShapeUtils.rect(batch, getX() + getWidth()/2+part.origin.x - s,getY() + getHeight()/2+part.origin.y - s, s*2, s*2, 2);
		}
		batch.setColor(Color.WHITE);
	}
}
