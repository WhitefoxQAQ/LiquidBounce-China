package net.ccbluex.liquidbounce.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Logger {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void printmessage(final String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
    public static void printinfo(final String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§7[§9LiquidBounce§7]:"+message));
    }

    public static void printconsolemessage(String s) {
        System.out.println("[LiquidBounce]:"+s);
    }
}
