package io.anuke.myri.ui;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.myri.terrain.TerrainPolygon;
import io.anuke.ucore.Geometry;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;
import io.anuke.utils.io.GifRecorder;

public class TerrainCanvas extends Widget{
	private PolygonSpriteBatch polybatch;
	private Array<TerrainPolygon> polygons = new Array<TerrainPolygon>();
	private float dscale = 20;
	private float screenscale = 2;
	private float[] brush = Geometry.nGon(8, 20);
	private Area brusharea = new Area(Geometry.polygon(brush));
	private float mousex, mousey;
	private TerrainPolygon selected;
	private GifRecorder recorder;
	
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
			TerrainPolygon intersect = null;
			
			for(TerrainPolygon poly : polygons){
				//if(Geometry.intersectPolygons(poly.getVertices(), brush)){
					intersect = poly;
				//}
			}
			
			if(intersect != null){
				brusharea.transform(AffineTransform.getTranslateInstance(mousex, mousey));
				
				intersect.area.add(brusharea);
				intersect.update();
				brusharea.transform(AffineTransform.getTranslateInstance(-mousex, -mousey));
				
			}else{
				
			}
			selected = intersect;
			return true;
		}
		
		public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			
		}

		public void touchDragged (InputEvent event, float x, float y, int pointer) {
			brusharea.transform(AffineTransform.getTranslateInstance(mousex, mousey));
			
			selected.area.add(brusharea);
			selected.update();
			brusharea.transform(AffineTransform.getTranslateInstance(-mousex, -mousey));
			
		}
	};
	
	@Override
	public void draw(Batch batch, float alpha){
		
		mousex = (Gdx.input.getX() - Gdx.graphics.getWidth()/2)/screenscale;
		mousey = (Gdx.graphics.getHeight() - Gdx.input.getY() - Gdx.graphics.getHeight()/2)/screenscale;
		
		if(recorder == null) recorder = new GifRecorder((SpriteBatch)batch);
		
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
		ShapeUtils.polygon(batch, brush, Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY(), screenscale);
		
		batch.end();
		batch.begin();
		recorder.update(VisUI.getSkin().getRegion("white"), Gdx.graphics.getDeltaTime());
		
	}
}
