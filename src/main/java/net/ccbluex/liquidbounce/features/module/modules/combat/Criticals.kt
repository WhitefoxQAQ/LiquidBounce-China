/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.Logger
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import org.apache.commons.lang3.RandomUtils

@ModuleInfo(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT)
class Criticals : Module() {
    var timer = MSTimer()
    var lastStep = MSTimer()
    var groundTicks = 0
    val modeValue = ListValue("Mode", arrayOf("Packet", "HypixelPacket", "NoGround", "Hop", "TPHop", "Jump", "LowJump"), "packet")
    val delayValue = IntegerValue("Delay", 0, 0, 1000)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 20)

    val msTimer = MSTimer()
    var nogroundstate = false
    override fun onEnable() {
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            val entity = event.targetEntity
            if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb || mc.thePlayer.isInWater ||
                    mc.thePlayer.isInLava || mc.thePlayer.ridingEntity != null || entity.hurtResistantTime >= hurtTimeValue.get() ||
                    LiquidBounce.moduleManager[Fly::class.java]!!.state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return

            val x = mc.thePlayer.posX
            val y = mc.thePlayer.posY
            val z = mc.thePlayer.posZ

            when (modeValue.get().toLowerCase()) {
                "packet" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.0625, z, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 1.1E-5, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    mc.thePlayer.onCriticalHit(entity)
                }

                "hypixelpacket" -> {
                    if( entity.hurtResistantTime <= hurtTimeValue.get() && lastStep.delay(20f)  && (timer.delay(200f) || entity.hurtResistantTime > 0) && mc.thePlayer.isCollidedVertically) {
                        if(groundTicks > 1) {
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + RandomUtils.nextFloat(0.01f,0.06f), z, false))
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                            mc.thePlayer.onCriticalHit(entity)
                        }
                    }
                }

                "hop" -> {
                    mc.thePlayer.motionY = 0.1
                    mc.thePlayer.fallDistance = 0.1f
                    mc.thePlayer.onGround = false
                }

                "tphop" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.02, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.01, z, false))
                    mc.thePlayer.setPosition(x, y + 0.01, z)
                }
                "jump" -> mc.thePlayer.motionY = 0.42
                "lowjump" -> mc.thePlayer.motionY = 0.3425
            }

            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if(packet is C02PacketUseEntity && canCrit() && LiquidBounce.moduleManager[Speed::class.java]!!.state ){
            nogroundstate = true
        }
        if (packet is C03PacketPlayer && modeValue.get().equals("NoGround", ignoreCase = true) && nogroundstate) {
            packet.onGround = false
            nogroundstate = false;
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if(isOnGround(0.001)){
            groundTicks++;
        }else if(!mc.thePlayer.onGround){
            groundTicks = 0
        }

    }
    fun isOnGround(height: Double): Boolean {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(0.0, -height, 0.0)).isEmpty()
    }
    private fun canCrit(): Boolean {
        return mc.thePlayer.onGround && !mc.thePlayer.isInWater
    }
    override val tag: String?
        get() = modeValue.get()
}
