package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.*;

import io.anuke.gif.GifRecorder;
import io.anuke.myri.Myri;
import io.anuke.myri.animation.ModelAnimation;
import io.anuke.myri.animation.WalkAnimation;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.ModelData;
import io.anuke.myri.io.Resources;
import io.anuke.myri.ui.PartWidget;
import io.anuke.scene.Layout;
import io.anuke.scene.SceneModule;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;

public class ModelEditor extends SceneModule<Myri>{
	public static ModelEditor i;
	boolean setup = false;
	
	Stage stage;
	PartWidget selected;
	
	SoftModel model;
	SoftModelRenderer renderer;
	ModelAnimation anim = new WalkAnimation();
	GifRecorder recorder;
	
	VisTextField namefield, parentfield;
	VisCheckBox rotatebox, editbox, underbox;
	Layout wlayout;

	public ModelEditor(){
		i=this;
		stage = new Stage();
		renderer = new SoftModelRenderer();
		renderer.debug = true;
		renderer.round = false;
		Textures.load("textures/parts1/");
		VisUI.load(SkinScale.X2);
		ShapeUtils.region = VisUI.getSkin().getRegion("white");
		stage.setViewport(new ScreenViewport());
		recorder = new GifRecorder((SpriteBatch)stage.getBatch());
		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(this);
		plex.addProcessor(stage);
		Gdx.input.setInputProcessor(plex);
	}

	public void setup(){
		setup = true;

		VisTable table = new VisTable();
		table.setFillParent(true);
		stage.addActor(table);

		VisTextButton newbutton = new VisTextButton("New Part");
		newbutton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				PartWidget widget = new PartWidget();
				widget.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				if(selected == null) selected = widget;
				stage.addActor(widget);
			}
		});
		
		VisTextButton save = new VisTextButton("Save");
		save.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actora){
				if(selected == null) return;
				
				Array<ModelData> children = new Array<ModelData>();
				ModelData main = new ModelData(selected);
				for(Actor actor : stage.getActors()){
					if(!(actor instanceof PartWidget)) continue;
				
					PartWidget p = (PartWidget)actor;
					ModelData data = null;
					if(p != selected){
						data = new ModelData(p);
						children.add(data);
					}else{
						data = main;
						data.children = children;
					}
				}
				String string = Resources.json().toJson(main);
				Gdx.files.local("model1.json").writeString(string, false);
				log("File written.");
			}

		});

		table.top().right();
		table.add(newbutton);
		table.add(save);
		
		setupParts();

		try{
			ModelData data = Resources.json().fromJson(ModelData.class, Gdx.files.local("model1.json"));
			PartWidget w = data.asWidget();
			stage.addActor(w);
			for(ModelData d : data.children)
				stage.addActor(d.asWidget());
			selected = w;
			
			model = Resources.loadModel(Gdx.files.local("model1.json"), false);
			model.setPosition(Gdx.graphics.getWidth()/6, 50);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	void setupParts(){
		Layout l = fill();
		l.touch(Touchable.disabled);
		
		wlayout = l;
		
		namefield = l.field("legb", (text)->{
			selected.name = text;
		});
		
		parentfield = l.field("body", (text)->{
			selected.parentname = text;
		});
		
		rotatebox = l.check("Rotated", (b)->{
			selected.rotated = b;
		});
		
		editbox = l.check("Edit", (b)->{
			selected.edit = b;
		});
		
		underbox = l.check("Under", (b)->{
			selected.under = b;
		});
		
		VisImageButton down = l.ibutton("icon-arrow-right", ()->{
			selected.toFront();
		});
		
		VisImageButton up = l.ibutton("icon-arrow-left", ()->{
			selected.toBack();
		});
		
		l.top().left();
		
		l.$(namefield).fillY();
		l.$(down);
		l.$(up);
		l.row();
		l.$(parentfield).fillY();
		l.$text(":Parent").colspan(2).row();
		
		l.$(editbox).colspan(3).align(Align.left);
		l.row();
		l.$(underbox).colspan(3).align(Align.left);
		l.row();
		l.$(rotatebox).colspan(3).align(Align.left);
	}
	
	public void setSelected(PartWidget widget){
		if(selected != null) selected.setShown(false);
		selected = widget;
		namefield.setText(widget.name);
		parentfield.setText(widget.parentname);
		editbox.setChecked(widget.edit);
		rotatebox.setChecked(widget.rotated);
		underbox.setChecked(widget.under);
		wlayout.touch(Touchable.enabled);
	}
	
	public void updateBones(boolean flip, float w, float h, Vector2[] bones, String name){
		SoftModel child = name.equals("body") ? model : model.getChild(name);
		
		Vector2[] out = child.getBones();
		for(int i = 0; i < out.length; i ++){
			Vector2 bone = bones[i];
			if(!flip){
				out[i].x = (bone.x)/(w)*child.getWidth();
				out[i].y = bone.y/h*model.getHeight();
			}else{
				out[out.length-1-i].y = (bone.x)/(h)*child.getWidth();
				out[out.length-1-i].x = -bone.y/w*child.getHeight();
			}
		}
		
		child.updateBones();
	}
	
	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		if(Gdx.graphics.getFrameId() == 2 && !setup) setup();
		if(Gdx.input.isKeyJustPressed(Keys.D)) renderer.debug = !renderer.debug;
		
		clearScreen(Color.BLACK);
		act();
		
		if(model != null){
			anim.update(model);
			
			renderer.render(model);
		}
		
		stage.getBatch().begin();
		recorder.update();
		stage.getBatch().end();
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		renderer.setSize(width/3, height/3);
	}
}
