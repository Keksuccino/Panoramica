package de.keksuccino.panoramica.events;

import de.keksuccino.konkrete.events.EventBase;

public class TickEvent extends EventBase {

    @Override
    public boolean isCancelable() {
        return false;
    }

}
