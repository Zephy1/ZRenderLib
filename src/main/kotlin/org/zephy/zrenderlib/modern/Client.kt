package org.zephy.zrenderlib.modern

//#if MC>12100
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient

object Client : ClientModInitializer {
    @JvmStatic
    fun getMinecraft(): MinecraftClient = MinecraftClient.getInstance()

    private val tasks = mutableListOf<Task>()

    class Task(var delay: Int, val callback: () -> Unit)

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

    /**
     * Schedule's a task to run on Minecraft's main thread in [delay] ticks.
     * Defaults to the next tick.
     * @param delay The delay in ticks
     * @param callback The task to run on the main thread
     */
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
}
//#endif