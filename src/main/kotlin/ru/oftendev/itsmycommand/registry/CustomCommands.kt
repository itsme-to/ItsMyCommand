package ru.oftendev.itsmycommand.registry

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import ru.oftendev.itsmycommand.commands.dynamic.DynamicCommand

object CustomCommands: ConfigCategory("commands", "commands") {
    val registry = Registry<DynamicCommand>()

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        val parent = getParentCommand(config.getString("command"))

        if (parent?.first != null) {
            parent.first.addSubcommand(
                DynamicCommand(
                    plugin,
                    config.apply { this.set("command", parent.second) },
                    1,
                    "null"
                )
            )
        } else {
            registry.register(
                DynamicCommand(
                    plugin,
                    config,
                    1,
                    id
                ).apply { this.register() }
            )
        }
    }

    override fun clear(plugin: LibreforgePlugin) {
        registry.forEach {
            it.unregister()
        }
    }

    private fun getParentCommand(command: String): Pair<PluginCommand, String>? {
        var currentCommand = command.split(" ").toMutableList()
        var parent: PluginCommand? = null

        while (true) {
            if (currentCommand.isEmpty()) return null

            if (parent == null) {
                parent = registry.firstOrNull { it.name.equals(currentCommand.first(), true) } ?: return null
                currentCommand = currentCommand.drop(1).toMutableList()
            } else {
                parent = ((parent.subcommands.firstOrNull { it.name.equals(currentCommand.first(), true) }
                        as? PluginCommand) ?: return Pair(parent, currentCommand.joinToString(" ")))
                currentCommand = currentCommand.drop(1).toMutableList()
            }
        }
    }
}