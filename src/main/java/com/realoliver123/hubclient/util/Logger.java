package com.realoliver123.hubclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Logger {
    public static void sendNote(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[Macro] " + message));
        }
        System.out.println("[Macro] " + message);
    }
}