package wtf.shekels.alice.commands

case class LabelledCommand[I](className: String, commandName: String, command: Command[I])
