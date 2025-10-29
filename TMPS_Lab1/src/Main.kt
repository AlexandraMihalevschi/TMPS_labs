
fun main() {
    // Create dependencies
    val repository = SchoolRepository()
    val printer = ConsolePrinter()
    val validator = InputValidator()
    val inputReader = InputReader()
    val idGenerator = IdGenerator()
    val idCardPrinter = IdCardPrinter()
    val menuDisplay = MenuDisplay()

    // Create services
    val studentService = StudentService(repository, validator, printer, inputReader, idGenerator)
    val teacherService = TeacherService(repository, validator, printer, inputReader, idGenerator)
    val reportService = ReportService(repository, printer, inputReader, menuDisplay)
    val idCardService = IdCardService(repository, printer, idCardPrinter)

    // Create controller
    val controller = ApplicationController(
        menuDisplay,
        inputReader,
        printer,
        studentService,
        teacherService,
        reportService,
        idCardService
    )

    // Start application
    controller.start()
}