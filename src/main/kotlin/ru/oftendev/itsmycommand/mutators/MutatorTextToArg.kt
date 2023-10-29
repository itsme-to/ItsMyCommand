package ru.oftendev.itsmycommand.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location
import ru.oftendev.itsmycommand.commands.dynamic.executionData

object MutatorTextToArg: Mutator<NoCompileData>("text_to_argument") {
    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val eData = executionData[data.text ?: ""] ?: return data

        val id = config.getString("argument")

        val argument = eData.firstOrNull { it.id.equals(id, true) } ?: return data

        executionData[argument.value?.first as? String ?: ""] = eData

        return data.copy(text = argument.value?.first as? String)
    }
}