import androidx.compose.ui.window.application
import client.ResearchManagementApp
import androidx.compose.material3.MaterialTheme

fun main() = application {
    androidx.compose.ui.window.Window(
        onCloseRequest = ::exitApplication,
        title = "University Research Management System"
    ) {
        MaterialTheme {
            ResearchManagementApp()
        }
    }
}
