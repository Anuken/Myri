package com.mygdx.myri.modules;

import io.anuke.gdxutils.graphics.Hue;
import io.anuke.gdxutils.graphics.PixmapUtils;
import io.anuke.gdxutils.graphics.Textures;
import io.anuke.gdxutils.modules.Module;
import io.anuke.utils.MiscUtils;
import io.anuke.utils.io.GifRecorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
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
	public GifRecorder recorder = new GifRecorder(batch);
	public boolean editMode = false;
	{
		MiscUtils.maximizeWindow();
	}

	public void init(){
		ShaderLoader.BasePath = "shaders/";
		processor = new PostProcessor(false, true, true);
		processor.getCombinedBuffer().buffer1.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		processor.getCombinedBuffer().buffer2.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//PixelateEffect effect = new PixelateEffect();
		//processor.addEffect(effect);
		try{
			model = new Json().fromJson(ModelData.class, Gdx.files.local("model1.json")).asModel();
			Array<SoftModel> additions = new Array<SoftModel>();
			Array<SoftModel> removals = new Array<SoftModel>();
			for(SoftModel child : model.getChildren()){
				if(!child.rotated) continue;
				removals.add(child);
				Texture tex = child.getTexture();
				tex.getTextureData().prepare();
				Pixmap pixmap = tex.getTextureData().consumePixmap();
				Pixmap rotated = PixmapUtils.rotate(pixmap, 90);
				tex.dispose();
				pixmap.dispose();
				
				Texture newtex = new Texture(rotated);
				SoftModel nmodel = new SoftModel(newtex, child.getV());
				nmodel.setName(child.getName());
				nmodel.getOrigin().set(child.getOrigin());
				nmodel.getPosition().set(child.getPosition());
				nmodel.updateTransformedPosition();
				nmodel.rotated = true;
				additions.add(nmodel);
			}
			model.getChildren().removeAll(removals, true);
			model.getChildren().addAll(additions);
			
		}catch(Exception e){
			model = new SoftModel(Textures.get("body"), 1);
			e.printStackTrace();
		}

		renderer.debug = false;

	}

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		processor.capture();

		Hue.clearScreen(Color.BLACK);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//int b = 0;
		//for(Vector2 bone : model.getBones())
		//	bone.y = (float)Math.sin((b++)/5f + Gdx.graphics.getFrameId()/30f)*8f;

		model.updateBones();
		if( !editMode) renderer.render(model);

		if(editMode) getModule(Editor.class).stage.draw();

		batch.begin();
		batch.draw(VisUI.getSkin().getRegion("white"), Gdx.graphics.getWidth() / 2 - 2, Gdx.graphics.getHeight() / 2 - 2, 4, 4);
		if(!editMode)recorder.update(VisUI.getSkin().getRegion("white"), Gdx.graphics.getDeltaTime());
		batch.end();

		processor.render();
	}

	@Override
	public void resize(int width, int height){
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		renderer.resize(width, height);
	}
}
