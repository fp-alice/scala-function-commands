package wtf.shekels.alice.reflection

import cats.effect.IO
import wtf.shekels.alice.annotations.CommandHandler
import wtf.shekels.alice.commands.{Command, LabelledCommand}
import cats.implicits._

object CommandHandlerLoader extends Loader[Class[_], List[LabelledCommand[_]]]("wtf.shekels.alice.commands.impl") {

  override def load: IO[List[LabelledCommand[_]]] = getClasses(basePath)

  protected def getClasses(packageName: String): IO[List[LabelledCommand[_]]] = for {
    resources <- getPackageResources(packageName)
    classes <- resources.traverse(classFromFile(packageName, _))
  } yield classes.flatMap(loadCommands).flatten

  protected def getLabelledCommands(clazz: Class[_]): List[LabelledCommand[_]] = {
    val instance = clazz.getDeclaredConstructor().newInstance()
    clazz.getMethods
      .filter(m => m.getReturnType.isAssignableFrom(classOf[Command[_]]))
      .map(m => LabelledCommand(clazz.getSimpleName, m.getName, m.invoke(instance).asInstanceOf[Command[_]]))
      .toList
  }

  protected def loadCommands(clazz: Class[_]): Option[List[LabelledCommand[_]]] = for {
    _ <- Loader.getAnnotation[CommandHandler](clazz)
  } yield getLabelledCommands(clazz)
}
