data class Student(val id: Int, val name: String, val grade: Int)
data class Teacher(val id: Int, val name: String, val subject: String)

// Repository - handles only data storage
class SchoolRepository {
    private val students = mutableListOf<Student>()
    private val teachers = mutableListOf<Teacher>()

    fun saveStudent(student: Student) {
        students.add(student)
    }

    fun saveTeacher(teacher: Teacher) {
        teachers.add(teacher)
    }

    fun getAllStudents(): List<Student> = students.toList()
    fun getAllTeachers(): List<Teacher> = teachers.toList()
}

// Printer - handles only displaying information
class ConsolePrinter {
    fun printStudent(student: Student) {
        println("  👨‍🎓 Student #${student.id}: ${student.name} (Grade ${student.grade})")
    }

    fun printTeacher(teacher: Teacher) {
        println("  👨‍🏫 Teacher #${teacher.id}: ${teacher.name} - Subject: ${teacher.subject}")
    }

    fun printMessage(message: String) {
        println("  ✓ $message")
    }

    fun printError(error: String) {
        println("  ✗ $error")
    }
}

// Validator - handles ONLY validation logic
class InputValidator {
    fun isValidName(name: String): Boolean = name.isNotBlank() && name.length >= 2
    fun isValidGrade(grade: Int): Boolean = grade in 1..10
}