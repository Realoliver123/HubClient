package com.realoliver123.hubclient.features.combat;

import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.settings.BooleanSetting;
import com.realoliver123.hubclient.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import java.util.Random;

public class AutoClicker extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    // Added MORE settings for you
    public static NumberSetting minCPS = new NumberSetting("Min CPS", 8, 1, 20, 1);
    public static NumberSetting maxCPS = new NumberSetting("Max CPS", 12, 1, 20, 1);
    public static BooleanSetting weaponOnly = new BooleanSetting("Weapon Only", true);
    public static BooleanSetting jitter = new BooleanSetting("Jitter Movement", false);
    public static BooleanSetting blockHit = new BooleanSetting("Auto BlockHit", false);

    private long lastClick;
    private long nextDelay;
    private Random random = new Random();

    public AutoClicker() {
        super("AutoClicker", ModuleCategory.COMBAT);
        this.addSettings(minCPS, maxCPS, weaponOnly, jitter, blockHit);
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (mc.currentScreen != null) return;
        if (!Mouse.isButtonDown(0)) return;

        if (weaponOnly.isEnabled() && mc.thePlayer.getHeldItem() == null) return;

        if (System.currentTimeMillis() - lastClick >= nextDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());

            // Extra Settings Logic
            if (jitter.isEnabled()) {
                // Slight random rotation
                mc.thePlayer.rotationYaw += (random.nextFloat() - 0.5f) * 2;
                mc.thePlayer.rotationPitch += (random.nextFloat() - 0.5f) * 2;
            }

            if (blockHit.isEnabled() && Mouse.isButtonDown(1)) {
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
            }

            updateDelay();
            lastClick = System.currentTimeMillis();
        }
    }

    private void updateDelay() {
        double min = minCPS.getValue();
        double max = maxCPS.getValue();
        if (min > max) min = max;
        double cps = min + (random.nextDouble() * (max - min));
        this.nextDelay = (long) (1000.0 / cps);
    }
}