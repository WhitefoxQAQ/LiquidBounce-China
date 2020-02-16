package net.ccbluex.liquidbounce.ui.font;

import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

import net.ccbluex.liquidbounce.utils.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class UnicodeFontRenderer extends FontRenderer {
    private final UnicodeFont font;

    @SuppressWarnings("unchecked")
    public UnicodeFontRenderer(java.awt.Font awtFont) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"),
                Minecraft.getMinecraft().getTextureManager(), false);
        font = new UnicodeFont(awtFont);
        font.addAsciiGlyphs();

        //加载所有Font
        font.addGlyphs(0, 65535);

        Logger.printconsolemessage("Loading All Fonts to Catch......");
        font.getEffects().add(new ColorEffect(Color.WHITE));
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        String abc = "abcdefghigkimnopqrstuvwxyzABCDEFGHIGKIMKOPQRSTUVWXYZ123456789";
        FONT_HEIGHT = font.getHeight(abc) / 2;
    }

    public int drawString(String string, float x, float y, int color) {
        if (string == null)
            return 0;

        glPushMatrix();
        glScaled(0.5, 0.5, 0.5);

        boolean blend = glIsEnabled(GL_BLEND);
        boolean lighting = glIsEnabled(GL_LIGHTING);
        boolean texture = glIsEnabled(GL_TEXTURE_2D);
        if (!blend)
            glEnable(GL_BLEND);
        if (lighting)
            glDisable(GL_LIGHTING);
        if (texture)
            glDisable(GL_TEXTURE_2D);
        x *= 2;
        y *= 2;

        font.drawString(x, y, string, new org.newdawn.slick.Color(color));

        if (texture)
            glEnable(GL_TEXTURE_2D);
        if (lighting)
            glEnable(GL_LIGHTING);
        if (!blend)
            glDisable(GL_BLEND);
        glPopMatrix();
        return (int)x;
    }
    @Override
    public int drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x + 0.7f, y + 0.7f, -16777216);
        return this.drawString(text, x, y, color);
    }
    public void drawCenteredString(String text, float x, float y, int color) {
        drawString(text, x - (this.getStringWidth(text) / 2), y, Integer.MAX_VALUE);
    }
    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawString(text, x - (this.getStringWidth(text) / 2)+0.7f, y+0.7f, color);
        drawString(text, x - (this.getStringWidth(text) / 2), y, color);
    }
    @Override
    public int getCharWidth(char c) {
        return getStringWidth(Character.toString(c));
    }
    @Override
    public int getStringWidth(String string) {
        return font.getWidth(string) / 2;
    }
    public int getStringHeight(String string) {
        return font.getHeight(string) /2;
    }
}
