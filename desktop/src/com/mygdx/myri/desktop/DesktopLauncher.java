package com.mygdx.myri.desktop;

import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.anuke.myri.Myri;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Myri");
		Toolkit tk = Toolkit.getDefaultToolkit();
		config.setWindowedMode(tk.getScreenSize().width, tk.getScreenSize().height);
		new Lwjgl3Application(new Myri(), config);
	}
}
