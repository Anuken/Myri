package com.mygdx.myri.shaders;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bitfire.postprocessing.PostProcessorEffect;

public class PixelateEffect extends PostProcessorEffect{
	private PixelateFilter filter;
	
	public PixelateEffect(){
		filter = new PixelateFilter();
	}
	
	@Override
	public void dispose () {
		filter.dispose();
	}

	@Override
	public void rebind () {
		filter.rebind();
	}

	@Override
	public void render(FrameBuffer src, FrameBuffer dest){
		restoreViewport(dest);
		filter.setInput(src).setOutput(dest).render();
	}

}
