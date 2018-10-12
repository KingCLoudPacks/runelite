package net.runelite.client.plugins.freezetimer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.Player;

public class FreezeInstance {

    private static final int FREEZE_DELAY = 3;

    @Getter
    private final Player player;

    private final Instant instant;

    private final FreezeSpell spell;

    public FreezeInstance(FreezeSpell spell, Player player, Instant instant) {
        this.spell = spell;
        this.player = player;
        this.instant = instant;
    }

    public Instant getExpire() {
        return instant.plus(spell.getTime(), ChronoUnit.SECONDS);
    }

    public Instant getNext() {
        return instant.plus(spell.getTime() + FREEZE_DELAY, ChronoUnit.SECONDS);
    }

    public boolean finished() {
        return !Duration.between(getNext(), Instant.now()).isNegative();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreezeInstance that = (FreezeInstance) o;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {

        return Objects.hash(player);
    }

}
