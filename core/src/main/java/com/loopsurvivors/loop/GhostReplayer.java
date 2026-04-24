package com.loopsurvivors.loop;

public class GhostReplayer {

    private final LoopRecording recording;
    private int tickIndex = 0;

    public GhostReplayer(LoopRecording recording) {
        this.recording = recording;
    }

    /**
     * 다음 프레임 반환. null이면 녹화 종료 → Ghost는 정지 모드로 전환.
     */
    public InputFrame next() {
        if (tickIndex >= recording.size()) return null;
        return recording.get(tickIndex++);
    }

    public boolean isFinished() {
        return tickIndex >= recording.size();
    }
}
