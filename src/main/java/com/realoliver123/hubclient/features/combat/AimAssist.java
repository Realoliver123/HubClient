package com.realoliver123.hubclient.features.combat;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.settings.BooleanSetting;
import com.realoliver123.hubclient.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Random;

public class AimAssist extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();

    // --- TARGET SETTINGS ---
    public BooleanSetting targetPlayers = new BooleanSetting("Target Players", true);
    public BooleanSetting targetAnimals = new BooleanSetting("Target Animals", false);
    public BooleanSetting targetMobs = new BooleanSetting("Target Mobs", false);

    // --- MAIN SETTINGS ---
    public NumberSetting speed = new NumberSetting("Speed", 45, 1, 100, 1);
    public NumberSetting fov = new NumberSetting("FOV", 90, 10, 180, 5);
    public NumberSetting range = new NumberSetting("Range", 4.5, 3.0, 8.0, 0.1);

    // --- CONDITIONS ---
    public BooleanSetting clickOnly = new BooleanSetting("Click Only", true);
    public BooleanSetting weaponOnly = new BooleanSetting("Weapon Only", true);
    public BooleanSetting aimPitch = new BooleanSetting("Aim Pitch", false);

    // --- VISUALS ---
    public BooleanSetting showFOV = new BooleanSetting("Show FOV Circle", false);

    public AimAssist() {
        super("Aim Assist", ModuleCategory.COMBAT);
        addSettings(targetPlayers, targetAnimals, targetMobs, speed, fov, range, clickOnly, weaponOnly, aimPitch, showFOV);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isToggled() || mc.theWorld == null || mc.thePlayer == null) return;
        if (mc.currentScreen != null) return;

        // 1. Check Conditions
        if (clickOnly.isEnabled() && !Mouse.isButtonDown(0)) return;

        if (weaponOnly.isEnabled()) {
            if (mc.thePlayer.getCurrentEquippedItem() == null) return;
            if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) &&
                    !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                return;
            }
        }

        // 2. Find Target
        EntityLivingBase target = getClosestTarget();
        if (target != null) {
            // 3. Aim smoothly
            faceEntity(target);
        }
    }

    private EntityLivingBase getClosestTarget() {
        EntityLivingBase bestTarget = null;
        double bestDistance = range.getValue();

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) obj;

            if (entity == mc.thePlayer) continue;
            if (entity.isDead || entity.getHealth() <= 0) continue;

            // --- NEW FILTER LOGIC ---
            if (entity instanceof EntityPlayer) {
                if (!targetPlayers.isEnabled()) continue;
                if (((EntityPlayer) entity).capabilities.isCreativeMode) continue;
                // Simple Anti-Bot: Check if name has strange formatting or invisible
                if (entity.isInvisible()) continue;
            }
            else if (entity instanceof EntityAnimal && !targetAnimals.isEnabled()) continue;
            else if (entity instanceof EntityMob && !targetMobs.isEnabled()) continue;
                // Skip other entities (Armor Stands, etc) if not in above categories
            else if (!(entity instanceof EntityPlayer || entity instanceof EntityAnimal || entity instanceof EntityMob)) continue;

            double dist = mc.thePlayer.getDistanceToEntity(entity);
            if (dist > bestDistance) continue;

            // FOV Check
            float[] rotations = getRotations(entity);
            float yawDiff = getAngleDifference(mc.thePlayer.rotationYaw, rotations[0]);

            if (Math.abs(yawDiff) > fov.getValue() / 2.0) continue;

            bestTarget = entity;
            bestDistance = dist;
        }
        return bestTarget;
    }

    private void faceEntity(EntityLivingBase target) {
        float[] rotations = getRotations(target);
        float targetYaw = rotations[0];
        float targetPitch = rotations[1];

        float yawDiff = getAngleDifference(mc.thePlayer.rotationYaw, targetYaw);
        float pitchDiff = getAngleDifference(mc.thePlayer.rotationPitch, targetPitch);

        double div = (105 - speed.getValue()) + (random.nextDouble() * 2);

        float yawChange = (float) (yawDiff / div);
        mc.thePlayer.rotationYaw += yawChange;

        if (aimPitch.isEnabled()) {
            float pitchChange = (float) (pitchDiff / div);
            mc.thePlayer.rotationPitch += pitchChange;
        }
    }

    private float[] getRotations(Entity entity) {
        double x = entity.posX - mc.thePlayer.posX;
        double z = entity.posZ - mc.thePlayer.posZ;
        double y = (entity.posY + entity.getEyeHeight() - 0.4) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(y, dist) * 180.0 / Math.PI);

        return new float[]{yaw, pitch};
    }

    private float getAngleDifference(float current, float target) {
        float diff = target - current;
        while (diff > 180.0f) diff -= 360.0f;
        while (diff < -180.0f) diff += 360.0f;
        return diff;
    }

    // --- DRAW FOV CIRCLE ---
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;
        if (!isToggled() || !showFOV.isEnabled()) return;

        ScaledResolution sr = new ScaledResolution(mc);
        float midX = sr.getScaledWidth() / 2.0f;
        float midY = sr.getScaledHeight() / 2.0f;

        // Calculate Radius based on FOV angle
        // This approximates how wide the FOV angle looks on your specific screen size
        float fovValue = (float) fov.getValue();
        float radius = (float) (Math.tan(Math.toRadians(fovValue / 2.0f)) / Math.tan(Math.toRadians(mc.gameSettings.fovSetting / 2.0f)) * (sr.getScaledHeight() / 2.0f));

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        // Use Theme Color or White
        int c = SkyblockQOL.themeColor;
        float r = (c >> 16 & 0xFF) / 255.0F;
        float g = (c >> 8 & 0xFF) / 255.0F;
        float b = (c & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0f);

        GL11.glLineWidth(1.0f);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        // Draw Circle
        for (int i = 0; i < 360; i++) {
            double angle = Math.toRadians(i);
            double x = midX + Math.sin(angle) * radius;
            double y = midY + Math.cos(angle) * radius;
            GL11.glVertex2d(x, y);
        }

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}