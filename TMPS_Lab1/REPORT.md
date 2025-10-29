# Laboratory Work 1: Software Design – SOLID Principles 

**Course**: Software Engineering and Design
**Author**: Mihalevschi Alexandra

---

### Theory

The **SOLID principles** represent a set of five object-oriented design guidelines proposed to enhance code **maintainability, scalability, and readability**. They encourage modularity and separation of concerns, allowing systems to grow and adapt with minimal rework.

For this laboratory, 3 of the 5 principles were implemented and demonstrated theoretically and practically in Kotlin:

1. **S – Single Responsibility Principle (SRP):**
   Each class should have only one reason to change, meaning it should handle one well-defined responsibility.
   → In the project, components like `InputReader`, `Validator`, `SchoolRepository`, and `ConsolePrinter` each handle a single role.

2. **O – Open/Closed Principle (OCP):**
   Software entities should be open for extension but closed for modification.
   → Implemented through the `ReportGenerator` interface and its concrete implementations (`SummaryReport`, `DetailedReport`, `GradeReport`), allowing new report types without modifying existing logic.

3. **I – Interface Segregation Principle (ISP):**
   Clients should not be forced to depend on methods they do not use.
   → Demonstrated through `Identifiable`, `Gradeable`, and `Teachable` interfaces, where students and teachers implement only the interfaces relevant to their type.
---

### Objectives

* **Apply** SOLID design principles in a structured Kotlin application.
* **Demonstrate** modularity and clear separation of concerns.
* **Build** a simple console-based *School Registry System* adhering to these principles.

---

### Implementation Description

**Project name:** `SchoolRegistrySOLID`
**Language:** Kotlin


---

#### System Overview

The application simulates a small **School Registry Management System**, where users can:

* Add students and teachers
* View all registered entities
* Generate different types of reports
* Print ID cards
* Exit the application

It operates through a text-based menu interface.

---

#### Core Classes - Single Responsibility Principle

Each class was refactored to perform one distinct responsibility:

* `SchoolRepository` — manages in-memory data storage for students and teachers.
* `ConsolePrinter` — handles display and output formatting.
* `InputReader` — reads and parses user input.
* `InputValidator` — validates input fields (e.g., name, grade).
* `IdGenerator` — generates sequential IDs for new entities.
* `MenuDisplay` — manages and displays menus.
* `StudentService` and `TeacherService` — coordinate operations related to students and teachers.
* `ReportService` — manages all reporting functionality.
* `IdCardService` — prints formatted identification cards.
* `ApplicationController` — centralizes control flow and coordinates the entire system.

This strong separation of duties exemplifies the **Single Responsibility Principle**.

---

#### Open/Closed Principle 

The system introduces a `ReportGenerator` interface, allowing reports to be **extended** independently.
Implemented variants include:

* `SummaryReport` – shows total counts and average grade.
* `DetailedReport` – lists all students and teachers with details.
* `GradeReport` – visualizes grade distribution.

To extend functionality, one could simply add a new report class (e.g., `TopStudentsReport`) implementing `ReportGenerator`, **without modifying** existing code.

This design adheres to the **Open/Closed Principle**.

---

#### Interface Segregation Principle 

The model uses **segregated interfaces** to ensure that entities only implement what they actually need:

```kotlin
interface Identifiable { fun getId(): Int; fun getName(): String }
interface Gradeable { fun getGrade(): Int }
interface Teachable { fun getSubject(): String }
```

Classes like `StudentRecord` and `TeacherRecord` implement only relevant interfaces:

* `StudentRecord` → `Identifiable`, `Gradeable`
* `TeacherRecord` → `Identifiable`, `Teachable`

This prevents unnecessary dependencies and improves flexibility — a clear illustration of the **Interface Segregation Principle**.

---

#### Program Flow

1. **Header** and **Main Menu** are displayed by `MenuDisplay`.
2. User selects an action via numeric input.
3. `ApplicationController` delegates to appropriate service:

    * Add/view students or teachers
    * Generate chosen report
    * Print ID cards
4. Each service handles logic independently using its injected dependencies.
5. The application runs in a loop until the user selects *Exit*.

This architecture embodies **modular design and low coupling**.

---

#### Example Console Interaction

```
═════════════════════════════════════
   SCHOOL REGISTRY SYSTEM
   SOLID Principles: S, O, I
═════════════════════════════════════

─────────────────────────────────────
1. Add Student
2. Add Teacher
3. View All Students
4. View All Teachers
5. Generate Report
6. Print ID Cards
7. Exit
─────────────────────────────────────
Choose option: 1

➕ ADD STUDENT
  Name: John
  Grade (1-12): 8
  ✓ Student added successfully!
  👨‍🎓 Student #1: John (Grade 8)
```

Generating a report:

```
📊 SELECT REPORT TYPE
  1. Summary Report
  2. Detailed Report
  3. Grade Distribution
Choose report: 1

📊 SUMMARY REPORT
  Total Students: 3
  Total Teachers: 2
  Average Grade: 8.7
```
---

### Conclusions

This laboratory project effectively showcases the practical application of the **SOLID principles** in Kotlin.

* **Single Responsibility** ensures each class serves one focused purpose, simplifying debugging and refactoring.
* **Open/Closed** promotes easy feature extension without touching core code.
* **Interface Segregation** minimizes unnecessary dependencies and enforces clean abstractions.

Overall, the “School Registry” system provides a concrete, didactic example of how applying SOLID principles leads to a **robust, maintainable, and professional-grade software design** — even for a simple console application.

---
