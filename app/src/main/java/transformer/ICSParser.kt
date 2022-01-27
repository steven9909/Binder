package transformer

import data.CalendarEvent
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object ICSParser {

    private const val BEGIN_EVENT_HEADER = "BEGIN:VEVENT"
    private const val END_EVENT_HEADER = "END:VEVENT"
    private const val DATE_START_HEADER = "DTSTART"
    private const val DATE_END_HEADER = "DTEND"
    private const val SUMMARY_HEADER = "SUMMARY"

    private val dateFormat1 = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    private val dateFormat2 = SimpleDateFormat("yyyyMMdd")

    @SuppressWarnings("ComplexMethod")
    @Throws(ICSParserException::class)
    fun parse(inputStream: InputStream): List<CalendarEvent> {
        val list = mutableListOf<CalendarEvent>()

        var isParsingEvent = false

        var startTime: Long? = null
        var endTime: Long? = null
        var name: String? = null

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                if (it.startsWith(BEGIN_EVENT_HEADER)) {
                    if (isParsingEvent) {
                        throw ICSParserException
                    }
                    isParsingEvent = true
                } else if(it.startsWith(END_EVENT_HEADER)) {
                    if (isParsingEvent) {
                        isParsingEvent = false
                        name?.let { name ->
                            startTime?.let { startTime ->
                                endTime?.let { endTime ->
                                    list.add(CalendarEvent(name = name, startTime = startTime, endTime = endTime))
                                }
                            }
                        }
                    } else {
                        throw ICSParserException
                    }
                }
                if (isParsingEvent && it.startsWith(DATE_START_HEADER)) {
                    val lastColon = it.lastIndexOf(":")
                    if (lastColon != -1) {
                        val dateValue = it.substring(lastColon+1)
                        val date = try {
                            dateFormat1.parse(dateValue)
                        } catch(e: ParseException) {
                            dateFormat2.parse(dateValue)
                        }
                        startTime = date.time
                    }
                } else if(isParsingEvent && it.startsWith(DATE_END_HEADER)) {
                    val lastColon = it.lastIndexOf(":")
                    if (lastColon != -1) {
                        val dateValue = it.substring(lastColon + 1)
                        val date = try {
                            dateFormat1.parse(dateValue)
                        } catch (e: ParseException) {
                            dateFormat2.parse(dateValue)
                        }
                        endTime = date.time
                    }
                } else if(isParsingEvent && it.startsWith(SUMMARY_HEADER)) {
                    val lastColon = it.lastIndexOf(":")
                    if (lastColon != -1) {
                        name = it.substring(lastColon + 1)
                    }
                }
            }
        }

        return list
    }
}

object ICSParserException: Exception()
