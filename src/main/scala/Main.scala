import com.github.tototoshi.csv._
import java.time.LocalDate
import java.time.format.{DateTimeFormatter,DateTimeFormatterBuilder}
import java.time.temporal.ChronoUnit._

val startEndHhMmPattern = "([0-9]{1,2})[:.]([0-9]{2})-(?:([0-9]{1,2})[:.])?([0-9]{2})".r

/** split a time interval (possibly in abbreviated notation)
 *  into the start/end hour/minute */
def splitHhMm(time: String): Option[Tuple4[Int, Int, Int, Int]] =
  time match {
    case startEndHhMmPattern(group1, group2, group3, group4) => {
        val startHh = group1.toInt
        val startMm = group2.toInt
        val endMm = group4.toInt
        val endHh = if group3 == null then
          // abbreviated notation: when the end hour is not given,
          // use the same or next hour (whichever results in a positive duration)
          if endMm <= startMm then
            (startHh + 1) % 24
          else startHh
        else
          group3.toInt
        Some((startHh.toInt, startMm.toInt, endHh.toInt, endMm.toInt))
      }
    case _ => None
  }

/** formatter for reading dates */
val dateFormat = DateTimeFormatterBuilder()
  .appendPattern("uuuu-MM-dd")
  .toFormatter()

/** formatter for writing date/time stamps */
val dateTimeFormat = DateTimeFormatterBuilder()
  .appendPattern("uuuu-MM-dd E HH:mm")
  .toFormatter()

/** Main program */
@main def main: Unit = 
  // Open CSV input file for reading
  val reader = CSVReader.open("Org.csv")
  // read header row from CSV
  val headers = reader.readNext()
  headers.map( headers => {
    val parsedReverseHeaders = headers.tail.reverse.map( date =>
        LocalDate.parse(date, dateFormat)
        )
    // read all further rows from the CSV
    reader.iterator.foreach( line => {
      // for each CSV record, create one Org tree
      printf("* %s%s", line.head, System.lineSeparator())
      println("  :LOGBOOK:")
      parsedReverseHeaders.zip(line.tail.reverse).foreach( (date, intervals) => {
          // process each cell, i.e., task/day entry
          if ! "".equals(intervals) then
            intervals.split(',').reverse.map(_.trim).foreach( interval => {
              if !interval.isBlank then
                splitHhMm(interval).map( (startHh, startMm, endHh, endMm) =>
                    val startDateTime = date.atTime(startHh, startMm)
                    val parsedEndDateTime = date.atTime(endHh, endMm)
                    // if the end time is less than the start time, assume an interval going beyond midnight into the next day
                    val endDateTime = if parsedEndDateTime.isBefore(startDateTime)
                      then parsedEndDateTime.plusDays(1)
                      else parsedEndDateTime
                    val diffMinutes = MINUTES.between(startDateTime, endDateTime)
                    printf("  CLOCK: [%s]--[%s] => %2d:%02d%s", startDateTime.format(dateTimeFormat), endDateTime.format(dateTimeFormat), diffMinutes / 60, diffMinutes % 60, System.lineSeparator())
                  ).getOrElse(throw RuntimeException("Task '%s' on date '%s': interval '%s' cannot be parsed%s".format(line.head, date, interval, System.lineSeparator())))
            })
          end if
        })
      println("  :END:")
    })
  }).getOrElse(throw RuntimeException("Expected a header row with dates"))
  reader.close()
