package io.github.dolphin2410.hyupdong.packets;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class HyupdongMovePacket implements Packet<ServerPlayPacketListener> {
    public enum MoveType {
        W,
        A,
        S,
        D,
        LSHIFT,
        SPACE,
        CTRL
    }

    public enum ActionType {
        PRESS,
        RELEASE;

        public static ActionType fromBool(boolean bool) {
            return bool ? PRESS : RELEASE;
        }

        public boolean bool() {
            return this == PRESS;
        }
    }

    private final MoveType type;
    private final ActionType actionType;
    private final int body;

    public HyupdongMovePacket(MoveType type, ActionType actionType, int attached) {
        this.type = type;
        this.actionType = actionType;
        this.body = attached;
    }

    public HyupdongMovePacket(PacketByteBuf buf) {
        this.type = buf.readEnumConstant(MoveType.class);
        this.actionType = buf.readEnumConstant(ActionType.class);
        this.body = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(type);
        buf.writeEnumConstant(actionType);
        buf.writeVarInt(body);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {

    }

    public MoveType getType() {
        return type;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getBody() {
        return body;
    }
}
