interface Identifiable {
    fun getId(): Int
    fun getName(): String
}

interface Gradeable {
    fun getGrade(): Int
}

interface Teachable {
    fun getSubject(): String
}

class StudentRecord(private val student: Student) : Identifiable, Gradeable {
    override fun getId() = student.id
    override fun getName() = student.name
    override fun getGrade() = student.grade
}

class TeacherRecord(private val teacher: Teacher) : Identifiable, Teachable {
    override fun getId() = teacher.id
    override fun getName() = teacher.name
    override fun getSubject() = teacher.subject
}

class IdCardPrinter {
    fun printIdCard(person: Identifiable) {
        println("  🪪 ID Card: #${person.getId()} - ${person.getName()}")
    }
}