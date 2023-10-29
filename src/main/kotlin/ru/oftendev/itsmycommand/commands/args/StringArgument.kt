package ru.oftendev.itsmycommand.commands.args

import org.bukkit.entity.Player

class StringArgument: CommandArgument<String> {
    override fun parse(args: List<String>, sender: Player, isLast: Boolean): Pair<String?, List<String>> {
        return if (isLast) {
            Pair(args.joinToString(" "), mutableListOf())
        } else {
            Pair(args.firstOrNull(), args.drop(1))
        }
    }

    override fun tabComplete(sender: Player, args: List<String?>): List<List<String>> {
        return listOf(emptyList())
    }
}