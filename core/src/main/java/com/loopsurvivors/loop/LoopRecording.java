package com.loopsurvivors.loop;

import java.util.ArrayList;
import java.util.List;

public class LoopRecording {

    private final List<InputFrame> frames = new ArrayList<>();

    public void add(InputFrame frame) {
        frames.add(frame);
    }

    public InputFrame get(int tickIndex) {
        if (tickIndex < frames.size()) return frames.get(tickIndex);
        return null;
    }

    public int size() {
        return frames.size();
    }
}
