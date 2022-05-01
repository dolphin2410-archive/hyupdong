package io.github.dolphin2410.hyupdong.packets;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class HyupdongAttachInitPacket implements Packet<ClientPlayPacketListener> {
    private final int spirit;
    private final int body;

    public HyupdongAttachInitPacket(Entity spirit, Entity body) {
        this.spirit = spirit.getId();
        this.body = body.getId();
    }

    public HyupdongAttachInitPacket(PacketByteBuf buf) {
        this.spirit = buf.readVarInt();
        this.body = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(spirit);
        buf.writeVarInt(body);
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {

    }

    public int getSpirit() {
        return spirit;
    }

    public int getBody() {
        return body;
    }
}
