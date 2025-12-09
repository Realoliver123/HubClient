package com.realoliver123.hubclient.features.render;

import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VisualSpin extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    // CHANGED: Max speed increased from 20 to 50
    public NumberSetting speed = new NumberSetting("Speed", 5, 1, 50, 1);

    // Variables to store REAL rotation
    private float oldRenderYawOffset;
    private float oldPrevRenderYawOffset;
    private float oldRotationYawHead;
    private float oldPrevRotationYawHead;

    public VisualSpin() {
        super("Visual Spin", ModuleCategory.RENDER);
        addSettings(speed);
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (event.entityPlayer.equals(mc.thePlayer)) {
            // 1. BACKUP Real Rotation
            oldRenderYawOffset = event.entityPlayer.renderYawOffset;
            oldPrevRenderYawOffset = event.entityPlayer.prevRenderYawOffset;
            oldRotationYawHead = event.entityPlayer.rotationYawHead;
            oldPrevRotationYawHead = event.entityPlayer.prevRotationYawHead;

            // 2. Calculate Smooth Spin
            float spinSpeed = (float) speed.getValue() * 20f;
            float rotation = (float) (System.currentTimeMillis() % 360000) / 1000f * spinSpeed;
            rotation %= 360f;

            // 3. OVERRIDE Rotation (Both Current and Prev to prevent jitter)
            event.entityPlayer.renderYawOffset = rotation;
            event.entityPlayer.prevRenderYawOffset = rotation;

            event.entityPlayer.rotationYawHead = rotation;
            event.entityPlayer.prevRotationYawHead = rotation;
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.entityPlayer.equals(mc.thePlayer)) {
            // 4. RESTORE Real Rotation
            event.entityPlayer.renderYawOffset = oldRenderYawOffset;
            event.entityPlayer.prevRenderYawOffset = oldPrevRenderYawOffset;
            event.entityPlayer.rotationYawHead = oldRotationYawHead;
            event.entityPlayer.prevRotationYawHead = oldPrevRotationYawHead;
        }
    }
}