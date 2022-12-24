package com.johanvonelectrum.yaum.server.protocol;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.text.YaumText;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LoginProtocolManager {
    private static final Map<ClientConnection, LoginProtocolManager> MANAGERS = new HashMap<>();
    public static final Identifier HELLO = new Identifier("yaum", "request_protocol"); //S2C & C2S

    private final ServerLoginNetworkHandler serverLoginNetworkHandler;
    private final CompletableFuture<?> fence = new CompletableFuture<>();
    private LoginProtocolState state = null;

    public LoginProtocolManager(ServerLoginNetworkHandler serverLoginNetworkHandler) {
        this.serverLoginNetworkHandler = serverLoginNetworkHandler;
    }

    public static LoginProtocolManager of(ServerLoginNetworkHandler serverLoginNetworkHandler) {
        ClientConnection connection = serverLoginNetworkHandler.getConnection();

        if (MANAGERS.containsKey(connection))
            return MANAGERS.get(connection);

        LoginProtocolManager manager = new LoginProtocolManager(serverLoginNetworkHandler);
        MANAGERS.put(connection, manager);

        return manager;
    }

    public void start() {
        if (state != null) return;

        final ServerLoginNetworkAddon network = ServerNetworkingImpl.getAddon(serverLoginNetworkHandler);

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString("yaum / 1.0.1"); //TODO: use mod metadata
        buf.writeBoolean(YaumSettings.requireClient);

        LoginQueryRequestS2CPacket packet = (LoginQueryRequestS2CPacket) network.createPacket(HELLO, buf);
        network.sendPacket(packet);

        state = LoginProtocolState.REQUESTED;
    }

    private void wait(ServerLoginNetworking.LoginSynchronizer synchronizer) {
        synchronizer.waitFor(this.fence);
    }

    private void onHelloRes(String lang, String[] mods, PacketSender responseSender) {
        System.out.println(lang); //TODO: store to future (per user) translations
        System.out.println(Arrays.toString(mods)); // User mods: should be passed to isEligibleToTrusted(...) TODO: think a better name
        this.fence.complete(null); //TODO: add conditions to trust
    }

    public enum LoginProtocolState {
        REQUESTED,
        COMPLETE
    }

    static {
        ServerLoginNetworking.registerGlobalReceiver(HELLO, (server, handler, understood, buf1, synchronizer, responseSender) -> {
            if (!understood) {
                handler.disconnect(YaumText.translatable("error.yaum.invalid-packet").asText());
                MANAGERS.remove(handler.getConnection());

                return; //TODO: log (invalid response from client)
            }

            LoginProtocolManager manager = MANAGERS.get(handler.getConnection());
            if (YaumSettings.requireClient) manager.wait(synchronizer);

            String lang = buf1.readString();
            String[] mods = new String[buf1.readInt()];
            for (int i = 0; i < mods.length; i++) {
                mods[i] = buf1.readString();
            }

            manager.onHelloRes(lang, mods, responseSender);
        });
    }

}
