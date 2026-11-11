package com.brochacho.eggtimer.net;

import com.brochacho.eggtimer.client.ClientEggTimers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EggTimersPacket {
    public final ResourceLocation dimension;
    public final long[] positions;
    public final int[] ticks;

    public EggTimersPacket(ResourceLocation dimension, long[] positions, int[] ticks) {
        this.dimension = dimension;
        this.positions = positions;
        this.ticks = ticks;
    }

    public static void encode(EggTimersPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.dimension);
        buf.writeVarInt(msg.positions.length);
        for (int i = 0; i < msg.positions.length; i++) {
            buf.writeLong(msg.positions[i]);
            buf.writeVarInt(msg.ticks[i]);
        }
    }

    public static EggTimersPacket decode(FriendlyByteBuf buf) {
        ResourceLocation dim = buf.readResourceLocation();
        int n = buf.readVarInt();
        long[] pos = new long[n];
        int[] ticks = new int[n];
        for (int i = 0; i < n; i++) {
            pos[i] = buf.readLong();
            ticks[i] = buf.readVarInt();
        }
        return new EggTimersPacket(dim, pos, ticks);
    }

    public static void handle(EggTimersPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientEggTimers.update(msg)));
        ctx.get().setPacketHandled(true);
    }
}
