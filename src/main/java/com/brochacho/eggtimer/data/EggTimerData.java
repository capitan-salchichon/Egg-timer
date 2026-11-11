package com.brochacho.eggtimer.data;

import com.brochacho.eggtimer.EggKind;
import com.brochacho.eggtimer.EggTimers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Temporizadores por dimensión. Clave = BlockPos.asLong(), valor = ticks que faltan
 * para la siguiente etapa (grieta) o para eclosionar (triops).
 * Solo avanzan mientras el chunk está cargado, igual que el random tick original.
 */
public class EggTimerData extends SavedData {
    private static final String NAME = "alexs_egg_timer";
    private final Map<Long, Integer> timers = new HashMap<>();

    public static EggTimerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(EggTimerData::load, EggTimerData::new, NAME);
    }

    public static EggTimerData load(CompoundTag tag) {
        EggTimerData data = new EggTimerData();
        ListTag list = tag.getList("Timers", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            data.timers.put(t.getLong("Pos"), t.getInt("Ticks"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<Long, Integer> e : timers.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putLong("Pos", e.getKey());
            t.putInt("Ticks", e.getValue());
            list.add(t);
        }
        tag.put("Timers", list);
        return tag;
    }

    /** Registra solo si no existía. */
    public void registerIfAbsent(BlockPos pos, int ticks) {
        if (timers.putIfAbsent(pos.asLong(), ticks) == null) setDirty();
    }

    /** Registra o reinicia. */
    public void set(BlockPos pos, int ticks) {
        timers.put(pos.asLong(), ticks);
        setDirty();
    }

    public record Snapshot(long pos, int ticks) {}

    /** Tiempos totales restantes de los huevos cercanos a un jugador. */
    public List<Snapshot> snapshotNear(ServerLevel level, BlockPos center, int radius) {
        List<Snapshot> out = new ArrayList<>();
        long r2 = (long) radius * radius;
        for (Map.Entry<Long, Integer> e : timers.entrySet()) {
            BlockPos pos = BlockPos.of(e.getKey());
            if (pos.distSqr(center) > r2 || !level.hasChunkAt(pos)) continue;
            BlockState state = level.getBlockState(pos);
            EggKind kind = EggKind.of(state);
            if (kind == null) continue;
            out.add(new Snapshot(e.getKey(), Math.max(0, kind.totalRemaining(state, e.getValue()))));
        }
        return out;
    }

    public void tick(ServerLevel level) {
        if (timers.isEmpty()) return;
        boolean validate = level.getGameTime() % 20L == 0L;
        List<Long> due = new ArrayList<>();
        List<Long> gone = new ArrayList<>();

        for (Map.Entry<Long, Integer> e : timers.entrySet()) {
            BlockPos pos = BlockPos.of(e.getKey());
            if (!level.hasChunkAt(pos)) continue; // chunk descargado: el tiempo no avanza
            BlockState state = level.getBlockState(pos);
            EggKind kind = EggKind.of(state);
            if (kind == null) {
                if (validate) gone.add(e.getKey());
                continue;
            }
            int left = e.getValue() - 1;
            if (kind == EggKind.TRIOPS) {
                // la eclosión real la hace el scheduled tick de vanilla; aquí solo mostramos
                e.setValue(Math.max(0, left));
            } else if (left <= 0) {
                due.add(e.getKey());
            } else {
                e.setValue(left);
            }
        }

        for (Long key : gone) timers.remove(key);

        for (Long key : due) {
            BlockPos pos = BlockPos.of(key);
            BlockState state = level.getBlockState(pos);
            EggKind kind = EggKind.of(state);
            if (kind == null || kind == EggKind.TRIOPS) {
                timers.remove(key);
                continue;
            }
            if (!kind.hasHabitat(level, pos, state)) {
                timers.put(key, 100); // sin hábitat: pausado, reintenta cada 5 s
                continue;
            }
            int before = state.getValue(BlockStateProperties.HATCH);
            EggTimers.forceGrow = true;
            try {
                state.randomTick(level, pos, level.random);
            } finally {
                EggTimers.forceGrow = false;
            }
            BlockState after = level.getBlockState(pos);
            EggKind afterKind = EggKind.of(after);
            if (afterKind == null) {
                timers.remove(key); // eclosionó
            } else if (after.getValue(BlockStateProperties.HATCH) != before) {
                timers.put(key, afterKind.stageTicks());
            } else {
                timers.put(key, 100);
            }
        }

        if (validate || !due.isEmpty() || !gone.isEmpty()) setDirty();
    }
}
