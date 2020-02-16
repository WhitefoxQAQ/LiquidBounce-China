package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.AttackEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.utils.Colors;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import static net.ccbluex.liquidbounce.utils.Logger.mc;

@ModuleInfo(name = "TargetHUD", description = "undefiend.", category = ModuleCategory.RENDER)
public class TargetHUD extends Module {
    KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
    @EventTarget
    public void onRender2D(Render2DEvent e){
        Entity entity = killAura.getTarget();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        float width = scaledRes.getScaledWidth();
        float height = scaledRes.getScaledHeight();
        if(entity instanceof EntityLivingBase){
            if (entity != null && !entity.isDead && (mc.thePlayer.getDistanceToEntity(entity) < 8) ) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2 + 10), (float)(height - 90), 0.0F);
                RenderUtils.rectangle(0.0D, 0.0D, 125.0D, 36.0D, Colors.getColor(0, 150));
                mc.fontRendererObj.drawStringWithShadow(entity.getName(), 38.0F, 2.0F, -1);
                float health = ((EntityLivingBase) entity).getHealth();
                float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
                Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                float progress = health / ((EntityLivingBase) entity).getMaxHealth();
                Color customColor = ((EntityLivingBase) entity).hurtTime > 5 ? Color.RED : (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter() : Color.RED;
                double width1 = (double)mc.fontRendererObj.getStringWidth(entity.getName());
                width1 = getIncremental(width1, 10.0D);
                if (width1 < 50.0D) {
                    width1 = 50.0D;
                }

                double healthLocation = width1 * (double)progress;
                RenderUtils.rectangle(37.5D, 11.5D, 38.0D + healthLocation + 0.5D, 14.5D, customColor.getRGB());
                RenderUtils.drawRectBordered(37.0D, 11.0D, 39.0D + width1, 15.0D, 0.5D, Colors.getColor(0, 0), Colors.getColor(0));

                for(int i = 1; i < 10; ++i) {
                    double dThing = width1 / 10.0D * (double)i;
                    RenderUtils.rectangle(38.0D + dThing, 11.0D, 38.0D + dThing + 0.5D, 15.0D, Colors.getColor(0));
                }

                RenderUtils.drawRectBordered(1.0D, 1.0D, 35.0D, 35.0D, 0.5D, Colors.getColor(0, 0), Colors.getColor(255));
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                String str = "HP: " + (int)health + " | Dist: " + (int)mc.thePlayer.getDistanceToEntity(entity);
                mc.fontRendererObj.drawStringWithShadow(str, 76.0F, 35.0F, -1);
                String str2 = String.format("Yaw: %s Pitch: %s BodyYaw: %s", (int)entity.rotationYaw, (int)entity.rotationPitch, (int) ((EntityLivingBase) entity).renderYawOffset);
                mc.fontRendererObj.drawStringWithShadow(str2, 76.0F, 47.0F, -1);
                String str3 = String.format("TOG: %s HURT: %s TE: %s", entity.onGround, ((EntityLivingBase) entity).hurtTime, entity.ticksExisted);
                mc.fontRendererObj.drawStringWithShadow(str3, 76.0F, 59.0F, -1);
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderUtils.drawEntityOnScreen(18, 34, 16, 0.0F, 9.0F, (EntityLivingBase) entity);

                GlStateManager.popMatrix();
            }
        }
    }
    public static double getIncremental(double val, double inc) {
        double one = 1 / inc;
        return Math.round(val * one) / one;
    }
    public static int[] getFractionIndicies(final float[] fractions, final float progress) {
        final int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {}
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        Color color = null;
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length == colors.length) {
            final int[] indicies = getFractionIndicies(fractions, progress);
            final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
            final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
            final float max = range[1] - range[0];
            final float value = progress - range[0];
            final float weight = value / max;
            color = blend(colorRange[0], colorRange[1], 1.0f - weight);
            return color;
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }
    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float)ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        }
        else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        }
        else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        }
        else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            final NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format((double)red) + "; " + nf.format((double)green) + "; " + nf.format((double)blue));
            exp.printStackTrace();
        }
        return color3;
    }
}
