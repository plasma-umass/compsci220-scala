package cmpsci220.plugin

object Plugin extends sbt.AutoPlugin {
  import org.apache.commons.io.IOUtils
  import java.io.File
  import sbt._
  import Keys._
  import FileUtils._
  import spray.json._
  import java.nio.file._

  override def trigger = allRequirements
  override def requires = plugins.JvmPlugin

  object autoImport {
    lazy val submit = TaskKey[Unit]("submit")
    lazy val checkstyle = TaskKey[Unit]("checkstyle")
    lazy val findMisplacedFiles = TaskKey[Unit]("find-misplaced-files")
    lazy val directoryWarnings = TaskKey[Unit]("directory-warnings")
  }

  import autoImport._

  def submitTask(): Unit = {

    val files = findFiles("")(fileNameHasSuffix(_, ".scala"))
    val tgz = TgzBuilder("submission.tar.gz")
    for (file <- files) {
      tgz.add(file)
    }

    val metadata = JsObject("time" -> JsNumber(System.currentTimeMillis))
    tgz.write(".metadata", metadata.compactPrint.getBytes)

    tgz.close()

    println("Created submission.tar.gz. Upload this file to Moodle.")
  }

  override def projectSettings = super.projectSettings ++
    Seq(org.scalastyle.sbt.ScalastylePlugin.projectSettings :_*) ++
    Seq(
      scalaVersion := "2.11.8",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-Xfatal-warnings"
      ),
      resolvers += "PLASMA" at "https://dl.bintray.com/plasma-umass/maven",
      parallelExecution in Test := false,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test",
      libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
      libraryDependencies += "edu.umass.cs" %% "compsci220" % "1.3.1",
      findMisplacedFiles := Tasks.findMisplacedFiles(streams.value.log),
      directoryWarnings := Tasks.directoryWarnings(streams.value.log),
      checkstyle := Tasks.checkstyle(
        streams.value,
        target.value,
        org.scalastyle.sbt.ScalastylePlugin.scalastyleTarget.value),
      // Run ScalaStyle after compilation succeeds. ScalaStyle displays
      // terrible error messages when files have syntax errors. By running
      // it after compilation, we get better messages from scalac. However,
      // this means that the program builds successfully even if it doesn't
      // pass the style checker.
      checkstyle <<= checkstyle.triggeredBy(compile in Compile),
      // Print warnings about misplaced files and non-existent directories
      // before compilation.
      (compile in Compile) <<= (compile in Compile)
        .dependsOn(findMisplacedFiles, directoryWarnings),
      submit := {
        submitTask
      },
      // We refuse to create a submittable .tgz if the program doesn't
      // compile and pass ScalaStyle.
      submit <<= submit.dependsOn(compile in Compile, checkstyle)
    )
}
