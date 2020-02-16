package net.ccbluex.liquidbounce.features.module.modules.player;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
@ModuleInfo(name = "AntiFall", description = "Antifall.", category = ModuleCategory.PLAYER)
public class AntiFall extends Module {
    private final BoolValue voidcheck = new BoolValue("Void", true);
    private final FloatValue distance = new FloatValue("FallDistance", 5F, 1F, 20F);
    MSTimer timer = new MSTimer();
    private boolean saveMe;

    @EventTarget
    public void onMove(MoveEvent event) {
        if ((saveMe && timer.delay(150)) || mc.thePlayer.isCollidedVertically) {
            saveMe = false;
            timer.reset();
        }
        float dist = distance.get();
        if (mc.thePlayer.fallDistance > dist && !LiquidBounce.moduleManager.getModule(Fly.class).getState()) {
            if (!voidcheck.get() || !isBlockUnder()) {
                if (!saveMe) {
                    saveMe = true;
                    timer.reset();
                }
                mc.thePlayer.fallDistance = 0;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 12, mc.thePlayer.posZ, false));
            }
        }
    }
    private boolean isBlockUnder() {
        if(mc.thePlayer.posY < 0)
            return false;
        for(int off = 0; off < (int)mc.thePlayer.posY+2; off += 2){
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -off, 0);
            if(!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()){
                return true;
            }
        }
        return false;
    }
}
