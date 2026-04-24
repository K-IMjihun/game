package com.loopsurvivors.loop;

import com.badlogic.gdx.math.Vector2;

public record InputFrame(int tickIndex, Vector2 moveDir, boolean attackPressed, boolean skillPressed) {

    public InputFrame(int tickIndex, Vector2 moveDir, boolean attackPressed, boolean skillPressed) {
        this.tickIndex = tickIndex;
        this.moveDir = moveDir.cpy();
        this.attackPressed = attackPressed;
        this.skillPressed = skillPressed;
    }
}
