package seeker.doesoh.tracker.presentation.Chart

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.model.ChartDataset
import seeker.doesoh.tracker.data.model.Fuel
import seeker.doesoh.tracker.data.model.Temperature

@SuppressLint("ViewConstructor")
class CustomMarker(private val chartData: List<ChartDataset>, context: Context, layoutResource: Int): MarkerView(context,layoutResource) {
        var chartMarkerView: TextView = findViewById(R.id.tv_markerView)

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
                val index = e?.x?.toInt()?.div(10) ?: 0
                val currentData = chartData[index]
                val address = currentData.address ?: ""
                val date = currentData.date

                val data = when(currentData) {
                        is Temperature -> "${currentData.temperature}  °C"
                        is Fuel -> "${currentData.fuel} liters"
                        else -> ""
                }

                val formattedText = StringBuilder()
                        .appendLine(address)
                        .appendLine(date)
                        .appendLine(data)
                chartMarkerView.text = formattedText
                super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF {
                return MPPointF(-width.toFloat(), -height.toFloat());
        }
}