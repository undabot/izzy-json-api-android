package net.oneorder.izzygraffiti.models

import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

//TODO:: update all modeling system
@Type("shipments")
class UpdateShipmentForm(
    id: String,
    val status_event: String,
    val status_event_user_id: String,
    val status_event_done_at: String,
    val pallet_size: Double?,
    val pallet_numbers: String?,
    @Relationship("line_item_units")
    val multipleUnits: List<LineItemUnit>?,
    @Relationship("line_item_unit")
    val favUnit: LineItemUnit?,
): IzzyResource(id = id)

@Type("line_item_units")
class LineItemUnit(
    id: String? = null,
    val weight: Double? = null,
    val status_event: String? = null,
    method: String? = null
): IzzyResource(id = id, method = method)

typealias LineItemUnits = List<LineItemUnit>

object ShipmentStatus{
    const val EVENT_PREPARING = "prepare"
    const val EVENT_READY = "ready"
    const val EVENT_LOADED = "delivery_load"
}

object UnitEvents {
    const val EVENT_UNPICK = "unpick"
}