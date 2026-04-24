package com.mygame.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.loopsurvivors.LoopSurvivorsGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(1280, 720);
        config.setTitle("Loop Survivors");
        config.useVsync(true);
        new Lwjgl3Application(new LoopSurvivorsGame(), config);
    }
}
