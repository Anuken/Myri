package com.mygdx.myri.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class SoftModel{
	private final static EarClippingTriangulator triangulator = new EarClippingTriangulator();
	private String name;
	private Vector2 position = new Vector2(), origin = new Vector2(), transformedPosition = new Vector2();
	private Texture texture;
	private TextureRegion tregion;
	private float scale = 1f;
	private PolygonRegion region;
	private int vw = 20;
	private Vector2[] bones;
	private Array<SoftModel> children = new Array<SoftModel>();
	public float rotation;
	
	public SoftModel(Texture texture, int vw, int vh){
		this.texture = texture;
		this.vw = vw;
		bones = new Vector2[vw+1];
		for(int i = 0; i < bones.length; i ++) bones[i] = new Vector2();
		tregion = new TextureRegion(texture);
		resetVertices(defaultVertices());
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void updateTransformedPosition(){
		transformedPosition.set(position);
	}
	
	public Vector2 getTransformedPosition(){
		return transformedPosition;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getOrigin(){
		return origin;
	}
	
	public float getRotation(){
		return rotation;
	}
	
	public void setRotation(float rotation){
		this.rotation = rotation;
	}
	
	public void addChild(SoftModel model, float x, float y){
		model.getPosition().set(x, y);
		children.add(model);
	}
	
	public void addChild(SoftModel model){
		children.add(model);
	}
	
	public Array<SoftModel> getChildren(){
		return children;
	}
	
	public float getWidth(){
		return texture.getWidth()*scale;
	}
	
	public float getHeight(){
		return texture.getHeight()*scale;
	}
	
	public Vector2[] getBones(){
		return bones;
	}
	
	public PolygonRegion getRegion(){
		return region;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public float[] getVertices(){
		return region.getVertices();
	}
	
	public void updateBones(){
		float yw = texture.getHeight()*scale;
		float[] vertices = region.getVertices();
		Vector2 upper = new Vector2(), lower = new Vector2();
		Vector2 diff = new Vector2();
		
		for(int i = 0; i <= vw; i ++){
			Vector2 bone = bones[i];
			Vector2 last = null;
			
			if(i == 0){
				diff.set(bones[i+1]).sub(bone).scl(1);
			}else{
				last = bones[i-1];
				diff.set(bone).sub(last);
			}
			
			
			lower.set(vertices[i*2], vertices[i*2+1]);
			upper.set(vertices[vw*4-i*2+2], vertices[vw*4-i*2+1+2]);
			
			diff.setLength(yw/2);
			diff.rotate(90);
			upper.set(bone).add(diff);
			diff.rotate(-180);
			lower.set(bone).add(diff);
			
			vertices[i*2] = lower.x;
			vertices[i*2+1] = lower.y;
			vertices[vw*4-i*2+2] = upper.x;
			vertices[vw*4-i*2+1+2] = upper.y;
		}
		
		for(SoftModel child : children){
			Vector2 rorig = child.getPosition().cpy().add(child.getOrigin()).add(position.x/10f, position.y/10f); //relative origin

			float s = (rorig.x + getWidth()/2) / getWidth();
			
			Vector2 bone1 = null;
			Vector2 bone2 = null;
			
			int i = (int)(s*bones.length);
			if(rorig.x > 0){
				bone1 = bones[i];
				bone2 = bones[i+1];
			}else{
				if(i == 0){
					bone1 = bones[i];
					bone2 = bones[i+1];
				}else{
					bone1 = bones[i-1];
					bone2 = bones[i];
				}
			}
			
			Vector2 sub = child.getTransformedPosition().set(bone2).sub(bone1);
			sub.setLength(rorig.y);
			float angle = sub.angle();
			
			if(child.name.equals("tail")) child.rotation = angle;
			
			sub.rotate(child.getPosition().y < 0 ?-90 : 90);
			sub.add(rorig.x, bone2.y);
			sub.sub(child.origin);
			sub.sub(position.x * 0.1f, position.y*0.1f);
			
		}
	}
	
	private void resetVertices(float[] vertices){
		ShortArray triangles = triangulator.computeTriangles(vertices);
		region = new PolygonRegion(tregion, vertices, triangles.toArray());
		for(int i = 0; i < bones.length; i ++){
			bones[i].x = (((float)i/(vw))*texture.getWidth()*scale) - (texture.getWidth()*scale)/2;
			bones[i].y = 0;
		}
		setVerticeUVs();
	}
	
	private float[] defaultVertices(){
		FloatArray v = new FloatArray();
		float xw = texture.getWidth()*scale;
		float yw = texture.getHeight()*scale;
		
		//bottom
		for(int x = 0; x <= vw; x ++){
			v.add(((float)x/vw)*xw - xw/2);
			v.add(0 - yw/2);
		}

		
		//top
		for(int x = vw; x >= 0; x --){
			v.add(((float)x/vw)*xw - xw/2);
			v.add(yw - yw/2);
		}
				
		return v.toArray();
	}
	
	private void setVerticeUVs(){
		float[] v = region.getTextureCoords();
		
		int i = 0;
		
		//bottom
		for(int x = 0; x <= vw; x ++){
			v[i ++] = ((float)x/vw);
			v[i ++] = 0;
		}
		
		//top
		for(int x = vw; x >= 0; x --){
			v[i ++] = ((float)x/vw);
			v[i ++] = (1f);
		}

		
		//flip
		for(int r = 0; r < v.length/2; r ++){
			v[r*2+1] = 1f - v[r*2+1];
		}
		
	}
}
