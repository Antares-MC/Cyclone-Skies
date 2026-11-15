package com.paymentalerts.sound;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.sounds.SoundEvent;

public final class SoundCues {
    private final List<Cue> cues = new ArrayList();
    private boolean sorted = true;
    private int next;

        private static final record Cue(float at, Runnable action) {


        

        

        


    }

    public SoundCues sound(float f, SoundEvent soundEvent, float f2, float f3) {
        return action(f, () -> {
            AlertSounds.play(soundEvent, f2, f3);
        });
    }

    public SoundCues action(float f, Runnable runnable) {
        this.cues.add(new Cue(f, runnable));
        this.sorted = false;
        return this;
    }

    public void update(float f) {
        if (!this.sorted) {
            this.cues.sort(Comparator.comparingDouble((v0) -> {
                return v0.at();
            }));
            this.sorted = true;
        }
        while (this.next < this.cues.size() && this.cues.get(this.next).at() <= f) {
            List<Cue> list = this.cues;
            int i = this.next;
            this.next = i + 1;
            list.get(i).action().run();
        }
    }

    public boolean isDone() {
        return this.next >= this.cues.size();
    }
}
