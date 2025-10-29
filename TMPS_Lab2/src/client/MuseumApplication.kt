package client

import client.input.InputReader
import client.commands.*

class MuseumApplication {
    private val reader = InputReader()

    fun run() {
        println("🏛️  Museum Registry System Demo\n")

        while (true) {
            println("==== Menu ====")
            println("1. List all artifacts")
            println("2. Add a new artifact")
            println("3. Create an exhibition")
            println("4. Exit")
            print("Choose an option (1-4): ")

            val input = readLine()?.trim() ?: ""

            val command: MenuCommand? = when (input) {
                "1" -> ListArtifactsCommand()
                "2" -> AddArtifactCommand(reader)
                "3" -> CreateExhibitionCommand(reader)
                "4" -> {
                    if (reader.readConfirmation("Are you sure you want to exit?")) {
                        println("👋 Exiting. Goodbye!")
                        return
                    }
                    null
                }
                else -> {
                    println("❌ Invalid option, try again.")
                    null
                }
            }

            command?.execute()
            println()
        }
    }
}