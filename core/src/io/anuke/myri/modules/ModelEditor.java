package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.gif.GifRecorder;
import io.anuke.myri.Myri;
import io.anuke.myri.animation.ModelAnimation;
import io.anuke.myri.animation.TestAnimation;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.ModelData;
import io.anuke.myri.io.Resources;
import io.anuke.myri.ui.Part;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.CheckBox;
import io.anuke.ucore.scene.ui.TextField;
import io.anuke.ucore.util.Timers;

public class ModelEditor extends SceneModule<Myri>{
	public static ModelEditor i;

	Part selected;

	SoftModel model;
	SoftModelRenderer renderer;
	ModelAnimation anim = new TestAnimation();
	GifRecorder recorder;

	TextField namefield, parentfield;
	CheckBox rotatebox, editbox, underbox;

	public ModelEditor() {
		i = this;
		renderer = new SoftModelRenderer();
		renderer.debug = true;
		renderer.round = false;
		Textures.load("textures/parts1/");
		recorder = new GifRecorder(DrawContext.batch);
	}

	public void init(){
		
		build.begin();
		
		new table(){{
			atop();
			aright();
			
			new button("New Part", () -> {
				Part widget = new Part();
				widget.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				if(selected == null)
					selected = widget;
				scene.add(widget);
			});

			new button("Save", () -> {

				Array<Part> parts = $list(Part.class);
				ObjectMap<String, Array<Part>> map = new ObjectMap<>();

				for(Part part : parts){
					String parent = part.parentname;
					
					Array<Part> ch = null;
					
					if(map.containsKey(parent)){
						ch = map.get(parent);
					}else{
						ch = new Array<>();
						map.put(parent, ch);
					}
					
					ch.add(part);
					
				}
				
				ModelData data = getData($("mainpart"), map);
				Gdx.files.local("model1.json").writeString(Resources.json().toJson(data), false);
				
				log("File written.");
			});
		}};
		
		build.end();

		setupParts();

		try{
			ModelData data = Resources.json().fromJson(ModelData.class, Gdx.files.local("model1.json"));
			
			Part w = data.asWidget();
			w.setName("mainpart");
			scene.add(w);
			
			addWidgets(data);
			
			selected = w;
			
			log("Added widgets");

			model = Resources.loadModel(Gdx.files.local("model1.json"), false);
			model.setPosition(Gdx.graphics.getWidth() / 6, 50);
			model.updateTransformedPosition();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	void addWidgets(ModelData data){
		if(data.children == null) return;
		
		for(ModelData d : data.children){
			Part part = d.asWidget();
			part.parentname = data.name;
			scene.add(part);
			addWidgets(d);
		}
	}
	
	ModelData getData(Part part, ObjectMap<String, Array<Part>> map){
		ModelData main = new ModelData(part);
		
		if(map.containsKey(part.name)){
			main.children = new Array<ModelData>();
			for(Part other : map.get(part.name)){
				if(other != part)
				main.children.add(getData(other, map));
			}
			
			log("Children of " + part.name + ": " + main.children);
		}
		
		return main;
	}

	void setupParts(){
		
		new table(){{
			
			atop();
			aleft();
			
			new field("legb", (text) -> {
				selected.name = text;
				if(Textures.exists(text))
				selected.updateTexture();
			}){{namefield = get();}}
			.fillY();
			
			new imagebutton("icon-arrow-right", () -> {
				selected.toFront();
			});

			new imagebutton("icon-arrow-left", () -> {
				selected.toBack();
			});
			
			row();
			
			new field("body", (text) -> {
				selected.parentname = text;
			}){{parentfield = get();}}
			.fillY();
			
			new label(":Parent").colspan(2);
			
			row();
			
			new checkbox("Edit", (b) -> {
				selected.edit = b;
			}){{editbox = get();}}
			.colspan(3).left();
			
			row();

			new checkbox("Rotated", (b) -> {
				selected.rotated = b;
			}){{rotatebox = get();}}
			.colspan(3).left();
			
			row();

			new checkbox("Under", (b) -> {
				selected.under = b;
			}){{underbox = get();}}
			.colspan(3).left();
		}};

	}

	public void setSelected(Part widget){
		if(selected != null)
			selected.setShown(false);
		
		selected = widget;
		namefield.setText(widget.name);
		parentfield.setText(widget.parentname);
		editbox.setChecked(widget.edit);
		rotatebox.setChecked(widget.rotated);
		underbox.setChecked(widget.under);
	}

	public void updateBones(boolean flip, float w, float h, Vector2[] bones, String name){
		SoftModel child = name.equals("body") ? model : model.getChild(name);

		Vector2[] out = child.getBones();
		for(int i = 0; i < out.length; i++){
			Vector2 bone = bones[i];
			if(!flip){
				out[i].x = (bone.x) / (w) * child.getWidth();
				out[i].y = bone.y / h * model.getHeight();
			}else{
				out[out.length - 1 - i].y = (bone.x) / (h) * child.getWidth();
				out[out.length - 1 - i].x = -bone.y / w * child.getHeight();
			}
		}

		child.updateBones();
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
		if(Gdx.input.isKeyJustPressed(Keys.D))
			renderer.debug = !renderer.debug;

		
		Timers.update(Gdx.graphics.getDeltaTime());
		
		clearScreen(Color.BLACK);
		act();

		if(model != null){
			anim.update(model);
			model.updateAll();

			renderer.render(model);
		}

		//stage.getBatch().begin();
		//recorder.update();
		//stage.getBatch().end();
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		renderer.setSize(width / 3, height / 3);
	}
}
