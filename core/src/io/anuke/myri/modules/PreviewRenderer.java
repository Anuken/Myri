package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.utils.ShaderLoader;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.gif.GifRecorder;
import io.anuke.myri.Myri;
import io.anuke.myri.animation.WalkAnimation;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.Resources;
import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;

public class PreviewRenderer extends Module<Myri>{
	public SoftModel model;
	public SpriteBatch batch = new SpriteBatch();
	public SoftModelRenderer renderer = new SoftModelRenderer();
	public PostProcessor processor;
	public GifRecorder recorder = new GifRecorder(batch);
	public boolean editMode = true;
	WalkAnimation walk = new WalkAnimation();

	public void init(){
		ShaderLoader.BasePath = "shaders/";
		processor = new PostProcessor(false, true, true);
		processor.getCombinedBuffer().buffer1.getColorBufferTexture().setFilter(TextureFilter.Nearest,
				TextureFilter.Nearest);
		processor.getCombinedBuffer().buffer2.getColorBufferTexture().setFilter(TextureFilter.Nearest,
				TextureFilter.Nearest);
		// PixelateEffect effect = new PixelateEffect();
		// processor.addEffect(effect);
		if(!editMode){
			try{
				model = Resources.loadModel(Gdx.files.local("model1.json"));
			}catch(Exception e){
				model = new SoftModel(Textures.get("body"), 1);
				e.printStackTrace();
			}
		}

		renderer.debug = false;
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
		processor.capture();

		UCore.clearScreen(Color.BLACK);

		//walk.update(model);

		// int b = 0;
		// for(Vector2 bone : model.getBones())
		// bone.y = (float)Math.sin((b++)/5f +
		// Gdx.graphics.getFrameId()/30f)*8f;

		if(!editMode)
			model.updateBones();
		if(!editMode)
			renderer.render(model);

		if(editMode)
			getModule(ModelEditor.class).stage.draw();

		batch.begin();
		batch.draw(VisUI.getSkin().getRegion("white"), Gdx.graphics.getWidth() / 2 - 2,
				Gdx.graphics.getHeight() / 2 - 2, 4, 4);
		
		if(!editMode) recorder.update();
		 
		batch.end();

		processor.render();
	}

	@Override
	public void resize(int width, int height){
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		//renderer.resize(width, height);
	}
}
