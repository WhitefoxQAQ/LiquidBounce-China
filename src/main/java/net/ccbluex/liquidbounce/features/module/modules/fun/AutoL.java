package net.ccbluex.liquidbounce.features.module.modules.fun;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.Random;


@ModuleInfo(name = "AutoL", description = "Auto Type L To Killed Player.",
        category = ModuleCategory.FUN)
public class AutoL extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Normal"}, "Normal");
    private final BoolValue advalue = new BoolValue("AD", true);
    String[] NormaL = {"我告诉你这样的情况你还需要明白了解的不是吗", "我这埋汰你呢都是没速度和我继续的比拟呢不是奥妙",
            "我好象你爸爸似的你难道自己不清楚这样情况埃", "你这样的蜗牛就是完全没什么速度反抗你爸爸我埃",
            "我可认为你这样的垃圾好象完全没什么力量似的", "然后你完全没有力量你明白你的扣字垃圾吗。呵呵。废物"
            , "我好象你爸爸似的随便殴打你这样的事实情况了埃",
            "你为什么在这里耀武扬威"
            , "现在了解什么情况你完全垃圾似的知道了没埃"
            , "你好象垃圾似的你怎么和我对抗呢，呵呵你出来告诉我吗 然后你完全没有速度你明白什么情况吗，垃圾似的出来埃",
            "然后我随便殴打你这样的垃圾似的事实的不是吗", "你自己好象我儿子似的只能污蔑我还是怎么埃",
            "我这理解你是垃圾速度和我继续的比拟呢对吗", "我就是你父亲似的随便殴打你完全没什么力量似的",
            "父亲我随便殴打你这样的事实情况了啊弟弟", "难道不是那么出来我现在不随便殴打你吗青年", "你自己现在不了解什么还是怎么殴打飞机的爸爸",
            "我怎么感觉你和我没脾气的儿子是的反抗爸爸的", "儿子你现在自己怎么反抗的爸爸的难道什么情况啊", "小伙子啊爸爸随意的打的你了啊你明白什么情况",
            "你就是完全没什么力量反抗我还是怎么啊弟弟", "我这给你打击的哭泣的话你都是垃圾速度和我继续呢"
            , "麻烦你现在了解什么情况你出来告诉大家知道了吗", "你好象垃圾似的你有什么脾气告诉我知道了没埃",
            "你不知道爸爸我的速度可以完全吧你抹杀了啊你怎么和我相提并论了阿", "为什么在这里唧唧喳喳"
            , "儿子你现在自己怎么反抗的爸爸的难道什么情况啊",
            "我怎么国家你和我没脾气的儿子是的什么殴打"
            , "现在的你看见你爹的各种速度害怕了还是怎么的 孩子你现在的可有意思了吗你明白什么情况了吗 ",
            "你干什么啊在你登峰造极的爸爸我面前班门弄斧是不是啊?", "你是不是无可奈何了啊?小伙子是不是看见你爹爹我的言语畏惧了啊?",
            "你不知道爸爸我的速度可以完全吧你抹杀了啊你怎么和我相提并论了阿", "你现在是不是坐在电 脑前手心 出汗呢，你是不是紧张了呢",
            "你是不是八仙过海的来狗你登峰造极的爹爹我啊,用你华丽的言语攻击我啊, 小伙子.你怎么是个瓮中之鳖啊.我草你麻痹的无名小卒还大言不惭的吹嘘啊",
            "你爸爸我的速度揍你足够了", "我现在觉得你完全没有力气你自己清楚吗，呵呵垃圾似的", "然后你开始唠嗑你可以反抗还是杂的了，废物似的明白吗",
            "你好象垃圾似的你有什么脾气告诉我知道了没埃",
            "那么我们现在随便的唠嗑下你自己准备完全了吗",
            "你自己好象残酷恶霸事实你告诉我啊少年"
            , "那么我们现在就立刻唠嗑一下请你不要逃避好吗",
            "你跟本没有速度你我面前什么不能开始了啊是吗?",
            "你现在什么自己没有速度你现在和我唠嗑一下能吗?"
            , "你能够现在带着词汇立刻滚蛋好吗? 你的反抗好没有力气啊?",
            "然后你觉得你可以唠嗑还是杂的了你出来告诉我啊，儿子似的",
            "我的残酷速度随便殴打你这样的事实情况了埃", "还真是个井底之蛙，我都不想再打击你了",
            "你认为你就这么跟我说几句话旧能跟你大哥我抗衡了吗", "你这样没什么文化水平似的垃圾怎么和我开始",
            "我和你继续的开始唠嗑怎么的告诉大家情况呢蜗牛埃", "为什么在这里叽叽喳喳。你认为你那恶心的词汇可以伤到你爸爸我"
            , "你自己不清楚你完全垃圾似的没有水平还是怎么的啊. 我好象你爸爸似的不了解情况还是怎么的开始唠嗑啊. 我告诉你这个垃圾似的只能浪费时间逃避我了"
            , "儿子我好象你爸爸这样的情况你自己告诉我似的", "我这不是侮辱你恩吗也没速度和忘记下的呢", "但是你怎么反抗你爸爸我埃出来告诉我和大家"
            , "我就是随便和你开始但是你怎么反抗你爸爸我", "你认为就你这点词汇能把我打倒在这小小的网络世界中吗"
    };

    @EventTarget
    public void onPacket(final PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            Random r = new Random();
            String text = NormaL[r.nextInt(57)];
            String ad = "";
            String ad2 = "";
            String message = ((S02PacketChat) packet).getChatComponent().getUnformattedText();
            if (message.contains("被" + mc.thePlayer.getGameProfile().getName() + "击杀。")) {
                String username = message.replaceAll("被" + mc.thePlayer.getGameProfile().getName() + "击杀。", "");
                mc.thePlayer.sendChatMessage("/ac [LiquidBounce] " + username + " L " + text + (advalue.get() ? ad + ad2 : ""));
                mc.thePlayer.sendChatMessage("/wdr " + username + " ka speed reach fly velocity ac");
            }
            if (message.contains(" 被击杀，击杀者： " + mc.thePlayer.getGameProfile().getName() + "。")) {
                String username = message.replaceAll(" 被击杀，击杀者： " + mc.thePlayer.getGameProfile().getName() + "。", "");
                mc.thePlayer.sendChatMessage("/ac [LiquidBounce] " + username + " L " + text + (advalue.get() ? ad + ad2 : ""));
                mc.thePlayer.sendChatMessage("/wdr " + username + " ka speed reach fly velocity ac");
            }
        }
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }

}
