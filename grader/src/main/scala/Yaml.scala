package grader

import org.yaml.snakeyaml._
import java.nio.file._

object GradingYaml {

 import constructor.Constructor

  private val ctor = new Constructor(classOf[FeedbackBean])
  private val desc = new TypeDescription(classOf[FeedbackBean])
  desc.putListPropertyType("rubric", classOf[TestResultBean])
  ctor.addTypeDescription(desc)
  val yaml = new Yaml(ctor)

  def open(path: Path): GradingYaml = {
    yaml.load(new String(Files.readAllBytes(path))) match {
      case bean: FeedbackBean => new GradingYaml(path, bean)
      case _ => throw new IllegalArgumentException(s"$path is not a grading.yaml file")
    }
  }

}

class GradingYaml private[grader] (path: Path, bean: FeedbackBean) {

  import GradingYaml._

  def addCriterion(description: String, points: Int, maxPoints: Int): Unit = {
    val result = new TestResultBean()
    result.setCriterion(description)
    result.setScore(points)
    result.setMaxScore(maxPoints)
    bean.getRubric.add(result)
    updateTotal()
    updateTime()
  }

  def updateTime(): Unit = {
    bean.setTime(new java.util.Date().toString)
  }

  private def updateTotal(): Unit = {
    import scala.collection.JavaConversions._
    bean.getCumulative.setScore(bean.getRubric.map(_.getScore).sum)
    bean.getCumulative.setMaxScore(bean.getRubric.map(_.getMaxScore).sum)
  }

  def save(): Unit = {
    val dump = yaml.dumpAs(bean, nodes.Tag.MAP, DumperOptions.FlowStyle.BLOCK)
    Files.write(path, dump.getBytes)
  }

}
