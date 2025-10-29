interface ReportGenerator {
    fun generate(students: List<Student>, teachers: List<Teacher>)
}

class SummaryReport : ReportGenerator {
    override fun generate(students: List<Student>, teachers: List<Teacher>) {
        println("\n📊 SUMMARY REPORT")
        println("  Total Students: ${students.size}")
        println("  Total Teachers: ${teachers.size}")
        println("  Average Grade: ${if (students.isEmpty()) "N/A" else String.format("%.1f", students.map { it.grade }.average())}")
    }
}

class DetailedReport : ReportGenerator {
    override fun generate(students: List<Student>, teachers: List<Teacher>) {
        println("\n📋 DETAILED REPORT")
        println("Students:")
        students.forEach { println("  • ${it.name} - Grade ${it.grade}") }
        println("\nTeachers:")
        teachers.forEach { println("  • ${it.name} - ${it.subject}") }
    }
}

class GradeReport : ReportGenerator {
    override fun generate(students: List<Student>, teachers: List<Teacher>) {
        println("\n📈 GRADE DISTRIBUTION REPORT")
        val gradeGroups = students.groupBy { it.grade }.toSortedMap()
        gradeGroups.forEach { (grade, studentList) ->
            println("  Grade $grade: ${studentList.size} student(s)")
        }
    }
}