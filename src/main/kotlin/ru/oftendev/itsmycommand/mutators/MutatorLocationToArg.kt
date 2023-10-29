package ru.oftendev.itsmycommand.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location
import ru.oftendev.itsmycommand.commands.dynamic.executionData

object MutatorLocationToArg: Mutator<NoCompileData>("location_to_argument") {
    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val eData = executionData[data.text ?: ""] ?: return data

        val id = config.getString("argument")

        val argument = eData.filter { it.id.equals(id, true) }
            .firstNotNullOfOrNull { it.value?.first as? Location } ?: return data

        return data.copy(location = argument)
    }
}