import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Attribute._
import org.fusesource.jansi.Ansi.Color._

package object cmpsci220 {

  AnsiConsole.systemInstall()

  /** This value enables all test cases */
  implicit val testsEnabled = TestsEnabled(true)

  val error = sys.error _

  /** A test case */
  def test(description : String)(body : => Unit)
    (implicit testsEnabled : TestsEnabled) : Unit = {
    if (testsEnabled.isEnabled) {
      try {
        body
        print(ansi().fg(GREEN).a(s"Succeeded $description").reset().newline())
      }
      catch {
        case (exn : Throwable) => {
          print(ansi().fg(RED).a(s"Failed $description").reset().newline()
                      .a(INTENSITY_FAINT).a(exn.toString).reset().newline())
        }
      }
    }
  }

  def fails(description : String)(body : => Unit)
    (implicit testsEnabled : TestsEnabled) : Unit = {
    if (testsEnabled.isEnabled) {
      try {
        body
        print(ansi().fg(RED).a(s"Failed $description").reset().newline())
      }
      catch {
        case (exn : Throwable) => {
          print(ansi().fg(GREEN).a(s"Succeeded $description").reset().newline()
                      .a(INTENSITY_FAINT).a(exn.toString).reset().newline())
        }
      }
    }
  }


}