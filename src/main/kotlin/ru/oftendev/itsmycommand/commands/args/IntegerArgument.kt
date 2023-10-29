package ru.oftendev.itsmycommand.commands.args

import com.willfp.eco.util.toSingletonList
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class IntegerArgument: CommandArgument<Int> {
    override fun parse(args: List<String>, sender: Player, isLast: Boolean): Pair<Int?, List<String>> {
        return Pair(args.firstOrNull()?.toIntOrNull(), args.drop(1))
    }

    override fun tabComplete(sender: Player, args: List<String?>): List<List<String>> {
        val first = args.firstOrNull() ?: return listOf(emptyList())
        return StringUtil.copyPartialMatches(
            first,
            listOf(1,10,100,1000,10000,100000).map { it.toString() },
            mutableListOf()
        ).toSingletonList()
    }
}