package com.johanvonelectrum.yaum.mixin.protocol;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.server.protocol.LoginProtocolManager;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class LoginProtocolGate {

    @Shadow private int loginTicks;
    @Shadow public abstract void disconnect(Text reason);

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (!YaumSettings.protocol) return;

        LoginProtocolManager.of((ServerLoginNetworkHandler)(Object)this).start();
    }

}
