package ru.oftendev.itsmycommand

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.mutators.Mutators
import ru.oftendev.itsmycommand.commands.MainCommand
import ru.oftendev.itsmycommand.mutators.MutatorLocationToArg
import ru.oftendev.itsmycommand.mutators.MutatorPlayerAsArg
import ru.oftendev.itsmycommand.mutators.MutatorTextToArg
import ru.oftendev.itsmycommand.mutators.MutatorValueToArg
import ru.oftendev.itsmycommand.registry.CustomCommands

lateinit var itsMyCommand: ItsMyCommand
    private set

const val pluginId = "itsmycommand"

class ItsMyCommand: LibreforgePlugin() {
    init {
        itsMyCommand = this
    }

    override fun handleEnable() {
        Mutators.register(MutatorLocationToArg)
        Mutators.register(MutatorPlayerAsArg)
        Mutators.register(MutatorTextToArg)
        Mutators.register(MutatorValueToArg)

    }

    override fun loadPluginCommands(): MutableList<PluginCommand> {
        return mutableListOf(
            MainCommand(this)
        )
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            CustomCommands
        )
    }
}