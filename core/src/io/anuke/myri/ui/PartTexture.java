package io.anuke.myri.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import io.anuke.myri.modules.ModelEditor;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;

public class PartTexture extends Actor{
	public Texture texture;
	public boolean outline = true;
	public Vector2[] bones = new Vector2[6];
	
	private String texname;
	private int range = 10;
	private int selected = -1;
	private float scale = 10;
	private float lx, ly;
	private Part part;

	public PartTexture(Part part, String name) {
		this.part = part;
		
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(part.edit){
					for(int i = 0; i < bones.length; i++){
						Vector2 v = bones[i];

						if(Vector2.dst(v.x + getWidth() / 2 + getX(), v.y + getHeight() / 2 + getY(), x, y) < range){
							selected = i;
							lx = x;
							ly = y;
							break;
						}
					}
				}

				if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
					part.origin.set(x - getWidth() / 2, y - getHeight() / 2);
					return true;
				}

				lx = x;
				ly = y;
				part.toFront();

				ModelEditor.i.setSelected(part);
				part.setShown(true);

				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button){

				if(part.edit){
					selected = -1;
				}
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				if(part.edit){
					if(selected != -1){
						bones[selected].add(x-lx, y-ly);
						lx = x;
						ly = y;
						ModelEditor.i.updateBones(part.rotated, getWidth(), getHeight(), bones, texname);
					}
				}else{
					if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
						part.origin.set(x - getWidth() / 2, y - getHeight() / 2);
					}else{
						part.moved(x - lx, y - ly);
					}
				}
			}
		});

		for(int i = 0; i < bones.length; i++)
			bones[i] = new Vector2();
		
		setTexture(name);
	}

	public void setTexture(String name){
		texname = name;
		texture = Textures.get(name);
		setSize(texture.getWidth() * scale, texture.getHeight() * scale);
		
		if(!part.rotated){
			for(int i = 0; i < bones.length; i++){
				bones[i].set(-getWidth() / 2 + (float) i / (bones.length - 1) * getWidth(), 0);
			}
		}else{
			for(int i = 0; i < bones.length; i++){
				bones[i].set(0, -getHeight() / 2 + (float) i / (bones.length - 1) * getHeight());
			}
		}
	}

	public void draw(Batch batch, float alpha){
		batch.draw(texture, getX(), getY(), getWidth(), getHeight());
		batch.setColor(Color.CORAL);
		if(outline){
			ShapeUtils.rect(batch, getX(), getY(), getWidth(), getHeight(), 2);
			int s = 4;
			batch.setColor(Color.GREEN);
			ShapeUtils.rect(batch, getX() + getWidth() / 2 + part.origin.x - s, getY() + getHeight() / 2 + part.origin.y - s, s * 2, s * 2, 2);
			drawBones(batch);
		}
		batch.setColor(Color.WHITE);
	}

	void drawBones(Batch batch){
		ShapeUtils.thickness = 2;

		float ofx = getX() + getWidth() / 2;
		float ofy = getY() + getHeight() / 2;

		batch.setColor(Color.YELLOW);
		for(int i = 0; i < bones.length - 1; i++){
			ShapeUtils.line(batch, bones[i].x + ofx, bones[i].y + ofy, bones[i + 1].x + ofx, bones[i + 1].y + ofy);
		}

		float s = 6;

		int i = 0;
		for(Vector2 v : bones){
			batch.setColor(i++ == selected ? Color.CYAN : Color.RED);
			ShapeUtils.rect(batch, ofx + v.x - s, ofy + v.y - s, s * 2, s * 2, 2);
		}

		batch.setColor(Color.WHITE);
	}
}
