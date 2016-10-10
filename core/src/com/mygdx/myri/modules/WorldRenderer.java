package com.mygdx.myri.modules;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.myri.Myri;

import io.anuke.ucore.UCore;
import io.anuke.ucore.modules.Module;

public class WorldRenderer extends Module<Myri>{
	public OrthogonalTiledMapRenderer trenderer;
	public TiledMap map;

	public WorldRenderer(){
		UCore.maximizeWindow();
		TmxMapLoader loader = new TmxMapLoader();
		map = loader.load("maps/map1");
		trenderer = new OrthogonalTiledMapRenderer(map);
	}
	
	public void update(){
		trenderer.render();
	}
}
