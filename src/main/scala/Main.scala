import com.github.tototoshi.csv._

/** Main program */
@main def main: Unit = 
  // Open CSV export file for reading
  val reader = CSVReader.open("Org.csv")
  // read all rows from the CSV
  reader.iteratorWithHeaders
  // parse one CSV record to an Org tree
    .foreach(
      line => line.foreach {
        case (key, value) =>
          printf("%s -> %s\n", key, value)
      }
    )
  reader.close()
