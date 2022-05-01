package io.github.dolphin2410.hyupdong.packets;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class HyupdongAttachPacket implements Packet<ClientPlayPacketListener> {
    private final int body;
    private final int spirit;

    public HyupdongAttachPacket(Entity entity, Entity attached) {
        this.body = entity.getId();
        this.spirit = attached.getId();
    }

    public HyupdongAttachPacket(PacketByteBuf buf) {
        this.body = buf.readVarInt();
        this.spirit = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(body);
        buf.writeVarInt(spirit);
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {

    }

    public int getBody() {
        return body;
    }

    public int getSpirit() {
        return spirit;
    }
}
