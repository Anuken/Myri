package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

import io.anuke.myri.Myri;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.ModelData;
import io.anuke.myri.io.Resources;
import io.anuke.myri.ui.PartWidget;
import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;

public class ModelEditor extends Module<Myri>{
	public static ModelEditor i;
	Stage stage = new Stage();
	FileChooser chooser;
	PartWidget part;
	public PartWidget selected;
	public VisCheckBox vbox;
	boolean setup = false;
	SoftModel model;
	SoftModelRenderer renderer;

	public ModelEditor(){
		i=this;
		renderer = new SoftModelRenderer();
		renderer.debug = true;
		renderer.round = false;
		Textures.load("textures/parts1/");
		VisUI.load(SkinScale.X2);
		ShapeUtils.region = VisUI.getSkin().getRegion("white");
		stage.setViewport(new ScreenViewport());
		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(this);
		plex.addProcessor(stage);
		Gdx.input.setInputProcessor(plex);
	}

	public void setup(){
		setup = true;

		FileChooser.setDefaultPrefsName("myri");
		chooser = new FileChooser(Mode.OPEN);

		VisTable table = new VisTable();
		table.setFillParent(true);
		stage.addActor(table);

		VisTextButton newbutton = new VisTextButton("New Part");
		newbutton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				PartWidget widget = new PartWidget(false);
				widget.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				if(part == null) part = widget;
				stage.addActor(widget);
			}
		});

		vbox = new VisCheckBox("Visible", true);
		vbox.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor a){
				for(Actor actor : stage.getActors()){
					if(actor instanceof PartWidget){
						PartWidget p = (PartWidget)actor;
						p.setShown(vbox.isChecked());
					}
				}
			}
		});
		
		vbox.setChecked(false);
		
		VisTextButton save = new VisTextButton("Save");
		save.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actora){
				if(part == null) return;
				System.out.println("File written.");
				Array<ModelData> children = new Array<ModelData>();
				ModelData main = new ModelData(part);
				for(Actor actor : stage.getActors()){
					if(!(actor instanceof PartWidget)) continue;
				
					PartWidget p = (PartWidget)actor;
					ModelData data = null;
					if(p != part){
						data = new ModelData(p);
						children.add(data);
					}else{
						data = main;
						data.children = children;
					}
				}
				String string = Resources.json().toJson(main);
				Gdx.files.local("model1.json").writeString(string, false);
			}

		});

		table.top().right().add(vbox);
		table.add(newbutton);
		table.add(save);

		try{
			ModelData data = Resources.json().fromJson(ModelData.class, Gdx.files.local("model1.json"));
			PartWidget w = data.asWidget();
			stage.addActor(w);
			for(ModelData d : data.children)
				stage.addActor(d.asWidget());
			part = w;
			
			model = Resources.loadModel(Gdx.files.local("model1.json"), false);
			model.setPosition(Gdx.graphics.getWidth()/6, 30);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void updateBones(boolean flip, float w, float h, Vector2[] bones, String name){
		SoftModel child = name.equals("body") ? model : model.getChild(name);
		
		Vector2[] out = child.getBones();
		log(out.length);
		for(int i = 0; i < out.length; i ++){
			Vector2 bone = bones[i];
			if(!flip){
				out[i].x = (bone.x)/(w)*child.getWidth();
				out[i].y = bone.y/h*model.getHeight();
			}else{
				out[out.length-1-i].y = (bone.x)/(h)*child.getWidth();
				//out[i].x = bone.y/h*model.getHeight();
			}
		}
		
		child.updateBones();
	}
	
	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		if(Gdx.graphics.getFrameId() == 2 && !setup) setup();
		
		UCore.clearScreen(Color.BLACK);
		stage.draw();
		
		stage.act(Gdx.graphics.getDeltaTime());
		
		if(model != null)
		renderer.render(model);
	}

	@Override
	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
		renderer.setSize(width/3, height/3);
	}
}
