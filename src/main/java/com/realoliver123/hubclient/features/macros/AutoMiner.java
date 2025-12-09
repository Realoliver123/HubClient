package com.realoliver123.hubclient.features.macros;

import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoMiner extends Module {

    // --- FIX: DEFINE 'mc' HERE ---
    private final Minecraft mc = Minecraft.getMinecraft();

    public AutoMiner() {
        super("Auto Miner", ModuleCategory.MACROS);
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        // Safety check: if game isn't loaded or player is null
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // If the module is toggled ON, hold down the attack key
        if (this.isToggled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
        }
    }

    @Override
    public void onDisable() {
        // When toggled OFF, release the key immediately
        if (mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }
    }
}