package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CNHypixelSpeed extends SpeedMode {
    boolean collided = false, lessSlow;
    public static int stage;
    double less, stair;
    private double speed;
    public boolean shouldslow = false;
    private final MSTimer timer = new MSTimer();
    private final MSTimer lastCheck = new MSTimer();
    public CNHypixelSpeed() {
        super("CNHypixel");
    }

    @Override
    public void onEnable() {
        boolean player = mc.thePlayer == null;
        collided = player ? false : mc.thePlayer.isCollidedHorizontally;
        lessSlow = false;
        less = 0;
        stage = 2;
        mc.timer.timerSpeed = 1;
        super.onEnable();
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.isCollidedHorizontally) {
            collided = true;
        }
        if (collided) {
            mc.timer.timerSpeed = 1;
            stage = -1;
        }
        if (stair > 0)
            stair -= 0.25;
        less -= less > 1 ? 0.12 : 0.11;
        if (less < 0)
            less = 0;
        if (!isInLiquid() && isOnGround(0.01) && (isMoving2())) {
            collided = mc.thePlayer.isCollidedHorizontally;
            if (stage >= 0 || collided) {
                stage = 0;
                double a = LiquidBounce.moduleManager.getModule(Scaffold.class).getState() ? 0.407 : 0.41999742;
                double motY = a + getJumpEffect() * 0.1;
                if (stair == 0) {
                    mc.thePlayer.jump();
                    event.setY(mc.thePlayer.motionY = motY);
                    //  ChatUtil.printChat("PosY:"+motY);
                } else {

                }

                less++;
                if (less > 1 && !lessSlow)
                    lessSlow = true;
                else
                    lessSlow = false;
                if (less > 1.12)
                    less = 1.12;
            }
        }
        speed = getHypixelSpeed(stage) + 0.01+Math.random() /500;
        speed *= 0.855;
        if (stair > 0) {
            speed *= 0.7 - getSpeedEffect() * 0.11;
        }

        if (stage < 0)
            speed = defaultSpeed();
        if (lessSlow) {
            speed *= 0.95;
        }

        if (isInLiquid()) {
            speed = 0.12;
        }

        if ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
            setMotion(event, speed);
            ++stage;
        }
    }

    private double getHypixelSpeed(int stage) {
        double value = defaultSpeed() + (0.028 * getSpeedEffect()) + (double) getSpeedEffect() / 16;
        double firstvalue = 0.4145 + (double) getSpeedEffect() / 13;
        double decr = (((double) stage / 500) * 2);


        if (stage == 0) {
            //JUMP
            if (timer.delay(300)) {
                timer.reset();
                //mc.timer.timerSpeed = 1.354f;
            }
            if (!lastCheck.delay(500)) {
                if (!shouldslow)
                    shouldslow = true;
            } else {
                if (shouldslow)
                    shouldslow = false;
            }
            value = 0.64 + (getSpeedEffect() + (0.028 * getSpeedEffect())) * 0.134;

        } else if (stage == 1) {
            if (mc.timer.timerSpeed == 1) {
                //mc.timer.timerSpeed = 1.254f;
            }
            value = firstvalue;
        } else if (stage >= 2) {
            if (mc.timer.timerSpeed == 1) {
                //mc.timer.timerSpeed = 1f;
            }
            value = firstvalue - decr;
        }
        if (shouldslow || !lastCheck.delay(500) || collided) {
            value = 0.2;
            if (stage == 0)
                value = 0;
        }


        return Math.max(value, shouldslow ? value : defaultSpeed() + (0.028 * getSpeedEffect()));
    }

    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        else
            return 0;
    }

    public static double defaultSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            //  if(((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Hypixel")){
            // 	baseSpeed *= (1.0D + 0.225D * (amplifier + 1));
            // }else
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }

    public static int getJumpEffect() {
        if (mc.thePlayer.isPotionActive(Potion.jump))
            return mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        else
            return 0;
    }

    public static boolean isMoving2() {
        return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
    }

    public static boolean isOnGround(double height) {
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInLiquid() {
        if (mc.thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        final int y = (int) mc.thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    if (!(block instanceof BlockLiquid)) return false;
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    private void setMotion(MoveEvent em, double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            em.setX(0.0D);
            em.setZ(0.0D);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -42 : 42);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 42 : -42);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }
}
