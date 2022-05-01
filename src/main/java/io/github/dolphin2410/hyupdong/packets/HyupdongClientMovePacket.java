package io.github.dolphin2410.hyupdong.packets;

import io.github.dolphin2410.hyupdong.KeybindingWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class HyupdongClientMovePacket implements Packet<ServerPlayPacketListener> {
    private final HyupdongMovePacket.MoveType type;
    private final HyupdongMovePacket.ActionType actionType;
    private final int attached;

    public HyupdongClientMovePacket(HyupdongMovePacket packet) {
        this.type = packet.getType();
        this.actionType = packet.getActionType();
        this.attached = packet.getBody();
    }

    public HyupdongClientMovePacket(PacketByteBuf buf) {
        this.type = buf.readEnumConstant(HyupdongMovePacket.MoveType.class);
        this.actionType = buf.readEnumConstant(HyupdongMovePacket.ActionType.class);
        this.attached = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(type);
        buf.writeEnumConstant(actionType);
        buf.writeVarInt(attached);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {

    }

    public void handle(MinecraftClient client) {
        assert client.player != null;
        try {
            switch (type) {
                case W -> ((KeybindingWrapper) client.options.forwardKey).setPressedAlt(actionType.bool());
                case A -> ((KeybindingWrapper) client.options.leftKey).setPressedAlt(actionType.bool());
                case S -> ((KeybindingWrapper) client.options.backKey).setPressedAlt(actionType.bool());
                case D -> ((KeybindingWrapper) client.options.rightKey).setPressedAlt(actionType.bool());
                case LSHIFT -> ((KeybindingWrapper) client.options.sneakKey).setPressedAlt(actionType.bool());
                case SPACE -> ((KeybindingWrapper) client.options.jumpKey).setPressedAlt(actionType.bool());
                case CTRL -> ((KeybindingWrapper) client.options.sprintKey).setPressedAlt(actionType.bool());
            }
        } catch (Exception e) {
            client.player.sendChatMessage(e.getMessage());
        }
    }

    public HyupdongMovePacket.MoveType getType() {
        return type;
    }

    public HyupdongMovePacket.ActionType getActionType() {
        return actionType;
    }

    public int getAttached() {
        return attached;
    }
}
