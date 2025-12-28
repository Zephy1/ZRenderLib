package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
//#if MC<12100
//$$import net.minecraft.client.Minecraft
//$$import net.minecraftforge.common.MinecraftForge
//$$import net.minecraftforge.fml.common.Mod
//$$import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
//$$import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
//$$import net.minecraftforge.fml.common.gameevent.TickEvent
//$$@Mod(
//$$    modid = "zrenderlib",
//$$    name = "ZRenderLib",
//$$    version = "1.0.0",
//$$    clientSideOnly = true,
//$$    modLanguage = "Kotlin",
//$$    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
//$$)
//$$object Client {
//$$    @Mod.EventHandler
//$$    fun preInit(event: FMLPreInitializationEvent) {
//$$        listOf(this).forEach(MinecraftForge.EVENT_BUS::register)
//$$    }
//$$    @SubscribeEvent
//$$    fun onTick(event: TickEvent.ClientTickEvent) {
//$$        if (event.phase == TickEvent.Phase.END) return
//$$        synchronized(tasks) {
//$$            tasks.removeAll {
//$$                if (it.delay-- <= 0) {
//$$                    getMinecraft().addScheduledTask { it.callback() }
//$$                    true
//$$                } else {
//$$                    false
//$$                }
//$$            }
//$$        }
//$$    }
//#else
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

object Client : ClientModInitializer {
    override fun onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register {
            synchronized(tasks) {
                tasks.removeAll {
                    if (it.delay-- <= 0) {
                        getMinecraft().submit(it.callback)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }
//#endif

    private val tasks = mutableListOf<Task>()

    class Task(var delay: Int, val callback: () -> Unit)

    @JvmStatic
    //#if MC<12100
    //$$fun getMinecraft(): Minecraft = Minecraft.getMinecraft()
    //#else
    fun getMinecraft(): Minecraft = Minecraft.getInstance()
    //#endif

    @JvmStatic
    @JvmOverloads
    fun scheduleTask(delay: Int = 0, callback: () -> Unit) {
        addTask(delay, callback)
    }

    fun addTask(delay: Int, callback: () -> Unit) {
        synchronized(tasks) {
            tasks.add(Task(delay, callback))
        }
    }

    //#if MC>=12110
    fun <T> synchronizedTask(task: () -> T): T {
        val mc = getMinecraft()
        if (mc.isSameThread) {
            return task()
        }

        val latch = java.util.concurrent.CountDownLatch(1)
        var result: T? = null

        mc.execute {
            try {
                result = task()
            } finally {
                latch.countDown()
            }
        }

        latch.await()
        return result!!
    }
    //#endif
}
//#endif
