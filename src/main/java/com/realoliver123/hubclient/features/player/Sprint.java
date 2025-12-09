package com.realoliver123.hubclient.features.player;

import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Sprint extends Module {

    // --- FIX: DEFINE 'mc' HERE ---
    private final Minecraft mc = Minecraft.getMinecraft();

    public Sprint() {
        super("Sprint", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        // Safety Checks: ensure player exists and isn't sneaking, blinded, or hungry
        if (mc.thePlayer != null
                && !mc.thePlayer.isSneaking()
                && !mc.thePlayer.isCollidedHorizontally
                && mc.thePlayer.moveForward > 0
                && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {

            mc.thePlayer.setSprinting(true);
        }
    }
}