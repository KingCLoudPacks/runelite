package net.runelite.client.plugins.freezetimer;

import java.util.Arrays;
import java.util.Optional;

public enum FreezeSpell {

    ICE_BARRAGE(369, 20),
    ICE_BLITZ(367, 15);

    private final int id;
    private final int time;

    FreezeSpell(int id, int time) {
        this.id = id;
        this.time = time;
    }

    public static FreezeSpell getSpellForId(int id) {
        Optional<FreezeSpell> spell = Arrays.asList(values()).stream().filter(s -> s.getId() == id).findFirst();
        return (spell.isPresent()) ? spell.get() : null;
    }

    public static boolean isValid(int id) {
        Optional<FreezeSpell> spell = Arrays.asList(values()).stream().filter(s -> s.getId() == id).findFirst();
        return spell.isPresent();
    }

    public int getTime() {
        return this.time;
    }

    private int getId() {
        return this.id;
    }
}
