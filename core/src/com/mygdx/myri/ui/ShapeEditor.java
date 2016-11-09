package com.mygdx.myri.ui;

import com.badlogic.gdx.math.Vector2;

public class ShapeEditor{
	public Vector2[] points = new Vector2[0];
	public PartWidget widget;
	
	public ShapeEditor(PartWidget widget){
		this.widget = widget;
	}
}
