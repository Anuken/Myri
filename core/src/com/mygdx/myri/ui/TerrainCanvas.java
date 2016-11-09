package com.mygdx.myri.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.mygdx.myri.terrain.TerrainPolygon;

import io.anuke.ucore.graphics.Textures;

public class TerrainCanvas extends Widget{
	private PolygonSpriteBatch polybatch;
	private Array<TerrainPolygon> polygons = new Array<TerrainPolygon>();
	private float dscale = 20;
	private float screenscale = 3;
	private int sVertice = 0;
	private TerrainPolygon sPoly, mPoly;
	private float clickx, clicky;
	
	public TerrainCanvas(PolygonSpriteBatch polybatch){
		this.polybatch = polybatch;
		addListener(listener);
	}
	
	public void addPolygon(){
		polygons.add(new TerrainPolygon(Textures.get("grass"), new float[]{-dscale, -dscale, dscale, -dscale, dscale, dscale, -dscale, dscale}));
	}
	
	InputListener listener = new InputListener(){
		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			
			x = x - Gdx.graphics.getWidth()/2;
			y = y - Gdx.graphics.getHeight()/2;
			x /= screenscale;
			y /= screenscale;
			
			for(TerrainPolygon poly : polygons){
				int i = 0;
				for(Vector2 vector : poly){
					if(vector.dst(x - poly.x, y - poly.y) < 5){
						sVertice = i;
						sPoly = poly;
						return true;
					}
					i++;
				}
			}
			
			for(TerrainPolygon poly : polygons){
				if(Intersector.isPointInPolygon(poly.getVertices(), 0, poly.getVertices().length, x - poly.x, y - poly.y)){
					mPoly = poly;
					return true;
				}
			}
			
			clickx = x;
			clicky = y;
			return false;
		}
		
		public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			mPoly = null;
			sPoly = null;
		}

		public void touchDragged (InputEvent event, float x, float y, int pointer) {
			
			x = x - Gdx.graphics.getWidth()/2;
			y = y - Gdx.graphics.getHeight()/2;
			x /= screenscale;
			y /= screenscale;
			
			float ox = x;
			float oy = y;
			
			x -= clickx;
			y -= clicky;
			System.out.println(clickx + " " + clicky);
			
			if(mPoly == null){
				sPoly.getVertices()[sVertice*2] += x - sPoly.x;
				sPoly.getVertices()[sVertice*2+1] += y - sPoly.y;
				sPoly.setVertices(sPoly.getVertices());
			}else{
				System.out.println("wow");
				
				mPoly.x += x;
				mPoly.y += y;
			}
			
			clickx = ox;
			clicky = oy;
		}
	};
	
	@Override
	public void draw(Batch batch, float alpha){
		batch.end();
		polybatch.setProjectionMatrix(batch.getProjectionMatrix());
		polybatch.setTransformMatrix(batch.getTransformMatrix());
		polybatch.getTransformMatrix().translate(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0);
		polybatch.getTransformMatrix().scale(screenscale, screenscale, 1);
		polybatch.begin();
		
		for(TerrainPolygon poly : polygons)
			poly.draw(polybatch);
		
		polybatch.end();
		batch.begin();
	}
}
