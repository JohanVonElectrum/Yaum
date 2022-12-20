package com.johanvonelectrum.yaum.client;

import com.johanvonelectrum.yaum.server.protocol.LoginProtocolManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class YaumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // TODO: move away
        ClientLoginNetworking.registerGlobalReceiver(LoginProtocolManager.HELLO, (client, handler, buf, listenerAdder) -> {
            String yaumProtocol = buf.readString();
            boolean required = buf.readBoolean();

            PacketByteBuf buf2 = PacketByteBufs.create();
            buf2.writeString(MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode());
            Collection<String> mods = getMods();
            buf2.writeInt(mods.size());
            for (String mod : mods) {
                buf2.writeString(mod);
            }

            return CompletableFuture.completedFuture(buf2);
        });
    }

    private Collection<String> getMods() {
        return FabricLoader.getInstance().getAllMods().stream().map(modContainer -> {
            ModMetadata metadata = modContainer.getMetadata();
            return metadata.getId() + "@" + metadata.getVersion().getFriendlyString();
        }).toList();
    }

}
