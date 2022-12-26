package com.johanvonelectrum.yaum.mixin.core;

import com.johanvonelectrum.yaum.Yaum;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class YaumLoader {

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void load(CallbackInfo ci) {
        Yaum.onServerLoaded((MinecraftServer) (Object) this);
    }

    @Inject(method = "stop", at = @At("TAIL"))
    private void unload(boolean bl, CallbackInfo ci) {
        Yaum.onServerUnloaded();
    }


}
