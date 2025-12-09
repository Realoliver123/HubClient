package com.realoliver123.hubclient.util;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.gui.GuiSkyblockQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage; // Added this import
import java.io.IOException;
import java.io.InputStream;

public class TaskbarIcon {

    private static TrayIcon trayIcon;

    public static void init() {
        // Check if the system supports the tray (e.g., headless servers won't)
        if (!SystemTray.isSupported()) {
            System.out.println("[HubClient] System tray not supported!");
            return;
        }

        // Run in a separate thread to avoid freezing Minecraft during startup
        new Thread(new Runnable() {
            @Override
            public void run() {
                createTrayIcon();
            }
        }, "HubClient-Tray-Thread").start();
    }

    private static void createTrayIcon() {
        final SystemTray tray = SystemTray.getSystemTray();

        // 1. Load Image
        Image image = loadImage("assets/hubclient/icon.png");
        
        // Fallback: If no icon found, create a colored square
        if (image == null) {
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setColor(new Color(SkyblockQOL.themeColor));
            g.fillRect(0, 0, 16, 16);
            g.dispose();
        }

        // 2. Create Popup Menu
        PopupMenu popup = new PopupMenu();

        // Menu Item: Open GUI
        MenuItem openItem = new MenuItem("Open GUI");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // We must run Minecraft code on the main Minecraft thread!
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        if (Minecraft.getMinecraft().thePlayer != null) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiSkyblockQOL());
                        }
                    }
                });
            }
        });

        // Menu Item: Exit Game
        MenuItem exitItem = new MenuItem("Exit Game");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Minecraft.getMinecraft().shutdown();
            }
        });

        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);

        // 3. Create the Tray Icon
        trayIcon = new TrayIcon(image, "HubClient v" + SkyblockQOL.VERSION, popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("HubClient");

        // Add double-click action to open GUI
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                        @Override
                        public void run() {
                            if (Minecraft.getMinecraft().thePlayer != null) {
                                Minecraft.getMinecraft().displayGuiScreen(new GuiSkyblockQOL());
                            }
                        }
                    });
                }
            }
        });

        // 4. Add to System Tray
        try {
            tray.add(trayIcon);
            System.out.println("[HubClient] Taskbar icon added!");
        } catch (AWTException e) {
            System.out.println("[HubClient] TrayIcon could not be added.");
            e.printStackTrace();
        }
    }

    // Helper to load image from resources
    private static Image loadImage(String path) {
        try {
            // Try standard Minecraft resource loading
            InputStream stream = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("hubclient", "icon.png")).getInputStream();
            return ImageIO.read(stream);
        } catch (Exception e) {
            // Fallback for dev environment
             try {
                 return ImageIO.read(TaskbarIcon.class.getClassLoader().getResourceAsStream(path));
             } catch (Exception ex) {
                 return null;
             }
        }
    }
}