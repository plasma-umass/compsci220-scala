sealed trait Regex {

  def >>(other: Regex): Regex = (this, other) match {
    case (Zero, _) => Zero
    case (_, Zero) => Zero
    case (re1, One) => re1
    case (One, re2) => re2
    case (re1, re2) => Seq(re1, re2)
  }

  def +(other: Regex): Regex = (this, other) match {
    case (Zero, re2) => re2
    case (re1, Zero) => re1
    case (re1, re2) => if (re1 == re2) re1 else Alt(re1, re2)
  }

  override def toString(): String = {

    sealed trait Cxt
    case object CxtStar extends Cxt
    case object CxtAlt extends Cxt
    case object CxtSeq extends Cxt

    val b = new StringBuilder()

    def loop(regex: Regex, cxt: Cxt): Unit = regex match {
      case Character(ch) => b.append(ch)
      case One => b.append("𝟄")
      case Zero => b.append('∅')
      case Alt(re1, re2) => if (cxt == CxtStar || cxt == CxtSeq) {
        b.append('(')
        loop(re1, CxtAlt)
        b.append('|')
        loop(re2, CxtAlt)
        b.append(')')
      }
      else {
        loop(re1, CxtAlt)
        b.append('|')
        loop(re2, CxtAlt)
      }
      case Seq(re1, re2) => if (cxt == CxtStar) {
        b.append('(')
        loop(re1, CxtSeq)
        loop(re2, CxtSeq)
        b.append(')')
      }
      else {
        loop(re1, CxtSeq)
        loop(re2, CxtSeq)
      }
      case Star(re) => {
        loop(re, CxtStar)
        b.append('*')
      }
    }

    loop(this, CxtSeq)
    b.toString
  }

}
case class Character(ch: Char) extends Regex
case class Seq(re1: Regex, re2: Regex) extends Regex
case class Alt(re1: Regex, re2: Regex) extends Regex
case class Star(re: Regex) extends Regex
case object Zero extends Regex
case object One extends Regex
