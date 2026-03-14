package org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.With;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.ClockState;

import java.util.HashMap;
import java.util.Map;

@Data
@With
public class ClientboundSetTimePacket implements MinecraftPacket {
    private final long gameTime;
    private final Map<Integer, ClockState> clockUpdates;

    public ClientboundSetTimePacket(long gameTime, Map<Integer, ClockState> clockUpdates) {
        this.gameTime = gameTime;
        this.clockUpdates = Map.copyOf(clockUpdates);
    }

    public ClientboundSetTimePacket(ByteBuf in) {
        this.gameTime = in.readLong();

        this.clockUpdates = new HashMap<>();
        int size = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < size; i++) {
            int clockType = MinecraftTypes.readVarInt(in);
            ClockState state = new ClockState(MinecraftTypes.readVarLong(in), in.readBoolean());
            this.clockUpdates.put(clockType, state);
        }
    }

    @Override
    public void serialize(ByteBuf out) {
        out.writeLong(this.gameTime);

        MinecraftTypes.writeVarInt(out, this.clockUpdates.size());
        for (Map.Entry<Integer, ClockState> entry : this.clockUpdates.entrySet()) {
            MinecraftTypes.writeVarInt(out, entry.getKey());
            MinecraftTypes.writeVarLong(out, entry.getValue().totalTicks());
            out.writeBoolean(entry.getValue().paused());
        }
    }

    @Override
    public boolean shouldRunOnGameThread() {
        return true;
    }
}
