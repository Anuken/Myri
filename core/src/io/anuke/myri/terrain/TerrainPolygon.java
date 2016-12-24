package io.anuke.myri.terrain;

import java.awt.geom.Area;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;

import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.util.Geometry;

public class TerrainPolygon implements Iterable<Vector2>{
	static Vector2 vector = new Vector2();
	public PolygonRegion region;
	public EarClippingTriangulator tri = new EarClippingTriangulator();
	public float x, y;
	public Area area = new Area();
	
	public TerrainPolygon(Texture tex, float[] vertices){
		ShortArray shorts = tri.computeTriangles(vertices);
		region = new PolygonRegion(new TextureRegion(tex), vertices, shorts.toArray());
		
		area.add(new Area(Geometry.polygon(vertices)));
		
	}
	
	private void setVertices(float[] vertices){
		ShortArray shorts = tri.computeTriangles(vertices);
		region = new PolygonRegion(new TextureRegion(this.region.getRegion()), vertices, shorts.toArray());
	}
	
	public void setRegion(TextureRegion region){
		this.region = new PolygonRegion(new TextureRegion(region), this.region.getVertices(), this.region.getTriangles());
	}
	
	public void draw(PolygonSpriteBatch batch){
		batch.setColor(Color.WHITE);
		batch.draw(region, x, y);
		
		batch.setColor(Color.YELLOW);
		ShapeUtils.polygon(batch, region.getVertices(), x, y, 1);
	}
	
	public void update(){
		setVertices(Geometry.array(area.getPathIterator(null)));
	}
	
	public float[] getVertices(){
		return region.getVertices();
	}

	@Override
	public Iterator<Vector2> iterator(){
		return new Iterator<Vector2>(){
			int index = 0;
			@Override
			public boolean hasNext(){
				return index < region.getVertices().length/2;
			}

			@Override
			public Vector2 next(){
				int li = index++;
				return vector.set(region.getVertices()[li*2], region.getVertices()[li*2+1]);
			}
			
		};
	}
}
