package io.anuke.myri.ui;

import com.badlogic.gdx.math.Vector2;

public class ShapeEditor{
	public Vector2[] points = new Vector2[0];
	public Part widget;
	
	public ShapeEditor(Part widget){
		this.widget = widget;
	}
}
