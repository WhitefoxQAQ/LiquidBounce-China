package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "TargetStrafe", description = "fuck some niggar.", category = ModuleCategory.MOVEMENT)
public class TargetStrafe extends Module {
    private final FloatValue range = new FloatValue("Range", 0F, 0F, 8F);
    boolean downX;
    boolean downZ;
    private double lastX;
    private double lastZ;

    @Override
    public void onEnable() {
        super.onEnable();
        lastX = 0;
        lastZ = 0;
        downX = false;
        downZ = false;
    }

    @EventTarget
    public void onMove(MoveEvent e) {
        if(getClosest() != null) {
            if (lastX <= 0) {
                downX = false;
            }
            if (lastZ <= 0) {
                downZ = false;
            }
            if (!downX) {
                this.lastX += 0.15;
            }
            if (!downZ) {
                this.lastZ += 0.15;
            }
            if (this.lastX >= 0.5) {
                downX = true;
            }
            if (this.lastZ >= 0.5) {
                downZ = true;
            }
            if (downX) {
                this.lastX -= 0.15;
            }
            if (downZ) {
                this.lastZ -= 0.15;
            }
            e.setX(e.getX() + lastX);
            e.setZ(e.getZ() + lastZ);
        } else if (getClosest() == null) {
            lastX = 0;
            lastZ = 0;
            downX = false;
            downZ = false;
        }
    }

    private EntityPlayer getClosest() {
        double dist = range.get();
        EntityPlayer target = null;
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            double currentDist = mc.thePlayer.getDistanceToEntity(player);
            if (currentDist <= dist) {
                dist = currentDist;
                target = player;
            }
        }
        return target;
    }
}
