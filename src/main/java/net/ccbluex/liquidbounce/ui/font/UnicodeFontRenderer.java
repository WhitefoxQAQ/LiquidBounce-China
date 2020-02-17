package net.ccbluex.liquidbounce.ui.font;

import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

import net.ccbluex.liquidbounce.utils.Logger;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
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

    @Override
    public int drawString(String string, float x, float y, int color, boolean dropShadow) {
        if (string == null) {
            return 0;
        }
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        boolean blend = GL11.glIsEnabled(3042);
        boolean lighting = GL11.glIsEnabled(2896);
        boolean texture = GL11.glIsEnabled(3553);
        if (!blend) {
            GL11.glEnable(3042);
        }
        if (lighting) {
            GL11.glDisable(2896);
        }
        if (texture) {
            GL11.glDisable(3553);
        }
        this.font.drawString(x *= 2.0f, y *= 2.0f, string, new org.newdawn.slick.Color(color));
        if (texture) {
            GL11.glEnable(3553);
        }
        if (lighting) {
            GL11.glEnable(2896);
        }
        if (!blend) {
            GL11.glDisable(3042);
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glPopMatrix();
        GlStateManager.bindTexture(0);
        return (int) x;
    }

    @Override
    public int drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x + 0.6f, y + 0.6f, -16777216, false);
        return this.drawString(text, x, y, color, false);
    }

    @Override
    public int getCharWidth(char c) {
        return this.getStringWidth(Character.toString(c));
    }

    @Override
    public int getStringWidth(String string) {
        return this.font.getWidth(string) / 2;
    }

    public int getStringHeight(String string) {
        return this.font.getHeight(string) / 2;
    }
    public int drawCenteredStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2)+0.6f, y+0.6f, color, false);
       return this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y, color, false);
    }

    public int drawCenteredString(String text, float x, float y, int color) {
       return this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y, color, false);
    }
}