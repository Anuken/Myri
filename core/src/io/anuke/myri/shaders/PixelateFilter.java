package io.anuke.myri.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.bitfire.postprocessing.filters.Filter;
import com.bitfire.utils.ShaderLoader;

public class PixelateFilter extends Filter<PixelateFilter>{
	
	public PixelateFilter(){
		super(ShaderLoader.fromFile("screenspace", "pixelate"));
		rebind();
	}

	@Override
	public void rebind(){
		setParams("size", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	@Override
	protected void onBeforeRender(){
		inputTexture.bind(u_texture0);
	}
}
