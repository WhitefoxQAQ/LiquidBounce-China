package net.ccbluex.liquidbounce.ui.font;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.Logger;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.*;

public class FontManager {
    public static UnicodeFontRenderer yahei18;
    public static UnicodeFontRenderer yahei20;
    public static UnicodeFontRenderer yahei22;
    public static UnicodeFontRenderer yahei24;

    public static void loadFont() {
        try {
            InputStream inputStream = new FileInputStream(new File(LiquidBounce.fileManager.fontsDir, "yahei.ttf"));

            Font font;
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            Logger.printconsolemessage("Loading Fonts......");
            yahei18 = new UnicodeFontRenderer(font.deriveFont(18F));
            yahei20 = new UnicodeFontRenderer(font.deriveFont(20F));
            yahei22 = new UnicodeFontRenderer(font.deriveFont(22F));
            yahei24 = new UnicodeFontRenderer(font.deriveFont(24F));
        } catch (FileNotFoundException e) {
            Logger.printconsolemessage("yahei.ttf Font Not Found! Please put yahei.ttf to Fonts dir!");
        } catch (FontFormatException e) {
            e.printStackTrace();
            Logger.printconsolemessage("Font Format Error!");
        } catch (IOException e) {
            Logger.printconsolemessage("Font Format Error! Check Your Font File!");
            e.printStackTrace();
        }
        if (Minecraft.getMinecraft().gameSettings.language != null) {
            yahei18.setUnicodeFlag(true);
            yahei20.setUnicodeFlag(true);
            yahei22.setUnicodeFlag(true);
            yahei24.setUnicodeFlag(true);

            yahei18.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
            Logger.printconsolemessage("Loading YaHei18......");
            yahei20.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
            Logger.printconsolemessage("Loading YaHei20......");
            yahei22.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
            Logger.printconsolemessage("Loading YaHei22......");
            yahei24.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
            Logger.printconsolemessage("Loading YaHei24......");
        }

    }
}
