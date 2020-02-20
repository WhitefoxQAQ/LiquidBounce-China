package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CNHypixelSpeed extends SpeedMode {
    public static int stage;
    public static MSTimer lastCheck = new MSTimer();
    private final MSTimer timer = new MSTimer();
    public boolean shouldslow = false;
    boolean collided = false, lessSlow;
    double less, stair;
    private double speed;

    public CNHypixelSpeed() {
        super("CNHypixel");
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
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
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

    @Override
    public void onEnable() {
        boolean player = mc.thePlayer == null;
        collided = !player && mc.thePlayer.isCollidedHorizontally;
        lessSlow = false;
        less = 0;
        stage = 2;
        mc.timer.timerSpeed = 1;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
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
                }

                less++;
                lessSlow = less > 1 && !lessSlow;
                if (less > 1.12)
                    less = 1.12;
            }
        }
        speed = getHypixelSpeed(stage) + 0.01 + Math.random() / 500;
        speed *= 0.87;
        if (stair > 0) {
            speed *= 0.7 - getSpeedEffect() * 0.1;
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
        double value = defaultSpeed() + 0.028 * (double) getSpeedEffect() + (double) getSpeedEffect() / 15.0;
        double firstvalue = 0.4145 + (double) getSpeedEffect() / 12.5;
        double decr = (double) stage / 500.0 * 2.0;
        if (stage == 0) {
            if (this.timer.delay(300.0f)) {
                this.timer.reset();
            }
            if (!lastCheck.delay(500.0f)) {
                if (!this.shouldslow) {
                    this.shouldslow = true;
                }
            } else if (this.shouldslow) {
                this.shouldslow = false;
            }
            value = 0.64 + ((double) getSpeedEffect() + 0.028 * (double) getSpeedEffect()) * 0.134;
        } else if (stage == 1) {
            // empty if block
            value = firstvalue;
        } else if (stage >= 2) {
            // empty if block
            value = firstvalue - decr;
        }
        if (this.shouldslow || !lastCheck.delay(500.0f) || this.collided) {
            value = 0.2;
            if (stage == 0) {
                value = 0.0;
            }
        }
        return Math.max(value, this.shouldslow ? value : defaultSpeed() + 0.028 * (double) getSpeedEffect());
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
                    yaw += (forward > 0.0D ? -41 : 41);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 41 : -41);
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
