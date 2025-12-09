package com.realoliver123.hubclient.gui;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.config.ConfigHandler;
import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.features.ModuleManager;
import com.realoliver123.hubclient.settings.*;
import com.realoliver123.hubclient.util.DrawUtil;
import com.realoliver123.hubclient.util.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import java.io.IOException;
import java.awt.Color;
import java.util.List;

public class GuiSkyblockQOL extends GuiScreen {
    int windowWidth = 420;
    int windowHeight = 240;
    int sidebarWidth = 90;
    int moduleListWidth = 150;

    final int COLOR_BG_DARK = 0xFF121212;
    final int COLOR_SIDEBAR = 0xFF0E0E0E;
    final int COLOR_MODULE = 0xFF181818;
    final int COLOR_MODULE_HOVER = 0xFF202020;
    final int COLOR_DIM = 0xCC000000;
    final int COLOR_TEXT_DIM = 0xFF888888;
    final int COLOR_TEXT_WHT = 0xFFFFFFFF;

    private boolean isDraggingWindow = false;
    private boolean isDraggingSlider = false;
    private int dragOffsetX = 0, dragOffsetY = 0;

    private Module selectedModule = null;
    private Setting selectedSetting = null;

    @Override
    public void initGui() {
        if (SkyblockQOL.guiX == 0 && SkyblockQOL.guiY == 0) {
            SkyblockQOL.guiX = (this.width - windowWidth) / 2;
            SkyblockQOL.guiY = (this.height - windowHeight) / 2;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, COLOR_DIM);
        int startX = SkyblockQOL.guiX;
        int startY = SkyblockQOL.guiY;
        int settingsX = startX + sidebarWidth + moduleListWidth;

        Gui.drawRect(startX, startY, startX + sidebarWidth, startY + windowHeight, COLOR_SIDEBAR);
        Gui.drawRect(startX + sidebarWidth, startY, settingsX, startY + windowHeight, COLOR_BG_DARK);
        Gui.drawRect(settingsX, startY, startX + windowWidth, startY + windowHeight, COLOR_SIDEBAR);

        int activeColor = SkyblockQOL.chromaText ? getChromaColor(0) : SkyblockQOL.themeColor;
        DrawUtil.drawString("HUBCLIENT", startX + 10, startY + 10, activeColor);
        DrawUtil.drawString("v" + SkyblockQOL.VERSION, startX + 10, startY + 20, COLOR_TEXT_DIM);

        int catY = startY + 45;
        ModuleCategory[] categories = ModuleCategory.values();
        for (int i = 0; i < categories.length; i++) {
            boolean isSelected = (i == SkyblockQOL.selectedCategory);
            int color = isSelected ? activeColor : COLOR_TEXT_DIM;
            if (isSelected) Gui.drawRect(startX, catY - 1, startX + 2, catY + 9, activeColor);
            DrawUtil.drawString(categories[i].name, startX + 12, catY, color);
            catY += 18;
        }

        ModuleCategory currentCat = categories[SkyblockQOL.selectedCategory];

        if (currentCat == ModuleCategory.GUI) {
            drawCustomisationTab(startX, startY, mouseX, mouseY);
            drawModuleList(currentCat, startX, startY, mouseX, mouseY, 110);
            if (selectedModule != null) {
                drawSettingsPanel(selectedModule, settingsX, startY, mouseX, mouseY);
            }
        } else {
            drawModuleList(currentCat, startX, startY, mouseX, mouseY, 10);
            if (selectedModule != null) {
                drawSettingsPanel(selectedModule, settingsX, startY, mouseX, mouseY);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawModuleList(ModuleCategory category, int startX, int startY, int mouseX, int mouseY, int yOffset) {
        List<Module> modules = ModuleManager.getModulesByCategory(category);
        int modY = startY + yOffset;
        int cardX = startX + sidebarWidth + 5;
        int cardWidth = moduleListWidth - 10;

        for (Module m : modules) {
            boolean isHovered = mouseX >= cardX && mouseX <= cardX + cardWidth && mouseY >= modY && mouseY <= modY + 24;
            boolean isSelected = (m == selectedModule);
            int bgColor = isSelected ? 0xFF252525 : (isHovered ? COLOR_MODULE_HOVER : COLOR_MODULE);

            Gui.drawRect(cardX, modY, cardX + cardWidth, modY + 24, bgColor);
            if (isSelected) Gui.drawRect(cardX, modY, cardX + 1, modY + 24, SkyblockQOL.themeColor);

            int nameColor = m.isToggled() ? (SkyblockQOL.chromaText ? getChromaColor(0) : SkyblockQOL.themeColor) : COLOR_TEXT_WHT;
            DrawUtil.drawString(m.getName(), cardX + 6, modY + 8, nameColor);

            int switchX = cardX + cardWidth - 25;
            int toggleBg = m.isToggled() ? (SkyblockQOL.chromaText ? getChromaColor(0) : SkyblockQOL.themeColor) : 0xFF555555;
            Gui.drawRect(switchX, modY + 8, switchX + 14, modY + 16, toggleBg);
            modY += 28;
        }
    }

    private void drawSettingsPanel(Module m, int panelX, int panelY, int mouseX, int mouseY) {
        int contentX = panelX + 10;
        int currentY = panelY + 15;
        int width = windowWidth - (sidebarWidth + moduleListWidth) - 20;

        DrawUtil.drawString(m.getName(), contentX, currentY, 0xFFFFFFFF);
        Gui.drawRect(contentX, currentY + 12, contentX + width, currentY + 13, 0xFF333333);
        currentY += 20;

        for (Setting s : m.getSettings()) {
            if (s.hidden) continue;

            if (s instanceof BooleanSetting) {
                BooleanSetting bool = (BooleanSetting) s;
                DrawUtil.drawString(bool.name, contentX, currentY + 2, COLOR_TEXT_DIM);
                int boxSize = 10;
                int boxX = contentX + width - boxSize;
                boolean hover = mouseX >= boxX && mouseX <= boxX + boxSize && mouseY >= currentY && mouseY <= currentY + boxSize;
                Gui.drawRect(boxX, currentY, boxX + boxSize, currentY + boxSize, hover ? 0xFF444444 : 0xFF222222);
                if (bool.isEnabled()) Gui.drawRect(boxX + 2, currentY + 2, boxX + boxSize - 2, currentY + boxSize - 2, SkyblockQOL.themeColor);
                currentY += 18;
            }
            else if (s instanceof NumberSetting) {
                NumberSetting num = (NumberSetting) s;
                String label = num.name + ": " + String.format("%.1f", num.getValue());
                DrawUtil.drawString(label, contentX, currentY, COLOR_TEXT_DIM);
                currentY += 12;
                Gui.drawRect(contentX, currentY, contentX + width, currentY + 4, 0xFF222222);
                double percent = (num.getValue() - num.min) / (num.max - num.min);
                Gui.drawRect(contentX, currentY, contentX + (int)(width * percent), currentY + 4, SkyblockQOL.themeColor);
                currentY += 16; // Visual Height
            }
            else if (s instanceof StringSetting) {
                StringSetting str = (StringSetting) s;
                DrawUtil.drawString(str.name + ":", contentX, currentY, COLOR_TEXT_DIM);
                currentY += 10; // Label space

                Gui.drawRect(contentX, currentY, contentX + width, currentY + 14, 0xFF333333);
                boolean isSelected = (this.selectedSetting == s);
                String display = str.getText() + (isSelected ? "_" : "");
                int maxChars = 20;
                if (display.length() > maxChars) display = display.substring(display.length() - maxChars);
                DrawUtil.drawString(display, contentX + 3, currentY + 3, 0xFFFFFFFF);

                currentY += 20; // Input box space (Total 30px consumed)
            }
            else if (s instanceof ActionSetting) {
                ActionSetting act = (ActionSetting) s;
                boolean hover = mouseX >= contentX && mouseX <= contentX + width && mouseY >= currentY && mouseY <= currentY + 16;
                Gui.drawRect(contentX, currentY, contentX + width, currentY + 16, hover ? 0xFF444444 : 0xFF333333);
                DrawUtil.drawCenteredString(act.name, contentX + width / 2, currentY + 4, 0xFFFFFFFF);
                currentY += 20;
            }
        }
    }

    private void drawCustomisationTab(int startX, int startY, int mouseX, int mouseY) {
        int contentX = startX + sidebarWidth + 10;
        int contentWidth = moduleListWidth - 20;
        int currentY = startY + 20;

        // Font
        Gui.drawRect(contentX, currentY, contentX + contentWidth, currentY + 20, COLOR_MODULE);
        DrawUtil.drawString("Font: " + SkyblockQOL.fontName, contentX + 5, currentY + 6, COLOR_TEXT_WHT);
        currentY += 25;

        // Chroma
        Gui.drawRect(contentX, currentY, contentX + contentWidth, currentY + 20, COLOR_MODULE);
        int chromaLabelColor = SkyblockQOL.chromaText ? getChromaColor(0) : COLOR_TEXT_WHT;
        DrawUtil.drawString("Chroma Mode", contentX + 5, currentY + 6, chromaLabelColor);
        int switchX = contentX + contentWidth - 25;
        int toggleBg = SkyblockQOL.chromaText ? (SkyblockQOL.chromaText ? getChromaColor(0) : SkyblockQOL.themeColor) : 0xFF555555;
        Gui.drawRect(switchX, currentY + 6, switchX + 14, currentY + 14, toggleBg);
        currentY += 30;

        // Color Slider
        DrawUtil.drawString("Theme Color", contentX, currentY, COLOR_TEXT_DIM);
        currentY += 12;
        for (int i = 0; i < contentWidth; i++) {
            float hue = (float) i / (float) contentWidth;
            Gui.drawRect(contentX + i, currentY, contentX + i + 1, currentY + 8, Color.HSBtoRGB(hue, 1.0f, 1.0f));
        }
        int sliderX = contentX + (int)(SkyblockQOL.themeHue * contentWidth);
        Gui.drawRect(sliderX - 1, currentY - 2, sliderX + 1, currentY + 10, 0xFFFFFFFF);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.selectedSetting instanceof StringSetting) {
            StringSetting ss = (StringSetting) this.selectedSetting;
            if (keyCode == 14) {
                if (ss.getText().length() > 0) ss.setText(ss.getText().substring(0, ss.getText().length() - 1));
            } else if (keyCode == 28 || keyCode == 1) {
                this.selectedSetting = null;
            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                ss.setText(ss.getText() + typedChar);
            }
            ConfigHandler.saveConfig();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.selectedSetting != null) this.selectedSetting = null;

        int startX = SkyblockQOL.guiX;
        int startY = SkyblockQOL.guiY;

        // Handle Main GUI Logic
        if (mouseX >= startX && mouseX <= startX + windowWidth && mouseY >= startY && mouseY <= startY + 15) {
            this.isDraggingWindow = true;
            this.dragOffsetX = mouseX - startX;
            this.dragOffsetY = mouseY - startY;
            return;
        }

        if (mouseX > startX && mouseX < startX + sidebarWidth && mouseY > startY) {
            int catY = startY + 45;
            for (int i = 0; i < ModuleCategory.values().length; i++) {
                if (mouseY >= catY && mouseY <= catY + 15) {
                    SkyblockQOL.selectedCategory = i;
                    this.selectedModule = null;
                    Minecraft.getMinecraft().thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                    return;
                }
                catY += 18;
            }
        }

        ModuleCategory currentCat = ModuleCategory.values()[SkyblockQOL.selectedCategory];

        if (currentCat == ModuleCategory.GUI) {
            int contentX = startX + sidebarWidth + 10;
            int contentWidth = moduleListWidth - 20;
            int currentY = startY + 20;

            // Font
            if (mouseX >= contentX && mouseX <= contentX + contentWidth && mouseY >= currentY && mouseY <= currentY + 20) {
                Minecraft.getMinecraft().thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                String[] fonts = {"Minecraft", "Arial", "Verdana", "Comic Sans", "Impact", "Segoe UI", "Tahoma", "Times New", "Courier", "Georgia"};
                int idx = 0;
                for (int i=0; i<fonts.length; i++) if (fonts[i].equals(SkyblockQOL.fontName)) idx = i;
                idx++; if(idx >= fonts.length) idx = 0;
                SkyblockQOL.fontName = fonts[idx];
                if(SkyblockQOL.fontName.equals("Minecraft")) SkyblockQOL.customFont = false;
                else {
                    SkyblockQOL.customFont = true;
                    FontManager.setFont(SkyblockQOL.fontName);
                }
                ConfigHandler.saveConfig();
            }
            currentY += 25;
            // Chroma
            if (mouseX >= contentX && mouseX <= contentX + contentWidth && mouseY >= currentY && mouseY <= currentY + 20) {
                SkyblockQOL.chromaText = !SkyblockQOL.chromaText;
                Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
                ConfigHandler.saveConfig();
            }
            currentY += 30;
            // Slider
            currentY += 12;
            if (mouseX >= contentX && mouseX <= contentX + contentWidth && mouseY >= currentY && mouseY <= currentY + 10) {
                this.isDraggingSlider = true;
                updateThemeColor(mouseX, contentX, contentWidth);
            }

            // GUI Tab Module List (offset 110px)
            List<Module> modules = ModuleManager.getModulesByCategory(currentCat);
            int modY = startY + 110;
            int cardX = startX + sidebarWidth + 5;
            int cardWidth = moduleListWidth - 10;

            for (Module m : modules) {
                if (mouseX >= cardX && mouseX <= cardX + cardWidth && mouseY >= modY && mouseY <= modY + 24) {
                    int switchX = cardX + cardWidth - 25;
                    if (mouseX >= switchX && mouseX <= switchX + 14) {
                        m.toggle();
                        Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
                        ConfigHandler.saveConfig();
                    } else {
                        this.selectedModule = m;
                        Minecraft.getMinecraft().thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                    }
                    return;
                }
                modY += 28;
            }
            if (selectedModule != null) checkSettingsClicks(startX, startY, mouseX, mouseY);

        } else {
            // Normal Tab
            List<Module> modules = ModuleManager.getModulesByCategory(currentCat);
            int modY = startY + 10;
            int cardX = startX + sidebarWidth + 5;
            int cardWidth = moduleListWidth - 10;

            for (Module m : modules) {
                if (mouseX >= cardX && mouseX <= cardX + cardWidth && mouseY >= modY && mouseY <= modY + 24) {
                    int switchX = cardX + cardWidth - 25;
                    if (mouseX >= switchX && mouseX <= switchX + 14) {
                        m.toggle();
                        Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
                        ConfigHandler.saveConfig();
                    } else {
                        this.selectedModule = m;
                        Minecraft.getMinecraft().thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                    }
                    return;
                }
                modY += 28;
            }
            if (selectedModule != null) checkSettingsClicks(startX, startY, mouseX, mouseY);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void checkSettingsClicks(int startX, int startY, int mouseX, int mouseY) {
        int panelX = startX + sidebarWidth + moduleListWidth;
        int contentX = panelX + 10;
        int currentY = startY + 35;
        int width = windowWidth - (sidebarWidth + moduleListWidth) - 20;

        if (mouseX > panelX && mouseX < startX + windowWidth) {
            for (Setting s : selectedModule.getSettings()) {
                if (s.hidden) continue;

                if (s instanceof BooleanSetting) {
                    BooleanSetting bool = (BooleanSetting) s;
                    int boxSize = 10;
                    int boxX = contentX + width - boxSize;
                    if (mouseX >= boxX && mouseX <= boxX + boxSize && mouseY >= currentY && mouseY <= currentY + boxSize) {
                        bool.toggle();
                        Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
                        ConfigHandler.saveConfig();
                    }
                    currentY += 18;
                } else if (s instanceof NumberSetting) {
                    NumberSetting num = (NumberSetting) s;
                    if (mouseY >= currentY && mouseY <= currentY + 16 && mouseX >= contentX && mouseX <= contentX + width) {
                        double percent = (double)(mouseX - contentX) / (double)width;
                        num.setValue(num.min + (num.max - num.min) * percent);
                        ConfigHandler.saveConfig();
                    }
                    // FIXED: Logic was 28, Visual was 16. Visual updated to 16.
                    currentY += 28;
                } else if (s instanceof StringSetting) {
                    // FIXED: StringSetting takes 30px (10 label + 20 box)
                    if (mouseY >= currentY + 10 && mouseY <= currentY + 10 + 14 && mouseX >= contentX && mouseX <= contentX + width) {
                        this.selectedSetting = s;
                        Minecraft.getMinecraft().thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                    }
                    // This was the bug! Changed from 20 to 30 to match visuals.
                    currentY += 30;
                } else if (s instanceof ActionSetting) {
                    ActionSetting act = (ActionSetting) s;
                    if (mouseX >= contentX && mouseX <= contentX + width && mouseY >= currentY && mouseY <= currentY + 16) {
                        Minecraft.getMinecraft().thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                        act.perform();
                    }
                    currentY += 20;
                }
            }
        }
    }

    private void updateThemeColor(int mouseX, int contentX, int width) {
        float percent = (float)(mouseX - contentX) / (float)width;
        percent = Math.max(0, Math.min(1, percent));
        SkyblockQOL.themeHue = percent;
        SkyblockQOL.themeColor = Color.HSBtoRGB(percent, 1.0f, 1.0f);
    }

    @Override
    protected void mouseClickMove(int x, int y, int btn, long time) {
        if (isDraggingWindow) {
            SkyblockQOL.guiX = x - dragOffsetX;
            SkyblockQOL.guiY = y - dragOffsetY;
        }
        if (isDraggingSlider) {
            int contentX = SkyblockQOL.guiX + sidebarWidth + 10;
            int contentWidth = moduleListWidth - 20;
            updateThemeColor(x, contentX, contentWidth);
        }
    }

    @Override
    protected void mouseReleased(int x, int y, int state) {
        isDraggingWindow = false;
        isDraggingSlider = false;
        ConfigHandler.saveConfig();
    }

    @Override public boolean doesGuiPauseGame() { return false; }
    private int getChromaColor(long offset) { return Color.HSBtoRGB((System.currentTimeMillis() + offset) % 2000L / 2000.0f, 0.8f, 0.8f); }
}