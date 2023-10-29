package ru.oftendev.itsmycommand.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender
import ru.oftendev.itsmycommand.pluginId

class MainCommand(plugin: EcoPlugin): PluginCommand(
    plugin,
    pluginId,
    "$pluginId.use",
    false
) {
    init {
        this.addSubcommand(ReloadCommand(plugin))
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendPlainMessage(plugin.langYml.getMessage("invalid-command"))
    }

    override fun getAliases(): MutableList<String> {
        return mutableListOf(
            "itsmycmd"
        )
    }
}