package com.bsencan.openchess.actors;

import com.badlogic.gdx.scenes.scene2d.Event;

import entity.event.GameEvent;

/**
 * Created by Irina on 31.07.2017.
 */

public class GameOverEventActor extends Event {
    private final GameEvent event;

    public GameOverEventActor(GameEvent event) {
        super();
        this.event = event;
    }

    public GameEvent getEvent() {
        return event;
    }

}
