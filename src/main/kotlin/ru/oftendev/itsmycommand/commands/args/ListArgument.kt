package ru.oftendev.itsmycommand.commands.args

import com.willfp.eco.util.toSingletonList
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class ListArgument(private val accepted: List<String>): CommandArgument<String> {
    override fun parse(args: List<String>, sender: Player, isLast: Boolean): Pair<String?, List<String>> {
        return Pair(if (args.firstOrNull() in accepted) args.firstOrNull() else null, args.drop(1))
    }

    override fun tabComplete(sender: Player, args: List<String?>): List<List<String>> {
        val first = args.firstOrNull() ?: return listOf(emptyList())
        return StringUtil.copyPartialMatches(
            first,
            accepted,
            mutableListOf()
        ).toSingletonList()
    }
}