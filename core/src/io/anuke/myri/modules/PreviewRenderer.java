package io.anuke.myri.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.utils.ShaderLoader;

import io.anuke.gif.GifRecorder;
import io.anuke.myri.Myri;
import io.anuke.myri.animation.WalkAnimation;
import io.anuke.myri.graphics.SoftModel;
import io.anuke.myri.graphics.SoftModelRenderer;
import io.anuke.myri.io.Resources;
import io.anuke.myri.shaders.PixelateEffect;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.scene.style.Styles;

public class PreviewRenderer extends Module<Myri>{
	public SoftModel model;
	public SpriteBatch batch = new SpriteBatch();
	public SoftModelRenderer renderer = new SoftModelRenderer();
	public PostProcessor processor;
	public GifRecorder recorder = new GifRecorder(batch);
	public boolean editMode = true;
	PixelateEffect effect;
	WalkAnimation walk = new WalkAnimation();

	public void init(){
		ShaderLoader.BasePath = "shaders/";
		processor = new PostProcessor(false, true, true);
		processor.getCombinedBuffer().buffer1.getColorBufferTexture().setFilter(TextureFilter.Nearest,
				TextureFilter.Nearest);
		processor.getCombinedBuffer().buffer2.getColorBufferTexture().setFilter(TextureFilter.Nearest,
				TextureFilter.Nearest);
		
		effect = new PixelateEffect();
		//processor.addEffect(effect);
		
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

		clearScreen(Color.BLACK);

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
			getModule(ModelEditor.class).scene.draw();

		batch.begin();
		batch.draw(Styles.styles.getRegion("white"), Gdx.graphics.getWidth() / 2 - 2,
				Gdx.graphics.getHeight() / 2 - 2, 4, 4);
		
		if(!editMode) recorder.update();
		 
		batch.end();

		processor.render();
	}

	@Override
	public void resize(int width, int height){
		
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		effect.rebind();
		//renderer.resize(width, height);
	}
}
