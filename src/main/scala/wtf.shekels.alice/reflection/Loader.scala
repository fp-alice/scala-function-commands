package wtf.shekels.alice.reflection

import cats.effect.IO

import java.io.File
import scala.jdk.CollectionConverters._
import cats.implicits._

import scala.reflect.ClassTag

abstract class Loader[T, R](protected val basePath: String) {
  def load: IO[R]

  protected def getPackageResources(packageName: String): IO[List[File]] = for {
    classLoader <- IO(getClass.getClassLoader)
    path = packageName.replace(".", "/")
    paths <- IO(classLoader.getResources(path).asScala.toList)
    files <- IO(paths.flatMap(url => new File(url.getFile).listFiles()))
    walked <- files.filter(_.isDirectory).traverse(f => getPackageResources(path + "/" + f.getName))
  } yield walked.foldl(files)(_ ++ _).filterNot(_.isDirectory)

  protected def fileToClassPath(file: File): IO[String] = IO(file.getName.dropRight(6))

  protected def classFromFile(packageName: String, file: File): IO[Class[_]] = for {
    path <- fileToClassPath(file)
  } yield Class.forName(f"${packageName.replace("/", ".")}.$path")
}

object Loader {
  def getAnnotation[T: ClassTag](clazz: Class[_]): Option[T] = {
    val annotationClass = implicitly[ClassTag[T]].runtimeClass
    val annotation = clazz.getAnnotations.find(_.annotationType() == annotationClass)
    annotation.map(_.asInstanceOf[T])
  }
}