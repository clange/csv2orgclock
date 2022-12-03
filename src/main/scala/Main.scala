import com.github.tototoshi.csv._
import java.time.LocalDate
import java.time.format.{DateTimeFormatter,DateTimeFormatterBuilder}

val startEndHhMmPattern = "([0-9]{1,2})[:.]([0-9]{2})-(?:([0-9]{1,2})[:.])?([0-9]{2})".r

def splitHhMm(time: String): Option[Tuple4[Int, Int, Int, Int]] =
  time match {
    case startEndHhMmPattern(group1, group2, group3, group4) => {
        val startHh = group1
        val startMm = group2
        val endHh = Option(group3).getOrElse(startHh)
        val endMm = group4
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
    // read all further rows from the CSV
    reader.iterator.foreach( line => {
      // for each CSV record, create one Org tree
      printf("* %s\n", line.head)
      println("  :LOGBOOK:")
      headers.tail.map( date =>
          LocalDate.parse(date, dateFormat)
          ).zip(line.tail).foreach( (date, intervals) => {
            if ! "".equals(intervals) then
              intervals.split(',').map(_.trim).foreach( interval => {
                if !interval.isBlank then
                splitHhMm(interval).map( (startHh, startMm, endHh, endMm) =>
                    val startDateTime = date.atTime(startHh, startMm)
                    val endDateTime = date.atTime(endHh, endMm)
                    // TODO handle post 24:00
                    printf("  CLOCK: [%s]--[%s] => \n", startDateTime.format(dateTimeFormat), endDateTime.format(dateTimeFormat))
                    )
              })
            end if
          })
        println("  :END:")
    })
  })
  reader.close()
