package io.anuke.myri.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.anuke.ucore.util.Geometry;

public class SoftModelRenderer{
	public boolean debug = true, round = true;
	private PolygonSpriteBatch polybatch = new PolygonSpriteBatch();
	private ShapeRenderer shape = new ShapeRenderer();
	private Vector2 vector = new Vector2();

	public SoftModelRenderer(){
		shape.setAutoShapeType(true);
	}

	public void render(SoftModel model){
		polybatch.begin();
		renderModel(model, null);
		polybatch.end();
		
		if(debug){
			shape.begin(ShapeType.Line);
			renderDebug(model, null);
			shape.end();
		}
	}

	private void renderDebug(SoftModel model, SoftModel parent){
		float scale = model.getScale();
		
		Gdx.gl.glLineWidth(0.001f);
		
		if(parent != null){
			shape.getTransformMatrix().setToTranslation(model.getTransformedPosition().x * scale + parent.getPosition().x, model.getTransformedPosition().y * scale + parent.getPosition().y, 0);
		
		}else{
			shape.getTransformMatrix().setToTranslation(model.getPosition().x, model.getPosition().y - scale*2, 0);
		}

		shape.getTransformMatrix().scale(scale, scale, 1f);
		//shape.getTransformMatrix().rotate(new Vector3(1,0,0), model.side ? -90  : - 0);
		shape.updateMatrices();

		shape.setColor(Color.YELLOW);

		//draw vertices
		Geometry.iteratePolySegments(model.getVertices(), (x,y,x2,y2)->{
			
			if(!model.side){
				shape.line(x, y, x2, y2);
			}else{
				shape.line(y, -x, y2, -x2);
			}
		});
		
		shape.setColor(Color.PURPLE);

		//draw bones
		for(int i = 0;i < model.getBones().length - 1;i ++){
			if(!model.side){
				shape.line(model.getBones()[i], model.getBones()[i + 1]);
			}else{
				shape.line(model.getBones()[i].y, -model.getBones()[i].x, model.getBones()[i+1].y, -model.getBones()[i+1].x);
			}
		}

		shape.set(ShapeType.Filled);

		//draw vertice points
		shape.setColor(Color.RED);

		for(int i = 0;i < model.getVertices().length / 2;i ++){
			float x = model.getVertices()[i * 2];
			float y = model.getVertices()[i * 2 + 1];
			if(!model.side){
				shape.circle(x, y, 5 / 10f);
			}else{
				shape.circle(y, -x, 5 / 10f);
			}
		}

		//draw bone points
		shape.setColor(Color.BLUE);

		for(int i = 0;i < model.getBones().length;i ++){
			
			shape.circle(!model.side ? model.getBones()[i].x : model.getBones()[i].y, !model.side ? model.getBones()[i].y : -model.getBones()[i].x, 6 / 10f);
		}
		
		for(SoftModel child : model.getChildren()){
			renderDebug(child, model);
		}
	}

	private void renderModel(SoftModel model, SoftModel parent){
		float scale = model.getScale();
		
		polybatch.end();
		if(parent != null){
			float tx = (model.getTransformedPosition().x) * scale + parent.getPosition().x + model.getOrigin().x*scale;
			float ty = (model.getTransformedPosition().y) * scale  + parent.getPosition().y + model.getOrigin().y*scale;
			
			if(round){
				tx = (int)tx;
				ty = (int)ty;
			}
			
			//polybatch.getTransformMatrix().translate(x, y, 0);
			polybatch.getTransformMatrix().setToTranslation(tx, ty, 0);
		}else{
			polybatch.getTransformMatrix().setToTranslation(model.getPosition().x, model.getPosition().y - scale*1, 0);
		}
		
		polybatch.getTransformMatrix().scale(scale, scale, 1f);
		polybatch.getTransformMatrix().rotate(Vector3.Z, model.rotation );
		
		polybatch.begin();
		
		for(SoftModel child : model.getChildren()){
			if(child.underparent)
			renderModel(child, model);
		}
		
		if(parent == null){
			polybatch.end();
			polybatch.getTransformMatrix().setToTranslation(model.getPosition().x, model.getPosition().y - scale*1, 0);
			polybatch.getTransformMatrix().scale(scale, scale, 1f);
			polybatch.getTransformMatrix().rotate(Vector3.Z, model.rotation );
			polybatch.begin();
		}
		
		Vector2 offset = vector.set(0, 0);;
		if(parent != null) offset.set(model.getOrigin());
		
		if(model.side){
			offset.y -= 0.06f*scale;
		}
		
		if(model.side){
			polybatch.draw(model.getRegion(), -offset.x, -offset.y, 
					0,0,model.getTexture().getWidth(), model.getTexture().getHeight(),1,1, -90);
		}else{
			polybatch.draw(model.getRegion(), -offset.x, -offset.y, 
					model.getTexture().getWidth(), model.getTexture().getHeight());
		}
		
		for(SoftModel child : model.getChildren()){
			if(!child.underparent)
			renderModel(child, model);
		}
	}
	
	public void setSize(int width, int height){
		polybatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		shape.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		shape.updateMatrices();
	}
	
	public void setProjectionMatrix(Matrix4 matrix){
		polybatch.setProjectionMatrix(matrix);
		shape.setProjectionMatrix(matrix);
		shape.updateMatrices();
	}
}
