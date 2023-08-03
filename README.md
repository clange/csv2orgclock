## Convert CSV to org-mode clock data

This program converts a CSV table containing clock logs (i.e., at what times = columns you worked on what task = rows) to the [Org mode](https://orgmode.org) format for Emacs.
This is convenient when you do not have access to your full Org mode setup, e.g., on a mobile device.

For subsequent processing, e.g., generating reports that can be further transcribed into official timesheets, I recommend following the [clocktable-spreadsheet](https://github.com/clange/org-mode) principle.

See here for [a sample spreadsheet with a useful coloring setup](https://docs.google.com/spreadsheets/d/1aO8h8o7xVB1Rz_r4bL6rBATCjl_2OOMCxjP8Td2fwAo/edit?usp=sharing).  Exporting that to CSV creates an input file ready for this program.

### Usage

The program requires the name of a CSV file as an argument.  Thus, with [sbt](https://www.scala-sbt.org/), you can run it like

```
sbt 'run Org.csv'
```

This will write to standard output.  Once you are happy with the output, i.e., the program no longer throws errors, redirect the output to a `*.org` file.  For technical reasons, you will need to manually strip a few lines of sbt output at the top and bottom of that file before processing it with Org mode.

The most convenient way of fixing errors (e.g., incomplete entries) is to repetitively run a command like this:

```
sbt 'run Org.csv' | tee output.org
```

#### General notes

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
