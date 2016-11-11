package io.anuke.myri.terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.ucore.Noise;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.graphics.Textures;

public class MapProcessor{
	private boolean processing;
	private ObjectMap<Integer, Pixmap> textures = new ObjectMap<Integer, Pixmap>();
	private final int invalidColor = Color.rgba8888(Color.BLACK);
	private final int shadow = Color.rgba8888(0, 0, 0, 0.3f);
	private int layer = 0;

	public MapProcessor() {
		map(Color.GREEN, "grass");
		map(Color.GRAY, "magmarock");
		map(Color.DARK_GRAY, "blackrock");
		map(Color.RED, "lava");
		map(Color.BROWN, "dirt");
		map(Color.TAN, "sand");
		map(Color.OLIVE, "gravel");
	}
	
	/*
	private void map(int r, int g, int b, String texture){
		map(new Color(r/255f, g/255f, b/255f, 1f), texture);
	}
*/
	private void map(Color color, String texture){
		Texture tex = Textures.get(texture);
		tex.getTextureData().prepare();
		Pixmap pix = tex.getTextureData().consumePixmap();
		textures.put(Color.rgba8888(color), pix);
	}

	public void process(Array<Pixmap> layers, Pixmap pixmap){
		processing = true;
		layer = 0;
		
		for(Pixmap input : layers){
			PixmapUtils.traverse(input, (x, y) -> {
				int color = input.getPixel(x, y);
				int out = invalidColor;

				Pixmap pix = textures.get(color);
				if(pix != null){
					out = pix.getPixel(x % pix.getWidth(), y % pix.getHeight());
					if(layer == 1){
						pixmap.drawPixel(x, y + 4, shadow);
						
					}
					pixmap.drawPixel(x, y, out);
					if(layer == 1) pixmap.drawPixel(x, y, Color.rgba8888(0, 0, 0, 0.3f-Math.abs(Noise.normalNoise(x, y/2, 8f, 0.3f))));
					if(layer == 0) pixmap.drawPixel(x, y, Color.rgba8888(0, 0, 0, 0.1f*(int)(Math.abs(Noise.normalNoise(x, y, 45f, 0.5f) + Noise.normalNoise(x, y, 5f, 0.4f))/0.1f)));
					
				}

			});
			layer++;
		}

		processing = false;
	}

	public boolean isProcessing(){
		return processing;
	}
}
