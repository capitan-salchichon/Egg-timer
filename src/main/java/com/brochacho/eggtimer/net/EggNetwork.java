package com.brochacho.eggtimer.net;

import com.brochacho.eggtimer.AlexsEggTimer;
import com.brochacho.eggtimer.data.EggTimerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class EggNetwork {
    private static final String VERSION = "1";
    private static final int SYNC_RADIUS = 64;
    private static final Set<UUID> HAS_DATA = new HashSet<>();

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AlexsEggTimer.MODID, "main"),
            () -> VERSION,
            NetworkRegistry.acceptMissingOr(VERSION),
            NetworkRegistry.acceptMissingOr(VERSION));

    private EggNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, EggTimersPacket.class,
                EggTimersPacket::encode, EggTimersPacket::decode, EggTimersPacket::handle);
    }

    /** Envía a cada jugador los tiempos de los huevos a menos de 64 bloques. */
    public static void syncPlayers(ServerLevel level, EggTimerData data) {
        ResourceLocation dim = level.dimension().location();
        for (ServerPlayer player : level.players()) {
            List<EggTimerData.Snapshot> snap = data.snapshotNear(level, player.blockPosition(), SYNC_RADIUS);
            boolean had = HAS_DATA.contains(player.getUUID());
            if (snap.isEmpty() && !had) continue;

            long[] pos = new long[snap.size()];
            int[] ticks = new int[snap.size()];
            for (int i = 0; i < snap.size(); i++) {
                pos[i] = snap.get(i).pos();
                ticks[i] = snap.get(i).ticks();
            }
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new EggTimersPacket(dim, pos, ticks));

            if (snap.isEmpty()) HAS_DATA.remove(player.getUUID());
            else HAS_DATA.add(player.getUUID());
        }
    }
}
