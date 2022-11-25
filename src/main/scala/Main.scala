import com.github.tototoshi.csv._

/** Main program */
@main def main: Unit = 
  // Open CSV input file for reading
  val reader = CSVReader.open("Org.csv")
  // read header row from CSV
  val headers = reader.readNext()
  headers.map(
    headers => {
      // read all further rows from the CSV
      reader.iterator.foreach(
        line => {
          // for each CSV record, create one Org tree
          printf("* %s\n", line.head)
          println("  :LOGBOOK:")
          headers.tail.zip(line.tail).foreach(
            (date, times) => {
              if ! "".equals(times) then
                printf("  CLOCK: ")
                print("\n")
              end if
          )
          println("  :END:")
        }
      )
    }
  )
  reader.close()
