package com.realoliver123.hubclient.features.render;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.settings.BooleanSetting;
import com.realoliver123.hubclient.settings.ModeSetting;
import com.realoliver123.hubclient.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class ESP extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    // --- TARGETS ---
    public static BooleanSetting targetsPlayers = new BooleanSetting("Targets: Players", true);
    public static BooleanSetting targetsAnimals = new BooleanSetting("Targets: Animals", true);
    public static BooleanSetting targetsMobs = new BooleanSetting("Targets: Mobs", true);

    // --- COLORS ---
    public static BooleanSetting espChroma = new BooleanSetting("Chroma", false);
    public static NumberSetting espColorHue = new NumberSetting("Color Hue", 0.5, 0.0, 1.0, 0.01);

    // --- STYLES ---
    public static BooleanSetting box3D = new BooleanSetting("3D Box", true);
    public static BooleanSetting box2D = new BooleanSetting("2D Box", false);
    public static BooleanSetting health = new BooleanSetting("Health Bar", true);
    public static BooleanSetting skeleton = new BooleanSetting("Skeleton", false);
    public static BooleanSetting names = new BooleanSetting("Names", false);
    public static BooleanSetting tracers = new BooleanSetting("Tracers", false);

    public ESP() {
        super("ESP", ModuleCategory.RENDER);
        addSettings(targetsPlayers, targetsAnimals, targetsMobs, espChroma, espColorHue, box3D, box2D, health, skeleton, names, tracers);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isToggled()) return;

        // 1. Setup OpenGL (DISABLE DEPTH FOR X-RAY EFFECT)
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth(); // This allows seeing through walls
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GL11.glLineWidth(1.5f);

        // 2. Calculate Color
        int c;
        if (espChroma.isEnabled()) {
            float hue = (System.currentTimeMillis() % 3000) / 3000f;
            c = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        } else {
            c = Color.HSBtoRGB((float) espColorHue.getValue(), 1.0f, 1.0f);
        }

        float r = (c >> 16 & 0xFF) / 255.0F;
        float g = (c >> 8 & 0xFF) / 255.0F;
        float b = (c & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);

        // 3. Iterate Entities
        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) obj;

            if (entity == mc.thePlayer) continue;

            // Filter
            boolean isValid = false;
            if (entity instanceof EntityPlayer && targetsPlayers.isEnabled()) isValid = true;
            if (entity instanceof EntityAnimal && targetsAnimals.isEnabled()) isValid = true;
            if (entity instanceof EntityMob && targetsMobs.isEnabled()) isValid = true;

            if (!isValid) continue;

            // Interpolate Position
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks - mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks - mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks - mc.getRenderManager().viewerPosZ;

            // --- DRAWING ---

            // Boxes
            if (box3D.isEnabled()) drawBox3D(x, y, z, entity.width, entity.height);
            if (box2D.isEnabled()) drawBox2D(x, y, z, entity.width, entity.height);

            // Tracers
            if (tracers.isEnabled()) drawTracer(x, y, z);

            // Health Bar
            if (health.isEnabled()) {
                drawHealth(entity, x, y, z);
                // Restore ESP color
                GL11.glColor4f(r, g, b, 1.0F);
            }

            // Skeleton
            if (skeleton.isEnabled() && entity instanceof EntityPlayer) {
                drawSkeleton((EntityPlayer) entity, x, y, z, event.partialTicks);
            }

            // Names
            if (names.isEnabled()) {
                drawName(entity, x, y, z);
                // Restore state for next entity (Lines need texture disabled)
                GlStateManager.disableTexture2D();
                GlStateManager.disableDepth(); // VITAL: Ensure depth stays OFF
                GL11.glColor4f(r, g, b, 1.0F);
            }
        }

        // 4. Restore Global State (Only here do we re-enable depth)
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private void drawBox3D(double x, double y, double z, float width, float height) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        double w = width / 2.0 + 0.1;
        double h = height + 0.1;

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex3d(-w, 0, -w); GL11.glVertex3d(-w, 0, w); GL11.glVertex3d(w, 0, w); GL11.glVertex3d(w, 0, -w);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex3d(-w, h, -w); GL11.glVertex3d(-w, h, w); GL11.glVertex3d(w, h, w); GL11.glVertex3d(w, h, -w);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(-w, 0, -w); GL11.glVertex3d(-w, h, -w);
        GL11.glVertex3d(-w, 0, w);  GL11.glVertex3d(-w, h, w);
        GL11.glVertex3d(w, 0, w);   GL11.glVertex3d(w, h, w);
        GL11.glVertex3d(w, 0, -w);  GL11.glVertex3d(w, h, -w);
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

    private void drawBox2D(double x, double y, double z, float width, float height) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

        double w = width / 1.5 + 0.1;
        double h = height + 0.1;

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex3d(-w, 0, 0); GL11.glVertex3d(-w, h, 0); GL11.glVertex3d(w, h, 0); GL11.glVertex3d(w, 0, 0);
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

    private void drawTracer(double x, double y, double z) {
        GlStateManager.pushMatrix();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        GL11.glVertex3d(x, y + 1.0, z);
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

    private void drawHealth(EntityLivingBase entity, double x, double y, double z) {
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float percentage = MathHelper.clamp_float(health / maxHealth, 0.0f, 1.0f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

        GlStateManager.disableDepth(); // Force visibility

        float xOffset = (entity.width / 2.0f) + 0.4f;
        float h = entity.height + 0.1f;
        float w = 0.08f;

        // Back
        GL11.glColor4f(0f, 0f, 0f, 0.6f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(xOffset, 0, 0); GL11.glVertex3d(xOffset + w, 0, 0);
        GL11.glVertex3d(xOffset + w, h, 0); GL11.glVertex3d(xOffset, h, 0);
        GL11.glEnd();

        // Front
        float r = 1.0f - percentage;
        float g = percentage;
        GL11.glColor4f(r, g, 0f, 1.0f);

        float barHeight = h * percentage;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(xOffset, 0, 0); GL11.glVertex3d(xOffset + w, 0, 0);
        GL11.glVertex3d(xOffset + w, barHeight, 0); GL11.glVertex3d(xOffset, barHeight, 0);
        GL11.glEnd();

        GlStateManager.popMatrix();
    }

    private void drawSkeleton(EntityPlayer player, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        float yaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        double radYaw = Math.toRadians(yaw);
        double sin = Math.sin(radYaw);
        double cos = Math.cos(radYaw);
        double shoulderOff = 0.35;
        double hipOff = 0.15;

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x, y + player.getEyeHeight(), z); GL11.glVertex3d(x, y + 0.8, z);
        GL11.glVertex3d(x - cos * shoulderOff, y + 1.4, z - sin * shoulderOff); GL11.glVertex3d(x + cos * shoulderOff, y + 1.4, z + sin * shoulderOff);
        GL11.glVertex3d(x - cos * shoulderOff, y + 1.4, z - sin * shoulderOff); GL11.glVertex3d(x - cos * shoulderOff, y + 0.7, z - sin * shoulderOff);
        GL11.glVertex3d(x + cos * shoulderOff, y + 1.4, z + sin * shoulderOff); GL11.glVertex3d(x + cos * shoulderOff, y + 0.7, z + sin * shoulderOff);
        GL11.glVertex3d(x, y + 0.8, z);
        GL11.glVertex3d(x - cos * hipOff, y + 0.8, z - sin * hipOff); GL11.glVertex3d(x + cos * hipOff, y + 0.8, z + sin * hipOff);
        GL11.glVertex3d(x - cos * hipOff, y + 0.8, z - sin * hipOff); GL11.glVertex3d(x - cos * hipOff, y, z - sin * hipOff);
        GL11.glVertex3d(x + cos * hipOff, y + 0.8, z + sin * hipOff); GL11.glVertex3d(x + cos * hipOff, y, z + sin * hipOff);
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

    private void drawName(EntityLivingBase entity, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + entity.height + 0.6, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

        float scale = 0.04f;
        GlStateManager.scale(-scale, -scale, scale);

        // CRITICAL: We keep depth DISABLED here so it shows through walls
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        String name = entity.getDisplayName().getFormattedText();
        int width = mc.fontRendererObj.getStringWidth(name) / 2;

        // Draw Plate
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-width - 2, -2, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        worldrenderer.pos(-width - 2, 9, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        worldrenderer.pos(width + 2, 9, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        worldrenderer.pos(width + 2, -2, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        mc.fontRendererObj.drawString(name, -width, 0, -1);

        // IMPORTANT: We do NOT re-enable depth here.
        // We leave it disabled so the next entity's box is also X-Ray.
        GlStateManager.popMatrix();
    }
}