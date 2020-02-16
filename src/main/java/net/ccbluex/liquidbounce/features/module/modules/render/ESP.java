/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.Colors;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.shader.FramebufferShader;
import net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader;
import net.ccbluex.liquidbounce.utils.render.shader.shaders.OutlineShader;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(name = "ESP", description = "Allows you to see targets through walls.", category = ModuleCategory.RENDER)
public class ESP extends Module {
    public static Map<EntityLivingBase, double[]> entityPositionstop = new HashMap();
    public static Map<EntityLivingBase, double[]> entityPositionsbottom = new HashMap();
    public static boolean renderNameTags = true;
    public final ListValue modeValue = new ListValue("Mode", new String[]{"Box", "OtherBox", "WireFrame", "2D", "Other2D", "Outline", "ShaderOutline", "ShaderGlow"}, "Box");
    public final FloatValue outlineWidth = new FloatValue("Outline-Width", 3F, 0.5F, 5F);
    public final FloatValue wireframeWidth = new FloatValue("WireFrame-Width", 2F, 0.5F, 5F);
    private final FloatValue shaderOutlineRadius = new FloatValue("ShaderOutline-Radius", 1.35F, 1F, 2F);
    private final FloatValue shaderGlowRadius = new FloatValue("ShaderGlow-Radius", 2.3F, 2F, 3F);
    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 255, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    private final BoolValue colorTeam = new BoolValue("Team", false);
    private final BoolValue armor = new BoolValue("ArmorBar", true);
    private final BoolValue names = new BoolValue("Names", true);
    public int[] ColorCode = new int[32];
    private double gradualFOVModifier;

    public static double getIncremental(double val, double inc) {
        double one = 1 / inc;
        return Math.round(val * one) / one;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color = null;
        if (fractions != null) {
            if (colors != null) {
                if (fractions.length == colors.length) {
                    int[] indicies = getFractionIndicies(fractions, progress);

                    if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length) {
                        return colors[0];
                    }
                    float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
                    Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};

                    float max = range[1] - range[0];
                    float value = progress - range[0];
                    float weight = value / max;

                    color = blend(colorRange[0], colorRange[1], 1f - weight);
                } else {
                    throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
                }
            } else {
                throw new IllegalArgumentException("Colours can't be null");
            }
        } else {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        return color;
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];

        int startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;

        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float rgb1[] = new float[3];
        float rgb2[] = new float[3];

        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);

        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }

        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color;
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final String mode = modeValue.get();

        entityPositionstop.clear();
        entityPositionsbottom.clear();
        float pTicks = mc.timer.renderPartialTicks;

        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != null && entity != mc.thePlayer && EntityUtils.isSelected(entity, false)) {
                final EntityLivingBase entityLiving = (EntityLivingBase) entity;
                switch (mode.toLowerCase()) {
                    case "box":
                    case "otherbox": {
                        RenderUtils.drawEntityBox(entity, getColor(entityLiving), !mode.equalsIgnoreCase("otherbox"));
                        break;
                    }
                    case "2d": {
                        final RenderManager renderManager = mc.getRenderManager();
                        final Timer timer = mc.timer;

                        final double posX = entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX;
                        final double posY = entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY;
                        final double posZ = entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ;

                        RenderUtils.draw2D(entityLiving, posX, posY, posZ, getColor(entityLiving).getRGB(), Color.BLACK.getRGB());
                        break;
                    }
                    case "other2d": {
                        double x;
                        double y = entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
                        double z;
                        x = entityLiving.lastTickPosX + ((entityLiving.posX + 10) - (entityLiving.lastTickPosX + 10)) * pTicks - mc.getRenderManager().viewerPosX;
                        z = entityLiving.lastTickPosZ + ((entityLiving.posZ + 10) - (entityLiving.lastTickPosZ + 10)) * pTicks - mc.getRenderManager().viewerPosZ;
                        y += entityLiving.height + 0.5D;
                        double[] convertedPoints = convertTo2D(x, y, z);
                        double xd = Math.abs(convertTo2D(x, y + 1.0D, z, entityLiving)[1] - convertTo2D(x, y, z, entityLiving)[1]);
                        assert convertedPoints != null;
                        if ((convertedPoints[2] >= 0.0D) && (convertedPoints[2] < 1.0D)) {
                            entityPositionstop.put(entityLiving, new double[]{convertedPoints[0], convertedPoints[1], xd, convertedPoints[2]});
                            y = entityLiving.lastTickPosY + ((entityLiving.posY - 2.2) - (entityLiving.lastTickPosY - 2.2)) * pTicks - mc.getRenderManager().viewerPosY;
                            entityPositionsbottom.put(entityLiving, new double[]{convertTo2D(x, y, z)[0], convertTo2D(x, y, z)[1], xd, convertTo2D(x, y, z)[2]});
                        }
                        break;
                    }
                }
            }

        }

    }


    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final String mode = modeValue.get();
        switch (mode.toLowerCase()) {
            case "shaderoutline": {
                renderShaderEsp(event, OutlineShader.OUTLINE_SHADER, shaderOutlineRadius.get());
                break;
            }
            case "shaderglow": {
                renderShaderEsp(event, GlowShader.GLOW_SHADER, shaderGlowRadius.get());
                break;
            }
            case "other2d": {
                renderOther2d();
                break;
            }
        }
    }

    @EventTarget
    private void renderShaderEsp(Render2DEvent event, FramebufferShader shader, float radius) {
        shader.startDraw(event.getPartialTicks());

        renderNameTags = false;
        try {
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (!EntityUtils.isSelected(entity, false))
                    continue;

                mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
            }
        } catch (final Exception ex) {
            ClientUtils.getLogger().error("An error occurred while rendering all entities for shader esp", ex);
        }

        renderNameTags = true;
        shader.stopDraw(getColor(null), radius, 1F);
    }


    private void renderOther2d() {
        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);
        GL11.glScaled(twoDscale, twoDscale, twoDscale);
        for (EntityLivingBase ent : entityPositionstop.keySet()) {
            if (ent != null &&ent instanceof EntityLivingBase && ent != mc.thePlayer && EntityUtils.isSelected(ent, false)) {
                renderOther2dEntity((EntityLivingBase)ent);
            }
        }
        GL11.glScalef(1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }

    private void renderOther2dEntity(EntityLivingBase ent) {
        double[] renderPositions = entityPositionstop.get(ent);
        double[] renderPositionsBottom = entityPositionsbottom.get(ent);
        if ((renderPositions[3] > 0.0D) || (renderPositions[3] <= 1.0D)) {
            GlStateManager.pushMatrix();
            scale(ent);
            float y = (float) renderPositions[1];
            float endy = (float) renderPositionsBottom[1];
            float meme = endy - y;
            float x = (float) renderPositions[0] - (meme / 4f);
            float endx = (float) renderPositionsBottom[0] + (meme / 4f);
            if (x > endx) {
                endx = x;
                x = (float) renderPositionsBottom[0] + (meme / 4f);
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(2, 2, 2);
            GlStateManager.popMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            int color = parseEspColorFromNameTag(ent);

            double xDiff = (endx - x) / 4.0;
            double x2Diff = (endx - x) / 3;
            double yDiff = (endy - y) / 4.0;
            renderOtherBox(y, endy, x, endx, color, x2Diff, yDiff);
            if (ent instanceof EntityPlayer) {
                if (!LiquidBounce.moduleManager.getModule(NameTags.class).getState() && (names.get())) {
                    GlStateManager.pushMatrix();
                    String renderName = ent.getName();
                    FontRenderer font = mc.fontRendererObj;
                    float meme2 = ((endx - x) / 2 - (font.getStringWidth(renderName) / 2f));
                    font.drawStringWithShadow(renderName + " " + (int) mc.thePlayer.getDistanceToEntity(ent) + "m", (x + meme2), (y - font.FONT_HEIGHT - 5), -1);
                    GlStateManager.popMatrix();
                }
                if (armor.get()) {
                    renderOtherArmor((EntityPlayer) ent, y, endy, endx);
                }
            }

            float health = ent.getHealth();
            float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
            Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            float progress = health / ((EntityLivingBase) ent).getMaxHealth();
            Color customColor = health >= 0.0f ? blendColors(fractions, colors, progress).brighter() : Color.RED;
            double difference = y - endy + 0.5;
            double healthLocation = endy + difference * (double) progress;
            RenderUtils.drawRectBordered(x - 6.5, y - 0.5, x - 2.5, endy, 1.0, Colors.getColor(0, 100), Colors.getColor(0, 150));
            RenderUtils.rectangle(x - 5.5, endy - 1.0, x - 3.5, healthLocation, customColor.getRGB());
            if (-difference > 50.0) {
                for (int i = 1; i < 10; ++i) {
                    double dThing = difference / 10.0 * (double) i;
                    RenderUtils.rectangle(x - 6.5, endy - 0.5 + dThing, x - 2.5, endy - 0.5 + dThing - 1.0, Colors.getColor(0));
                }
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0f, 1.0f, 1.0f);
            final String nigger = (int) getIncremental(progress * 100, 1.0) + "%";
            mc.fontRendererObj.drawStringWithShadow(nigger + "", (float) x  - 30, (float) healthLocation  - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    private void renderOtherArmor(EntityPlayer ent, float y, float endy, float endx) {
        float var1 = (endy - y) / 4;
        ItemStack stack = ent.getEquipmentInSlot(4);
        if (stack != null) {
            RenderUtils.drawRectBordered(endx + 1, y + 1, endx + 6, y + var1, 1, Colors.getColor(28, 156, 179, 100), Colors.getColor(0, 255));
            float diff1 = (y + var1 - 1) - (y + 2);
            double percent = 1 - (double) stack.getItemDamage() / (double) stack.getMaxDamage();
            RenderUtils.rectangle(endx + 2, y + var1 - 1, endx + 5, y + var1 - 1 - (diff1 * percent), Colors.getColor(78, 206, 229));
            mc.fontRendererObj.drawStringWithShadow(stack.getMaxDamage() - stack.getItemDamage() + "", endx + 7, (y + var1 - 1 - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
        }
        ItemStack stack2 = ent.getEquipmentInSlot(3);
        if (stack2 != null) {
            RenderUtils.drawRectBordered(endx + 1, y + var1, endx + 6, y + var1 * 2, 1, Colors.getColor(28, 156, 179, 100), Colors.getColor(0, 255));
            float diff1 = (y + var1 * 2) - (y + var1 + 2);
            double percent = 1 - (double) stack2.getItemDamage() * 1 / (double) stack2.getMaxDamage();
            RenderUtils.rectangle(endx + 2, (y + var1 * 2), endx + 5, (y + var1 * 2) - (diff1 * percent), Colors.getColor(78, 206, 229));
            mc.fontRendererObj.drawStringWithShadow(stack2.getMaxDamage() - stack2.getItemDamage() + "", endx + 7, ((y + var1 * 2) - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
        }
        ItemStack stack3 = ent.getEquipmentInSlot(2);
        if (stack3 != null) {
            RenderUtils.drawRectBordered(endx + 1, y + var1 * 2, endx + 6, y + var1 * 3, 1, Colors.getColor(28, 156, 179, 100), Colors.getColor(0, 255));
            float diff1 = (y + var1 * 3) - (y + var1 * 2 + 2);
            double percent = 1 - (double) stack3.getItemDamage() * 1 / (double) stack3.getMaxDamage();
            RenderUtils.rectangle(endx + 2, (y + var1 * 3), endx + 5, (y + var1 * 3) - (diff1 * percent), Colors.getColor(78, 206, 229));
            mc.fontRendererObj.drawStringWithShadow(stack3.getMaxDamage() - stack3.getItemDamage() + "", endx + 7, ((y + var1 * 3) - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
        }
        ItemStack stack4 = ent.getEquipmentInSlot(1);
        if (stack4 != null) {
            RenderUtils.drawRectBordered(endx + 1, y + var1 * 3, endx + 6, y + var1 * 4, 1, Colors.getColor(28, 156, 179, 100), Colors.getColor(0, 255));
            float diff1 = (y + var1 * 4) - (y + var1 * 3 + 2);
            double percent = 1 - (double) stack4.getItemDamage() * 1 / (double) stack4.getMaxDamage();
            RenderUtils.rectangle(endx + 2, (y + var1 * 4) - 1, endx + 5, (y + var1 * 4) - (diff1 * percent), Colors.getColor(78, 206, 229));
            mc.fontRendererObj.drawStringWithShadow(stack4.getMaxDamage() - stack4.getItemDamage() + "", endx + 7, ((y + var1 * 4) - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
        }
    }

    private void renderOtherBox(float y, float endy, float x, float endx, int color, double x2Diff, double yDiff) {
        RenderUtils.rectangle(x + 0.5, y + 0.5, x + 1.5, y + yDiff + 0.5, color);
        RenderUtils.rectangle(x + 0.5, endy - 0.5, x + 1.5, endy - yDiff - 0.5, color);
        RenderUtils.rectangle(x - 0.5, y + 0.5, x + 0.5, y + yDiff + 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + 1.5, y + 2.5, x + 2.5, y + yDiff + 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x - 0.5, y + yDiff + 0.5, x + 2.5, y + yDiff + 1.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x - 0.5, endy - 0.5, x + 0.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + 1.5, endy - 2.5, x + 2.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x - 0.5, endy - yDiff - 0.5, x + 2.5, endy - yDiff - 1.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + 1.0, y + 0.5, x + x2Diff, y + 1.5, color);
        RenderUtils.rectangle(x - 0.5, y - 0.5, x + x2Diff, y + 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + 1.5, y + 1.5, x + x2Diff, y + 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + x2Diff, y - 0.5, x + x2Diff + 1.0, y + 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + 1.0, endy - 0.5, x + x2Diff, endy - 1.5, color);
        RenderUtils.rectangle(x - 0.5, endy + 0.5, x + x2Diff, endy - 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + 1.5, endy - 1.5, x + x2Diff, endy - 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(x + x2Diff, endy + 0.5, x + x2Diff + 1.0, endy - 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 0.5, y + 0.5, endx - 1.5, y + yDiff + 0.5, color);
        RenderUtils.rectangle(endx - 0.5, endy - 0.5, endx - 1.5, endy - yDiff - 0.5, color);
        RenderUtils.rectangle(endx + 0.5, y + 0.5, endx - 0.5, y + yDiff + 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 1.5, y + 2.5, endx - 2.5, y + yDiff + 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx + 0.5, y + yDiff + 0.5, endx - 2.5, y + yDiff + 1.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx + 0.5, endy - 0.5, endx - 0.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 1.5, endy - 2.5, endx - 2.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx + 0.5, endy - yDiff - 0.5, endx - 2.5, endy - yDiff - 1.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 1.0, y + 0.5, endx - x2Diff, y + 1.5, color);
        RenderUtils.rectangle(endx + 0.5, y - 0.5, endx - x2Diff, y + 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 1.5, y + 1.5, endx - x2Diff, y + 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - x2Diff, y - 0.5, endx - x2Diff - 1.0, y + 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 1.0, endy - 0.5, endx - x2Diff, endy - 1.5, color);
        RenderUtils.rectangle(endx + 0.5, endy + 0.5, endx - x2Diff, endy - 0.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - 1.5, endy - 1.5, endx - x2Diff, endy - 2.5, Colors.getColor(0, 150));
        RenderUtils.rectangle(endx - x2Diff, endy + 0.5, endx - x2Diff - 1.0, endy - 2.5, Colors.getColor(0, 150));
    }


    private int parseEspColorFromNameTag(EntityLivingBase ent) {
        String text = ent.getDisplayName().getFormattedText();
        if (Character.toLowerCase(text.charAt(0)) == '\247') {
            char oneMore = Character.toLowerCase(text.charAt(1));
            int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);

            if (colorCode < 16) {
                try {
                    int newColor = ColorCode[colorCode];
                    return Colors.getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        } else return Colors.getColor(255, 255, 255, 255);

        if (ent instanceof EntityLivingBase) {
            if (AntiBot.isBot((EntityLivingBase) ent)) {
                return Colors.getColor(100, 100, 100, 255);
            }
        }
        return 0;
    }


    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        return convertTo2D(x, y, z);
    }


    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }

    private void scale(Entity ent) {
        float scale = (float) 1;
        float target = scale * (mc.gameSettings.fovSetting
                / (mc.gameSettings.fovSetting/*
         * *
         * mc.thePlayer.getFovModifier()
         *//* .func_175156_o() */));
        if ((this.gradualFOVModifier == 0.0D) || (Double.isNaN(this.gradualFOVModifier))) {
            this.gradualFOVModifier = target;
        }
        this.gradualFOVModifier += (target - this.gradualFOVModifier) / (mc.getDebugFPS() * 0.7D);

        scale = (float) (scale * this.gradualFOVModifier);

        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }

    public Color getColor(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

            if (entityLivingBase.hurtTime > 0)
                return Color.RED;

            if (EntityUtils.isFriend(entityLivingBase))
                return Color.BLUE;

            if (colorTeam.get()) {
                final char[] chars = entityLivingBase.getDisplayName().getFormattedText().toCharArray();
                int color = Integer.MAX_VALUE;
                final String colors = "0123456789abcdef";

                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] != '§' || i + 1 >= chars.length)
                        continue;

                    final int index = colors.indexOf(chars[i + 1]);

                    if (index == -1)
                        continue;

                    color = ColorUtils.hexColors[index];
                    break;
                }

                return new Color(color);
            }
        }

        return colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
    }
}
