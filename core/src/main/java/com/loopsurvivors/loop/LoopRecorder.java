package com.loopsurvivors.loop;

import com.badlogic.gdx.math.Vector2;

public class LoopRecorder {

    private LoopRecording recording = new LoopRecording();

    public void record(int tickIndex, Vector2 moveDir, boolean attackPressed, boolean skillPressed) {
        recording.add(new InputFrame(tickIndex, moveDir, attackPressed, skillPressed));
    }

    /** 현재 루프 녹화를 확정하고 반환. 다음 루프용으로 내부 상태 초기화. */
    public LoopRecording finishAndReset() {
        LoopRecording finished = recording;
        recording = new LoopRecording();
        return finished;
    }
}
