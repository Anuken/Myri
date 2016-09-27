package com.mygdx.myri.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.myri.animation.SoftModel;

public class SoftModelRenderer{
	public boolean debug = true;
	private PolygonSpriteBatch polybatch = new PolygonSpriteBatch();
	private ShapeRenderer shape = new ShapeRenderer();
	private float dx = Gdx.graphics.getWidth() / 2;
	private float dy = Gdx.graphics.getHeight() / 2;

	public SoftModelRenderer(){
		shape.setAutoShapeType(true);
	}

	public void render(SoftModel model){
		dx = Gdx.graphics.getWidth() / 2;
		dy = Gdx.graphics.getHeight() / 2;
		
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

		if(parent != null){
			shape.getTransformMatrix().setToTranslation(dx + model.getTransformedPosition().x * 10, dy + model.getTransformedPosition().y * 10, 0);
		}else{
			shape.getTransformMatrix().setToTranslation(dx + model.getPosition().x, dy + model.getPosition().y, 0);
		}

		shape.getTransformMatrix().scale(10f, 10f, 1f);
		shape.updateMatrices();

		shape.setColor(Color.YELLOW);

		//draw vertices
		shape.polygon(model.getVertices());

		shape.setColor(Color.PURPLE);

		//draw bones
		for(int i = 0;i < model.getBones().length - 1;i ++){
			shape.line(model.getBones()[i], model.getBones()[i + 1]);
		}

		shape.set(ShapeType.Filled);

		//draw vertice points
		shape.setColor(Color.RED);

		for(int i = 0;i < model.getVertices().length / 2;i ++){
			float x = model.getVertices()[i * 2];
			float y = model.getVertices()[i * 2 + 1];
			shape.circle(x, y, 5 / 10f);
		}

		//draw bone points
		shape.setColor(Color.BLUE);

		for(int i = 0;i < model.getBones().length;i ++){
			shape.circle(model.getBones()[i].x, model.getBones()[i].y, 6 / 10f);
		}
		
		for(SoftModel child : model.getChildren()){
			renderDebug(child, model);
		}
	}

	private void renderModel(SoftModel model, SoftModel parent){
		polybatch.end();
		if(parent != null){
			polybatch.getTransformMatrix().setToTranslation(dx + model.getTransformedPosition().x * 10 + model.getOrigin().x*10, dy + model.getTransformedPosition().y * 10 + model.getOrigin().y*10, 0);
		}else{
			polybatch.getTransformMatrix().setToTranslation(dx + model.getPosition().x, dy + model.getPosition().y, 0);
		}
		
		polybatch.getTransformMatrix().scale(10f, 10f, 1f);
		polybatch.getTransformMatrix().rotate(new Vector3(0,0,1), model.rotation);
		
		polybatch.begin();
		Vector2 offset = Vector2.Zero;
		if(parent != null) offset = model.getOrigin();
		polybatch.draw(model.getRegion(), -offset.x, -offset.y, model.getTexture().getWidth(), model.getTexture().getHeight());
		
		for(SoftModel child : model.getChildren()){
			renderModel(child, model);
		}
	}

	public void resize(int width, int height){
		polybatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		shape.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		shape.updateMatrices();
	}
}
