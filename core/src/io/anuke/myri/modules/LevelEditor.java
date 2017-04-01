package io.anuke.myri.modules;

import java.nio.file.*;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisTable;

import io.anuke.myri.Myri;
import io.anuke.myri.terrain.MapProcessor;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;

public class LevelEditor extends Module<Myri>{
	Stage stage = new Stage();
	VisTable table;
	Path path = Paths.get("/home/anuke/Projects/Myri/map/");
	String mapname = "";
	WatchService watcher;
	boolean changed = false;
	MapProcessor processor;
	Texture maptex;
	Pixmap pixmap;

	public LevelEditor() {
		Textures.load("textures/");
		processor = new MapProcessor();
		VisUI.load(SkinScale.X2);
		ShapeUtils.region = VisUI.getSkin().getRegion("white");
		stage.setViewport(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		Textures.repeatWrap("grass");

		try{
			watcher = FileSystems.getDefault().newWatchService();
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

		}catch(Exception e){
			e.printStackTrace();
		}

		new Thread(() -> {
			while(true){
				WatchKey key;
				try{
					key = watcher.take();
				}catch(InterruptedException x){
					return;
				}

				List<WatchEvent<?>> events = key.pollEvents();
				for(WatchEvent<?> event : events){
					Path file = path.resolve((Path) event.context());
					changed = true;
					System.out.println(file + " changed. updating...");

				}
				key.reset();
				if(!key.isValid())
					break;
			}
		}){
			{
				setDaemon(true);
			}
		}.start();

		updateMap();
	}

	@Override
	public void init(){
		table = new VisTable();
		table.setFillParent(true);
		table.center();
	}

	void updateMap(){
		if(pixmap != null){
			pixmap.dispose();
			maptex.dispose();
		}

		// Pixmap in = new Pixmap(Gdx.files.absolute(path.toString() + "/" +
		// mapname));
		Array<Pixmap> pixmaps = new Array<Pixmap>();
		for(int i = 0; i < 10; i++){
			FileHandle file = Gdx.files.absolute(path.toString() + "/" + mapname + i + ".png");
			if(file.exists())
				pixmaps.add(new Pixmap(file));
		}

		Pixmap out = new Pixmap(pixmaps.get(0).getWidth(), pixmaps.get(0).getHeight(), Format.RGBA8888);
		processor.process(pixmaps, out);

		for(Pixmap in : pixmaps)
			in.dispose();
		pixmap = out;
		maptex = new Texture(pixmap);
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyPressed(Keys.ESCAPE))
			Gdx.app.exit();

		if(changed){
			updateMap();
			changed = false;
		}

		clearScreen(Color.BLACK);
		stage.getBatch().begin();
		stage.getBatch().draw(maptex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.getBatch().end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}
}
