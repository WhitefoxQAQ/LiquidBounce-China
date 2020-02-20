package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.FontManager;
import net.ccbluex.liquidbounce.ui.font.UnicodeFontRenderer;
import net.ccbluex.liquidbounce.utils.Logger;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.network.play.server.S02PacketChat;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@ModuleInfo(name = "ModCheck", description = "Check staff online.", category = ModuleCategory.MISC)
public class ModCheck extends Module {
    private String[] modlist = new String[]{"mxu",
            "魂魄妖梦",
            "heav3ns",
            "chrisan",
            "小阿狸",
            "chen_xixi",
            "StartOver_",
            "tanker_01",
            "bingmo",
            "crazyforlove",
            "时光易老不忘初心",
            "chen_duxiu",
            "造化钟神秀",
            "SnowDay",
            "minikloon",
            "Owenkill",
            "idk",
            "EnderMan001",
            "AzarelH",
            "Jumper",
            "Tanker",
            "绅士龙",
            "Lyra2REv2",
            "IntelXeonE"};
    private String modname;
    private MSTimer timer = new MSTimer();
    private List offlinemod = new ArrayList();
    private List onlinemod = new ArrayList();
    private final BoolValue showOnline = new BoolValue("ShowOnline", true);
    private final BoolValue showOffline = new BoolValue("ShowOffline", true);
    private final BoolValue blockmessage = new BoolValue("BlockWatchdogMessage", true);
    private int counter;
    private boolean isFinished;
    @EventTarget
    public void onRender(Render2DEvent e) {
        UnicodeFontRenderer font = FontManager.yahei18;
        List<String> listArray = Arrays.asList(this.modlist);
        int Mod = 0;
        int Long = 0;
        if (showOnline.get())
            for (String mods : listArray) {
                if (onlinemod.contains(mods)) {
                    Long += 10;
                }
            }
        if (showOffline.get() && !onlinemod.isEmpty())
            for (String mods : listArray) {
                if (offlinemod.contains(mods)) {
                    Long += 10;
                }
            }
        if (onlinemod.isEmpty()) {
            Long += 10;
        }
        RenderUtils.rectangle(5, 100, 120, 111, new Color(0, 125, 255, 255).getRGB());
        RenderUtils.rectangle(5, 110, 120, 110 + Long, new Color(255, 255, 255, 155).getRGB());
        Long = 0;
        if (showOnline.get())
            for (String mods : listArray) {
                // 如果客服不重复在线
                if (onlinemod.contains(mods)) {
                    // 客服在线 Rect自动加长
                    FontManager.yahei18.drawStringWithShadow(mods, 15, 110 + Long, Color.GREEN.getRGB());
                    Long += 10;
                    Mod++;// Mod数值增加
                }
            }
        if (showOffline.get() && !onlinemod.isEmpty())
            for (String mods : listArray) {
                // 如果客服不重复在线
                if (offlinemod.contains(mods)) {
                    // 客服在线 Rect自动加长
                    FontManager.yahei18.drawStringWithShadow(mods, 15, 110 + Long, Color.RED.getRGB());
                    Long += 10;
                    Mod++;// Mod数值增加
                }
            }
        // RenderUtil.drawRoundedRect(5, 150, 90, 150 + Long, new Color(255, 255, 255,
        // 0).getRGB(),
        // new Color(255, 255, 255, 155).getRGB());
        if (Mod < 1) {
            FontManager.yahei18.drawCenteredStringWithShadow("当前没有客服在线", 57.5f, 110, new Color(75, 75, 75).getRGB());
        }
        FontManager.yahei18.drawCenteredStringWithShadow("客服在线列表 (" + Mod + "/"+listArray.size()+")", 57.5f, 100,
                new Color(255, 255, 255).getRGB());

    }
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)event.getPacket();
            String e = s02PacketChat.getChatComponent().getUnformattedText();
            if(e.toLowerCase().contains("--------------------")){
                event.cancelEvent();
            }
            if(e.toLowerCase().contains("[WATCHDOG]") && blockmessage.get()){
                event.cancelEvent();
            }
            if (e.toLowerCase().contains("玩家离线，你不能邀请") || e.toLowerCase().contains("That player is not online!")) {
                event.cancelEvent();
                if (this.onlinemod.contains(this.modname)) {
                    Logger.printinfo(this.modname + "§a已下线!");
                    this.onlinemod.remove(this.modname);
                    this.offlinemod.add(this.modname);
                    return;
                }

                if (!this.offlinemod.contains(this.modname)) {
                    Logger.printinfo(this.modname + "§a不在线!");
                    this.offlinemod.add(this.modname);
                }
            }

            if (e.toLowerCase().contains("你不能邀请这位玩家入队")) {
                mc.thePlayer.sendChatMessage("/chat a");
                event.cancelEvent();
                if (this.offlinemod.contains(this.modname)) {
                    Logger.printinfo(this.modname + "§a已上线!");
                    this.offlinemod.remove(this.modname);
                    this.onlinemod.add(this.modname);
                    return;
                }

                if (!this.onlinemod.contains(this.modname)) {
                    Logger.printinfo(this.modname + "§a在线!");
                    this.onlinemod.add(this.modname);
                }
            }

            if (e.toLowerCase().contains("找不到名为 \"" + this.modname + "\" 的玩家")) {
                event.cancelEvent();
            }
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(this.timer.hasTimePassed(this.isFinished?10000L:2000L)) {
            if(this.counter >= this.modlist.length) {
                this.counter = -1;
                if(!this.isFinished) {
                    this.isFinished = true;
                }
            }

            ++this.counter;
            this.modname = this.modlist[this.counter];
            mc.thePlayer.sendChatMessage("/p " + this.modname);
            this.timer.reset();
        }


    }
}
