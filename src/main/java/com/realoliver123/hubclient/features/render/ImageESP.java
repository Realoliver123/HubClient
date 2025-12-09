package com.realoliver123.hubclient.features.render;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.config.ConfigHandler;
import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.settings.ActionSetting;
import com.realoliver123.hubclient.settings.BooleanSetting;
import com.realoliver123.hubclient.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageESP extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    // Texture Caching
    private ResourceLocation loadedTexture = null;
    private String lastLoadedPath = "";

    // Settings
    public static BooleanSetting playersOnly = new BooleanSetting("Players Only", true);
    public static NumberSetting scale = new NumberSetting("Scale", 1.0, 0.1, 5.0, 0.1);

    // --- NEW OPACITY SETTING ---
    public static NumberSetting opacity = new NumberSetting("Opacity", 1.0, 0.1, 1.0, 0.1);

    public ActionSetting changePath = new ActionSetting("Select Image...", this::openFileChooser);

    public ImageESP() {
        super("Image ESP", ModuleCategory.RENDER);
        this.addSettings(playersOnly, scale, opacity, changePath);
    }

    public void openFileChooser() {
        Thread t = new Thread(() -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select ESP Image");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    SkyblockQOL.espImagePath = selectedFile.getAbsolutePath();
                    ConfigHandler.saveConfig();
                    loadedTexture = null; // Force reload
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        t.start();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (SkyblockQOL.espImagePath.isEmpty()) return;

        // 1. Load Texture if needed
        if (loadedTexture == null || !SkyblockQOL.espImagePath.equals(lastLoadedPath)) {
            loadTexture();
        }
        if (loadedTexture == null) return;

        // 2. Render Loop
        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof Entity)) continue;
            Entity entity = (Entity) obj;
            if (entity == mc.thePlayer) continue;
            if (playersOnly.isEnabled() && !(entity instanceof EntityPlayer)) continue;

            renderImage(entity, event.partialTicks);
        }
    }

    private void loadTexture() {
        try {
            File f = new File(SkyblockQOL.espImagePath);
            if (!f.exists()) return;
            BufferedImage img = ImageIO.read(f);
            DynamicTexture tex = new DynamicTexture(img);
            loadedTexture = mc.getTextureManager().getDynamicTextureLocation("custom_esp", tex);
            lastLoadedPath = SkyblockQOL.espImagePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderImage(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        float s = (float) scale.getValue();
        float alpha = (float) opacity.getValue(); // Get Opacity Value

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + entity.height + 0.5, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.5 * s, -0.5 * s, 0.5 * s);

        // --- TRANSPARENCY SETUP ---
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false); // Crucial for seeing through it
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        // Apply Opacity here
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        mc.getTextureManager().bindTexture(loadedTexture);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        worldrenderer.pos(-1, -1, 0).tex(0, 0).endVertex();
        worldrenderer.pos(-1, 1, 0).tex(0, 1).endVertex();
        worldrenderer.pos(1, 1, 0).tex(1, 1).endVertex();
        worldrenderer.pos(1, -1, 0).tex(1, 0).endVertex();

        tessellator.draw();

        // Reset States
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}