package ru.oftendev.itsmycommand.commands.args

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class SelectorArgument: CommandArgument<List<Entity>> {
    override fun parse(args: List<String>, sender: Player, isLast: Boolean): Pair<List<Entity>?, List<String>> {
        return Pair(Bukkit.selectEntities(sender, args.firstOrNull() ?: ""), args.drop(1))
    }

    override fun tabComplete(sender: Player, args: List<String?>): List<List<String>> {
        return listOf(emptyList())
    }
}