package com.mygdx.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mygdx.myri.Myri;
import com.mygdx.myri.ui.TerrainCanvas;

import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;

public class LevelEditor extends Module<Myri>{
	Stage stage = new Stage();
	VisTable table;
	TerrainCanvas canvas;
	PolygonSpriteBatch batch;

	public LevelEditor(){
		UCore.maximizeWindow();
		Textures.load("textures/");
		VisUI.load(SkinScale.X2);
		ShapeUtils.region = VisUI.getSkin().getRegion("white");
		stage.setViewport(new ScreenViewport());
		batch = new PolygonSpriteBatch();
		Gdx.input.setInputProcessor(stage);
		
		Textures.repeatWrap("grass");
		
	}
	
	@Override
	public void init(){
		table = new VisTable();
		table.setFillParent(true);
		table.center();
		canvas = new TerrainCanvas(batch);
		canvas.setFillParent(true);
		canvas.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.addActor(canvas);
		stage.addActor(table);
		
		
		
		VisTextButton addbutton = new VisTextButton("add");
		addbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				canvas.addPolygon();
			}
		});
		
		table.top().right().add(addbutton);
		
		canvas.addPolygon();
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyPressed(Keys.ESCAPE))Gdx.app.exit();

		UCore.clearScreen(Color.BLACK);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}
}
