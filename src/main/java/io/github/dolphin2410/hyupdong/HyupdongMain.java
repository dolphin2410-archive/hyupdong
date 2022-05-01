package io.github.dolphin2410.hyupdong;

import io.github.dolphin2410.hyupdong.packets.HyupdongAttachInitPacket;
import io.github.dolphin2410.hyupdong.packets.HyupdongAttachPacket;
import io.github.dolphin2410.hyupdong.packets.HyupdongClientMovePacket;
import io.github.dolphin2410.hyupdong.packets.HyupdongMovePacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class HyupdongMain implements ModInitializer {
    public static Identifier attachPacket = new Identifier("attach_packet");
    public static Identifier detachPacket = new Identifier("detach_packet");
    public static Identifier movePacket = new Identifier("move_packet");
    public static Identifier moveClientPacket = new Identifier("move_client_packet");
    public static Identifier clientAttachInit = new Identifier("client_attach_init");
    public static PlayerEntity body;
    public static double mouseSensitivity;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("hyupdong")
                    .then(literal("attach")
                        .then(argument("player", player()).executes(ctx -> {
                            ServerPlayerEntity sender = ctx.getSource().getPlayer();
                            body = getPlayer(ctx, "player");

                            HyupdongAttachPacket packet = new HyupdongAttachPacket(body, sender);
                            PacketByteBuf buf = PacketByteBufs.create();
                            packet.write(buf);

                            HyupdongAttachInitPacket packet2 = new HyupdongAttachInitPacket(sender, body);
                            PacketByteBuf buf2 = PacketByteBufs.create();
                            packet2.write(buf2);

                            ServerPlayNetworking.send((ServerPlayerEntity) body, attachPacket, buf);
                            ServerPlayNetworking.send(sender, clientAttachInit, buf2);

                            return 0;
                        })))
                        .then(literal("detach").executes(ctx -> {
                            ServerPlayerEntity sender = ctx.getSource().getPlayer();
                            ServerPlayNetworking.send(sender, detachPacket, PacketByteBufs.empty());
                            return 0;
                        }))
                    );
        });

        // Received by the server
        // Tunnel spirit to the body
        ServerPlayNetworking.registerGlobalReceiver(movePacket, ((server, player, handler, buf, responseSender) -> {
            HyupdongMovePacket movePkt = new HyupdongMovePacket(buf);
            server.execute(() -> {
                HyupdongClientMovePacket clientMovePkt = new HyupdongClientMovePacket(movePkt);
                PacketByteBuf buf2 = PacketByteBufs.create();
                clientMovePkt.write(buf2);
                int body = movePkt.getBody();
                for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                    if (serverPlayer.getId() == body) {
                        ServerPlayNetworking.send(serverPlayer, moveClientPacket, buf2);
                    }
                }
            });
        }));

        // Received by the spirit
        // Everytime they activate the controls
        ClientPlayNetworking.registerGlobalReceiver(clientAttachInit, (client, handler, buf, responseSender) -> {
            HyupdongAttachInitPacket packet = new HyupdongAttachInitPacket(buf);
            client.execute(() -> {
               assert client.player != null;
               if (packet.getSpirit() == client.player.getId()) {
                   handleSpirit(client, packet.getBody());  // Listen to WASD events
               } else {
                   return;
               }
                client.setCameraEntity(client.player);
                mouseSensitivity = client.options.mouseSensitivity;  // The spirit cannot use their mouse
               client.options.mouseSensitivity = -1/3F;
           });
        });

        // Received by the body
        // Activates the activated controls
        ClientPlayNetworking.registerGlobalReceiver(moveClientPacket, (client, handler, buf, responseSender) -> {
            HyupdongClientMovePacket packet = new HyupdongClientMovePacket(buf);
            client.execute(() -> {
                packet.handle(client);  // Handles the control
            });
        });

        // Called when the spirit is attached to the body
        // Received by the body
        ClientPlayNetworking.registerGlobalReceiver(attachPacket, (client, handler, buf, responseSender) -> {
            HyupdongAttachPacket packet = new HyupdongAttachPacket(buf);
            client.execute(() -> {
                assert client.player != null;
                if (packet.getBody() != client.player.getId()) {
                    client.player.sendChatMessage("There was an internal error. Invalid packet was sent");
                } else {
                    client.player.sendChatMessage("Successfully attached spirit (id = " + packet.getSpirit() + ")");
                }
                Util.replaceKeybinding(client);  // The body can't move
            });
        });

        // On detach
        ClientPlayNetworking.registerGlobalReceiver(detachPacket, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                client.options.mouseSensitivity = mouseSensitivity;
                Util.revertKeybinding(client);
                client.setCameraEntity(client.player);
            });
        });
    }

    /**
     * @return The packet
     */
    public HyupdongMovePacket compareAndApply(int attached, HyupdongMovePacket.MoveType type, AtomicBoolean atomic, boolean comparator) {
        if (comparator != atomic.get()) {
            atomic.set(comparator);
            return new HyupdongMovePacket(type, HyupdongMovePacket.ActionType.fromBool(comparator), attached);
        }
        return null;
    }

    // The spirit
    public void handleSpirit(MinecraftClient client, int body) {
        GameOptions gameOptions = client.options;
        AtomicBoolean forwardPressed = new AtomicBoolean(gameOptions.forwardKey.isPressed());
        AtomicBoolean rightPressed = new AtomicBoolean(gameOptions.rightKey.isPressed());
        AtomicBoolean leftPressed = new AtomicBoolean(gameOptions.leftKey.isPressed());
        AtomicBoolean backPressed = new AtomicBoolean(gameOptions.backKey.isPressed());
        AtomicBoolean shiftPressed = new AtomicBoolean(gameOptions.sneakKey.isPressed());
        AtomicBoolean jumpPressed = new AtomicBoolean(gameOptions.jumpKey.isPressed());
        AtomicBoolean sprintPressed = new AtomicBoolean(gameOptions.sprintKey.isPressed());

        ClientTickEvents.END_CLIENT_TICK.register(client1 -> {
            HyupdongMovePacket[] list = {
                    compareAndApply(body, HyupdongMovePacket.MoveType.W, forwardPressed, gameOptions.forwardKey.isPressed()),
                    compareAndApply(body, HyupdongMovePacket.MoveType.A, leftPressed, gameOptions.leftKey.isPressed()),
                    compareAndApply(body, HyupdongMovePacket.MoveType.S, backPressed, gameOptions.backKey.isPressed()),
                    compareAndApply(body, HyupdongMovePacket.MoveType.D, rightPressed, gameOptions.rightKey.isPressed()),
                    compareAndApply(body, HyupdongMovePacket.MoveType.LSHIFT, shiftPressed, gameOptions.sneakKey.isPressed()),
                    compareAndApply(body, HyupdongMovePacket.MoveType.SPACE, jumpPressed, gameOptions.jumpKey.isPressed()),
                    compareAndApply(body, HyupdongMovePacket.MoveType.CTRL, sprintPressed, gameOptions.sprintKey.isPressed())
            };

            for (HyupdongMovePacket pkt : list) {
                if (pkt != null) {
                    PacketByteBuf pktBuf = PacketByteBufs.create();
                    pkt.write(pktBuf);
                    ClientPlayNetworking.send(movePacket, pktBuf);  // Send the move packets
                }
            }
        });
    }
}
