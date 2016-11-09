package com.mygdx.myri.shaders;

import com.bitfire.postprocessing.filters.Filter;
import com.bitfire.utils.ShaderLoader;

public class PixelateFilter extends Filter<PixelateFilter>{
	
	public PixelateFilter(){
		super(ShaderLoader.fromFile("screenspace", "pixelate"));
	}

	@Override
	public void rebind(){
		
	}

	@Override
	protected void onBeforeRender(){
		inputTexture.bind(u_texture0);
	}
}
