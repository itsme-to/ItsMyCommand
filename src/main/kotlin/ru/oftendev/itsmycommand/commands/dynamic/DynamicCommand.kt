package ru.oftendev.itsmycommand.commands.dynamic

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import ru.oftendev.itsmycommand.commands.args.*
import ru.oftendev.itsmycommand.itsMyCommand
import java.util.*

val executionData = mutableMapOf<String, List<ConfiguredArgument>>()

class DynamicCommand(plugin: EcoPlugin,
                     config: Config,
                     num: Int = 1,
                     private val id: String
) : PluginCommand(
    plugin,
    config.getString("command").split(" ")[num-1],
    config.getString("permission"),
    true
), Registrable {
    private val chain = Effects.compileChain(
        config.getSubsections("effects"),
        NormalExecutorFactory.NormalChainExecutor,
        ViolationContext(itsMyCommand)
    )

    private val conditions = Conditions.compile(
        config.getSubsections("conditions"),
        ViolationContext(itsMyCommand)
    )

    private val resulting = config.getString("command").split(" ").drop(num)

    private val isFinal = resulting.isEmpty()

    private val arguments = mutableMapOf<Int, ConfiguredArgument>()

    private val reversedArguments = mutableMapOf<ConfiguredArgument, List<Int>>()

    private val aliasList: MutableList<String> = config.getStrings("alias")

    init {
        if (!isFinal) {
            this.addSubcommand(DynamicCommand(
                plugin,
                config,
                num+1,
                "null"
            ))
        }

        val args = config.getSubsections("arguments")
            .map { ConfiguredArgument(it) }

        var curr = 0

        for (arg in args) {
            if (arg.size == 1) {
                arguments[curr] = arg
                reversedArguments[arg] = listOf(curr)
                curr+=1
            } else {
                val result = mutableListOf<Int>()
                for (i in 0..<arg.size) {
                    arguments[curr] = arg
                    result.add(curr)
                    curr +=1
                }
                reversedArguments[arg] = result
            }
        }
    }

    private fun checkIntegrity(args: MutableList<String>): ConfiguredArgument? {
        return if (args.size >= arguments.size) null else {
            arguments.values.drop(args.size).firstOrNull {
                it.isRequired
            }
        }
    }


    override fun onExecute(sender: Player, args: MutableList<String>) {
        if (!conditions.areMet(sender, EmptyProvidedHolder)) {
            return
        }

        if (!isFinal) {
            sender.sendPlainMessage(plugin.langYml.getMessage("invalid-command"))
            return
        }

        val integrity = checkIntegrity(args)

        if (integrity != null) {
            integrity.getRequireMessage(sender)?.let {
                sender.sendPlainMessage(it)
            }
            return
        }

        var currentArgs: List<String> = args

        reversedArguments.keys.forEachIndexed { index, configuredArgument ->
            currentArgs = configuredArgument.getValue(currentArgs, sender, index == reversedArguments.size - 1)
                .second

            if (configuredArgument.value?.first == null) {
                configuredArgument.getInvalidMessage(sender)?.let {
                    sender.sendPlainMessage(it)
                }
                return
            }
        }

        val random = UUID.randomUUID().toString()

        executionData[random] = reversedArguments.keys.toList()

        chain?.trigger(
            TriggerData(player = sender, text = random)
                .dispatch(sender)
        )
    }

    override fun tabComplete(sender: Player, args: MutableList<String>): MutableList<String> {
        var drop = 0
        val results = mutableMapOf<Int, MutableList<String>>()
        var i = 0
        reversedArguments.keys.forEach { v ->
            if (v.size == 1) {
                results[i] = v.tabComplete(args.drop(drop), sender).first().toMutableList()
                drop += 1
                i += 1
            } else {
                val complete = v.tabComplete(args.drop(drop), sender)
                for (j in complete) {
                    results[i] = j.toMutableList()
                    drop += 1
                    i += 1
                }
            }
        }

        return results.getOrDefault(args.size-1, mutableListOf())
    }

    override fun getID(): String {
        return id
    }

    override fun getAliases(): MutableList<String> {
        return aliasList
    }
}

class ConfiguredArgument(config: Config) {
    val id = config.getString("id")
    var value: Pair<*, List<String>>? = null
    private val argument: CommandArgument<*> = when(config.getString("args.type").lowercase()) {
        "string" -> StringArgument()
        "integer" -> IntegerArgument()
        "selector" -> SelectorArgument()
        "location" -> LocationArgument()
        else -> ListArgument(config.getStrings("args.list"))
    }
    val isRequired: Boolean = config.getBool("args.required")
    val size = if (this.argument is LocationArgument) 3 else 1
    private val requireMessage: String? = config.getStringOrNull("args.require-message")
    private val invalidMessage: String? = config.getStringOrNull("args.invalid-message")

    fun getRequireMessage(sender: Player): String? {
        return requireMessage?.formatEco(sender as? Player, true)
    }

    fun getInvalidMessage(sender: Player): String? {
        return invalidMessage?.formatEco(sender as? Player, true)
    }

    fun getValue(args: List<String>, sender: Player, isLast: Boolean):  Pair<*, List<String>> {
        value = argument.parse(args, sender, isLast)
        return value as Pair<Any?, List<String>>
    }

    fun tabComplete(args: List<String>, sender: Player): List<List<String>> {
        return argument.tabComplete(sender, args)
    }
}