import kotlin.system.exitProcess
// Manages ID generation only
class IdGenerator {
    private var nextStudentId = 1
    private var nextTeacherId = 1

    fun getNextStudentId(): Int = nextStudentId++
    fun getNextTeacherId(): Int = nextTeacherId++
}

// Handles user input only
class InputReader {
    fun readOption(): Int? {
        print("Choose option: ")
        return readLine()?.toIntOrNull()
    }

    fun readName(prompt: String): String {
        print("  $prompt: ")
        return readLine() ?: ""
    }

    fun readGrade(): Int? {
        print("  Grade (1-12): ")
        return readLine()?.toIntOrNull()
    }

    fun readSubject(): String {
        print("  Subject: ")
        return readLine() ?: ""
    }

    fun readReportType(): Int? {
        print("Choose report: ")
        return readLine()?.toIntOrNull()
    }
}

// Displays menu only
class MenuDisplay {
    fun showMainMenu() {
        println("\n─────────────────────────────────────")
        println("1. Add Student")
        println("2. Add Teacher")
        println("3. View All Students")
        println("4. View All Teachers")
        println("5. Generate Report")
        println("6. Print ID Cards")
        println("7. Exit")
        println("─────────────────────────────────────")
    }

    fun showReportMenu() {
        println("\n📊 SELECT REPORT TYPE")
        println("  1. Summary Report")
        println("  2. Detailed Report")
        println("  3. Grade Distribution")
    }

    fun showHeader() {
        println("═════════════════════════════════════")
        println("   SCHOOL REGISTRY SYSTEM")
        println("   SOLID Principles: S, O, I")
        println("═════════════════════════════════════\n")
    }
}

// Handles student operations ONLY
class StudentService(
    private val repository: SchoolRepository,
    private val validator: InputValidator,
    private val printer: ConsolePrinter,
    private val inputReader: InputReader,
    private val idGenerator: IdGenerator
) {
    fun addStudent() {
        println("\n➕ ADD STUDENT")
        val name = inputReader.readName("Name")

        if (!validator.isValidName(name)) {
            printer.printError("Invalid name!")
            return
        }

        val grade = inputReader.readGrade() ?: 0

        if (!validator.isValidGrade(grade)) {
            printer.printError("Invalid grade! Must be 1-12")
            return
        }

        val student = Student(idGenerator.getNextStudentId(), name, grade)
        repository.saveStudent(student)
        printer.printMessage("Student added successfully!")
        printer.printStudent(student)
    }

    fun viewAllStudents() {
        println("\n👨‍🎓 ALL STUDENTS")
        val students = repository.getAllStudents()
        if (students.isEmpty()) {
            printer.printMessage("No students registered yet.")
        } else {
            students.forEach { printer.printStudent(it) }
        }
    }
}

// Handles teacher operations ONLY
class TeacherService(
    private val repository: SchoolRepository,
    private val validator: InputValidator,
    private val printer: ConsolePrinter,
    private val inputReader: InputReader,
    private val idGenerator: IdGenerator
) {
    fun addTeacher() {
        println("\n➕ ADD TEACHER")
        val name = inputReader.readName("Name")

        if (!validator.isValidName(name)) {
            printer.printError("Invalid name!")
            return
        }

        val subject = inputReader.readSubject()

        if (!validator.isValidName(subject)) {
            printer.printError("Invalid subject!")
            return
        }

        val teacher = Teacher(idGenerator.getNextTeacherId(), name, subject)
        repository.saveTeacher(teacher)
        printer.printMessage("Teacher added successfully!")
        printer.printTeacher(teacher)
    }

    fun viewAllTeachers() {
        println("\n👨‍🏫 ALL TEACHERS")
        val teachers = repository.getAllTeachers()
        if (teachers.isEmpty()) {
            printer.printMessage("No teachers registered yet.")
        } else {
            teachers.forEach { printer.printTeacher(it) }
        }
    }
}

// Handles report generation ONLY
class ReportService(
    private val repository: SchoolRepository,
    private val printer: ConsolePrinter,
    private val inputReader: InputReader,
    private val menuDisplay: MenuDisplay
) {
    fun generateReport() {
        menuDisplay.showReportMenu()

        val students = repository.getAllStudents()
        val teachers = repository.getAllTeachers()

        val report: ReportGenerator? = when (inputReader.readReportType()) {
            1 -> SummaryReport()
            2 -> DetailedReport()
            3 -> GradeReport()
            else -> null
        }

        if (report != null) {
            report.generate(students, teachers)
        } else {
            printer.printError("Invalid report type!")
        }
    }
}

// Handles ID card printing ONLY
class IdCardService(
    private val repository: SchoolRepository,
    private val printer: ConsolePrinter,
    private val idCardPrinter: IdCardPrinter
) {
    fun printIdCards() {
        println("\n🪪 ID CARDS")
        val students = repository.getAllStudents()
        val teachers = repository.getAllTeachers()

        if (students.isEmpty() && teachers.isEmpty()) {
            printer.printMessage("No members to print ID cards for.")
            return
        }

        students.forEach { idCardPrinter.printIdCard(StudentRecord(it)) }
        teachers.forEach { idCardPrinter.printIdCard(TeacherRecord(it)) }
    }
}

// Coordinates the application flow ONLY
class ApplicationController(
    private val menuDisplay: MenuDisplay,
    private val inputReader: InputReader,
    private val printer: ConsolePrinter,
    private val studentService: StudentService,
    private val teacherService: TeacherService,
    private val reportService: ReportService,
    private val idCardService: IdCardService
) {
    fun start() {
        menuDisplay.showHeader()

        while (true) {
            menuDisplay.showMainMenu()
            when (inputReader.readOption()) {
                1 -> studentService.addStudent()
                2 -> teacherService.addTeacher()
                3 -> studentService.viewAllStudents()
                4 -> teacherService.viewAllTeachers()
                5 -> reportService.generateReport()
                6 -> idCardService.printIdCards()
                7 -> {
                    println("\n👋 Goodbye!")
                    exitProcess(0)
                }
                else -> printer.printError("Invalid option!")
            }
        }
    }
}
