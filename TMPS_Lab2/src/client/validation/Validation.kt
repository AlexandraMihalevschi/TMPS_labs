package client.validation

sealed class InputResult<out T> {
    data class Success<T>(val value: T) : InputResult<T>()
    data class Error(val message: String) : InputResult<Nothing>()
}

class InputValidator {
    fun validateId(id: String): InputResult<String> {
        return when {
            id.isBlank() -> InputResult.Error("ID cannot be empty")
            id.length < 3 -> InputResult.Error("ID must be at least 3 characters")
            !id.matches(Regex("[A-Za-z0-9_-]+")) -> InputResult.Error("ID can only contain letters, numbers, hyphens, and underscores")
            else -> InputResult.Success(id)
        }
    }

    fun validateName(name: String): InputResult<String> {
        return when {
            name.isBlank() -> InputResult.Error("Name cannot be empty")
            name.length < 2 -> InputResult.Error("Name must be at least 2 characters")
            else -> InputResult.Success(name)
        }
    }

    fun validateYear(year: String): InputResult<Int> {
        val yearInt = year.toIntOrNull()
        return when {
            yearInt == null -> InputResult.Error("Year must be a valid number")
            yearInt < 1000 -> InputResult.Error("Year must be 1000 or later")
            yearInt > 2025 -> InputResult.Error("Year cannot be in the future")
            else -> InputResult.Success(yearInt)
        }
    }

    fun validateDate(date: String): InputResult<String> {
        return when {
            date.isBlank() -> InputResult.Error("Date cannot be empty")
            !date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> InputResult.Error("Date must be in YYYY-MM-DD format")
            else -> InputResult.Success(date)
        }
    }

    fun validateArtifactType(type: String): InputResult<String> {
        val validTypes = listOf("painting", "sculpture", "artifact")
        return when {
            type.isBlank() -> InputResult.Error("Type cannot be empty")
            type.lowercase() !in validTypes -> InputResult.Error("Type must be one of: ${validTypes.joinToString(", ")}")
            else -> InputResult.Success(type.lowercase())
        }
    }
}