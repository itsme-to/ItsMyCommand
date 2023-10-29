package ru.oftendev.itsmycommand.commands.args

import org.bukkit.entity.Player

interface CommandArgument<T> {
    fun parse(args: List<String>, sender: Player, isLast: Boolean = false): Pair<T?, List<String>>

    fun tabComplete(sender: Player, args: List<String?>): List<List<String>>
}