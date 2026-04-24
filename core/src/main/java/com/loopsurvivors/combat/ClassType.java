package com.loopsurvivors.combat;

import com.badlogic.gdx.graphics.Color;

public enum ClassType {
    SWORD, BOW, HEAL;

    public Color tint() {
        return switch (this) {
            case SWORD -> new Color(0.9f, 0.3f, 0.3f, 1f);
            case BOW   -> new Color(0.3f, 0.9f, 0.3f, 1f);
            case HEAL  -> new Color(0.3f, 0.5f, 0.9f, 1f);
        };
    }
}
