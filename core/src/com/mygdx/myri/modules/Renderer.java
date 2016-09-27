package com.mygdx.myri.modules;

import io.anuke.gdxutils.graphics.Hue;
import io.anuke.gdxutils.modules.Module;
import io.anuke.utils.MiscUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.utils.ShaderLoader;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.myri.Myri;
import com.mygdx.myri.animation.SoftModel;
import com.mygdx.myri.graphics.SoftModelRenderer;
import com.mygdx.myri.io.ModelData;

public class Renderer extends Module<Myri>{
	public SoftModel model;
	public SpriteBatch batch = new SpriteBatch();
	public SoftModelRenderer renderer = new SoftModelRenderer();
	public PostProcessor processor;
	{MiscUtils.maximizeWindow();}

	public void init(){
		ShaderLoader.BasePath = "shaders/";
		processor = new PostProcessor( false, true, true);
		processor.getCombinedBuffer().buffer1.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		processor.getCombinedBuffer().buffer2.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//PixelateEffect effect = new PixelateEffect();
		//processor.addEffect(effect);
		model = new Json().fromJson(ModelData.class, Gdx.files.local("model1.json")).asModel();
		
		//renderer.debug = false;
		
		/*
		model = new SoftModel(new Texture("textures/parts1/body.png"), 20, 1);
		SoftModel legfr = new SoftModel(new Texture("textures/parts1/legfr.png"), 1, 10);
		SoftModel legfl = new SoftModel(new Texture("textures/parts1/legfl.png"), 1, 10);
		SoftModel legb = new SoftModel(new Texture("textures/parts1/legb.png"), 1, 10);
		SoftModel head = new SoftModel(new Texture("textures/parts1/head.png"), 5, 1);
		SoftModel tail = new SoftModel(new Texture("textures/parts1/tail.png"), 4, 1);
		
		model.addChild(head, 13.5f, 10);
		model.addChild(tail, -14.5f, 8);
		
		model.addChild(legb, -7.5f, -7.5f);
		model.addChild(legfl, 11f, -6.5f);
		model.addChild(legfr, 2.5f, -5.5f);
		*/

	}

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		processor.capture();
		
		Hue.clearScreen(Color.BLACK);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int b = 0;
		for(Vector2 bone : model.getBones())
			bone.y = (float)Math.sin((b++)/5f + Gdx.graphics.getFrameId()/30f)*8f;
		

		model.updateBones();
		renderer.render(model);

		//getModule(Editor.class).stage.draw();

		batch.begin();
		batch.draw(VisUI.getSkin().getRegion("white"), Gdx.graphics.getWidth() / 2 - 2, Gdx.graphics.getHeight() / 2 - 2, 4, 4);
		batch.end();
		
		processor.render();
	}

	@Override
	public void resize(int width, int height){
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		renderer.resize(width, height);
	}
}
