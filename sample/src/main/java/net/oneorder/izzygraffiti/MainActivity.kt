package net.oneorder.izzygraffiti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.undabot.izzy.parser.GsonParser
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.parser.IzzyConfiguration
import com.undabot.izzy.parser.SidePosting
import net.oneorder.izzygraffiti.models.LineItemUnit
import net.oneorder.izzygraffiti.models.ShipmentStatus
import net.oneorder.izzygraffiti.models.UnitEvents
import net.oneorder.izzygraffiti.models.UpdateShipmentForm

class MainActivity : AppCompatActivity() {
    private lateinit var izzy: Izzy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initIzzy()

        findViewById<View>(R.id.textView).setOnClickListener { onTextClicked() }
    }

    private fun initIzzy(){
        val izzyConfiguration = IzzyConfiguration(
            arrayOf(
                UpdateShipmentForm::class.java,
            )
        )
        izzy = Izzy(GsonParser(izzyConfiguration, Gson())) // or GsonParser when Gson is used
    }

    private fun onTextClicked() {
        val shipment = createShipment()
        val shipmentJson = izzy.serializeItem(shipment)
        Log.d(MainActivity::class.java.simpleName, shipmentJson)
    }

    private fun createShipment(): UpdateShipmentForm {
        val lineItemUnits = listOf(
            LineItemUnit(id = null, weight = 1.0, status_event = UnitEvents.EVENT_UNPICK, method = SidePosting.METHOD_CREATE),
            LineItemUnit(id = "2", weight = 1.0, status_event = null, method = SidePosting.METHOD_UPDATE),
            LineItemUnit(id = "3", weight = 1.0, status_event = null, method = SidePosting.METHOD_UPDATE),
            LineItemUnit(id = "4", weight = 1.0, status_event = null, method = SidePosting.METHOD_DISASSOCIATE),
        )

        return UpdateShipmentForm(
            id = "1",
            status_event = ShipmentStatus.EVENT_PREPARING,
            status_event_user_id = "2",
            status_event_done_at = "02-02-2020T02:02:02",
            pallet_size = 1.5,
            pallet_numbers = "1,2",
            multipleUnits = lineItemUnits,
            favUnit = LineItemUnit(id = null, weight = 1.0, status_event = UnitEvents.EVENT_UNPICK, method = SidePosting.METHOD_CREATE)
        )
    }
}