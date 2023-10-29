package ru.oftendev.itsmycommand.commands.args

import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toSingletonList
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class LocationArgument: CommandArgument<Location> {
    override fun parse(args: List<String>, sender: Player, isLast: Boolean): Pair<Location?, List<String>> {
        return if (args.size < 3) {
            Pair(null, args)
        } else {
            val x = parseX(args[0], sender) ?: return Pair(null, args)
            val y = parseY(args[1], sender) ?: return Pair(null, args)
            val z = parseZ(args[2], sender) ?: return Pair(null, args)

            val result = Pair(Location(sender.world, x, y, z), args.drop(3))

            return result
        }
    }

    override fun tabComplete(sender: Player, args: List<String?>): List<List<String>> {
        val result = mutableListOf<List<String>>()

        for (i in 0..2) {
            val current = args.getOrNull(i)
            if (current != null) {
                val location = when(i) {
                    0 -> sender.location.x.toNiceString().replace(",", ".")
                    1 -> sender.location.y.toNiceString().replace(",", ".")
                    else -> sender.location.z.toNiceString().replace(",", ".")
                }

                result.add(
                    StringUtil.copyPartialMatches(
                        current,
                        location.toSingletonList(),
                        mutableListOf()
                    )
                )
            } else {
                result.add(mutableListOf())
            }
        }

        return if (result.isEmpty()) listOf(emptyList()) else result
    }

    private fun parseX(string: String, sender: CommandSender): Double? {
        val add = if (string.startsWith("~") && sender is Player) sender.location.x
            else if (sender is Player) sender.location.x else 0.0
        return string.removePrefix("~").toDoubleOrNull()?.let { it + add } ?: return null
    }

    private fun parseY(string: String, sender: CommandSender): Double? {
        val add = if (string.startsWith("~") && sender is Player) sender.location.y
            else if (sender is Player) sender.location.y else 0.0
        return string.removePrefix("~").toDoubleOrNull()?.let { it + add } ?: return null
    }

    private fun parseZ(string: String, sender: CommandSender): Double? {
        val add = if (string.startsWith("~") && sender is Player) sender.location.z
            else if (sender is Player) sender.location.z else 0.0
        return string.removePrefix("~").toDoubleOrNull()?.let { it + add } ?: return null
    }
}