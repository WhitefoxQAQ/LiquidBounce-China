package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.Logger;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.network.play.client.C03PacketPlayer;
@ModuleInfo(name = "PacketMonitor", description = "Check C03Packet.", category = ModuleCategory.MISC)
public class PacketMotior extends Module {
    private int packetcounter;
    private String counter = "";
    private MSTimer timer = new MSTimer();

    @EventTarget
    public void onPacket(PacketEvent e){
        if (e.getPacket() instanceof C03PacketPlayer){
            packetcounter+=10;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent e){
        if (timer.delay(1000L)) {
            if (packetcounter > 22) {
                Logger.printinfo("\247c警告！Packet发送数量不正常!");
            }
            counter = String.valueOf(packetcounter);
            packetcounter = 0;
            timer.reset();
        }
    }
    @Override
    public String getTag() {
        return packetcounter+"";
    }
}
