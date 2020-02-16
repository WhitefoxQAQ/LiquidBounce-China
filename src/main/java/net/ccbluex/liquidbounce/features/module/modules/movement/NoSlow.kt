/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import akka.actor.Kill
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.item.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper


@ModuleInfo(name = "NoSlow", description = "Cancels slowness effects caused by soulsand and using items.",
        category = ModuleCategory.MOVEMENT)
class NoSlow : Module() {
    var killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura?
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val packet = BoolValue("Packet", true)
    private val packet2 = BoolValue("Packet2", true)
    // Soulsand
    val soulsandValue = BoolValue("Soulsand", true)

    @EventTarget
    fun onAttack(event: AttackEvent) {

    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val heldItem = mc.thePlayer.heldItem
        if (heldItem == null || heldItem.item !is ItemSword || !MovementUtils.isMoving()) {
            return
        }
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        if (!mc.thePlayer.isBlocking && !killAura.blockingStatus) {
            return
        }
        if (this.packet.get()) {
            when (event.eventState) {
                EventState.PRE -> {
                    val digging = C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN)
                    mc.netHandler.addToSendQueue(digging)
                }
                EventState.POST -> {
                    val blockPlace = C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
                    mc.netHandler.addToSendQueue(blockPlace)
                }
            }
        }
        if (this.packet2.get() && killAura.target == null) {
            when (event.eventState) {
                EventState.PRE -> {
                    val digging = C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN)
                    mc.netHandler.addToSendQueue(digging)
                }
                EventState.POST -> {
                    mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(getHypixelBlockpos(), 255,
                            mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                }
            }
        }
    }

    fun getHypixelBlockpos(): BlockPos? {
        val random = java.util.Random()
        val dx = MathHelper.floor_double(random.nextDouble() / 1000 + 2820)
        val jy = MathHelper.floor_double(random.nextDouble() / 100 * 0.20000000298023224)
        val kz = MathHelper.floor_double(random.nextDouble() / 1000 + 2820)
        return BlockPos(dx, -jy % 255, kz)
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> {
            if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
        }
        is ItemSword -> {
            if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
        }
        is ItemBow -> {
            if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
        }
        else -> 0.2F
    }

    override val tag: String?
        get() = if (packet.get() || packet2.get()) "Packet" else "Vanilla"

}
