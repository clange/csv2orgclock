import com.github.tototoshi.csv._
import scala.util.matching.Regex

val startEndHhMmPattern = Regex("([0-9]{1,2})[:.]([0-9]{2})-(?:([0-9]{1,2})[:.])?([0-9]{2})")

def splitHhMm(time: String): Option[Tuple4[Int, Int, Int, Int]] =
  for patternMatch <- startEndHhMmPattern.findAllMatchIn(time) do
    val startHh = patternMatch.group(1)
    val startMm = patternMatch.group(2)
    val match3 = patternMatch.group(3)
    val endHh = Option(match3).getOrElse(startHh)
    val endMm = patternMatch.group(4)
    return Some((startHh.toInt, startMm.toInt, endHh.toInt, endMm.toInt))
  None

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
      headers.tail.zip(line.tail).foreach( (date, intervals) => {
        if ! "".equals(intervals) then
          intervals.split(',').map(_.trim).foreach( interval => {
            if !interval.isBlank then
              splitHhMm(interval).map( (startHh, startMm, endHh, endMm) =>
                // TODO handle post 24:00
                printf("  CLOCK: [%s %02d:%02d]--[%s %02d:%02d] => \n", date, startHh, startMm, date, endHh, endMm)
              )
          })
        end if
      })
      println("  :END:")
    })
  })
  reader.close()
