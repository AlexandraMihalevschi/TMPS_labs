package client.input

import client.validation.InputValidator
import client.validation.InputResult

class InputReader {
    private val validator = InputValidator()

    fun readString(prompt: String, default: String = ""): String {
        print(prompt)
        return readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: default
    }

    fun readValidatedInput(
        prompt: String,
        validator: (String) -> InputResult<String>,
        maxRetries: Int = 3
    ): String? {
        repeat(maxRetries) { attempt ->
            print(prompt)
            val input = readLine()?.trim() ?: ""

            when (val result = validator(input)) {
                is InputResult.Success -> return result.value
                is InputResult.Error -> {
                    println("❌ ${result.message}")
                    if (attempt < maxRetries - 1) {
                        println("   Please try again (${maxRetries - attempt - 1} attempts remaining)")
                    }
                }
            }
        }
        println("❌ Max retries exceeded")
        return null
    }

    fun readValidatedYear(prompt: String, maxRetries: Int = 3): Int? {
        repeat(maxRetries) { attempt ->
            print(prompt)
            val input = readLine()?.trim() ?: ""

            when (val result = validator.validateYear(input)) {
                is InputResult.Success -> return result.value
                is InputResult.Error -> {
                    println("❌ ${result.message}")
                    if (attempt < maxRetries - 1) {
                        println("   Please try again (${maxRetries - attempt - 1} attempts remaining)")
                    }
                }
            }
        }
        println("❌ Max retries exceeded")
        return null
    }

    fun readConfirmation(prompt: String): Boolean {
        print("$prompt (y/n): ")
        return readLine()?.trim()?.lowercase() in listOf("y", "yes")
    }
}