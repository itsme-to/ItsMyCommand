package ru.oftendev.itsmycommand.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import ru.oftendev.itsmycommand.commands.dynamic.executionData

object MutatorPlayerAsArg: Mutator<NoCompileData>("player_as_argument") {
    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val eData = executionData[data.text ?: ""] ?: return data

        val id = config.getString("argument")

        val argument = eData.firstOrNull { it.id.equals(id, true) } ?: return data

        val player = (argument.value?.first as? List<Entity>)?.firstOrNull { it is Player } as? Player ?:
            (argument.value?.first as? String)?.let { Bukkit.getPlayer(it) } ?: return data

        return data.copy(player = player)
    }
}