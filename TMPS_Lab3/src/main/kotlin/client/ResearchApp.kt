package client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.builder.*
import domain.factories.ResearchFacade
import domain.models.PaperComponent
import domain.models.ResearchDatabase
import domain.models.ResearchPaper
import utilities.*
import androidx.compose.ui.text.font.FontFamily

@Composable
fun ResearchManagementApp() {
    val database = remember { ResearchDatabase() }
    val facade = remember { ResearchFacade(database) }
    
    // Initialize with sample data
    LaunchedEffect(Unit) {
        val samplePapers = listOf(
            ResearchPaper(
                id = "P001",
                title = "Machine Learning in Educational Systems",
                authors = listOf("Dr. John Smith", "Prof. Jane Doe"),
                abstract = "An analysis of ML applications in modern education",
                keywords = listOf("Machine Learning", "Education", "AI"),
                publicationDate = "2024",
                department = "Computer Science"
            ),
            ResearchPaper(
                id = "P002",
                title = "Quantum Computing Algorithms",
                authors = listOf("Dr. Alice Johnson"),
                abstract = "Novel algorithms for quantum computing systems",
                keywords = listOf("Quantum Computing", "Algorithms"),
                publicationDate = "2023",
                department = "Physics"
            ),
            ResearchPaper(
                id = "P003",
                title = "Sustainable Energy Solutions",
                authors = listOf("Dr. Bob Wilson", "Dr. Carol Brown"),
                abstract = "Renewable energy research and applications",
                keywords = listOf("Energy", "Sustainability", "Renewable"),
                publicationDate = "2024",
                department = "Engineering"
            )
        )
        
        samplePapers.forEach { paper ->
            facade.addCompleteResearchPaper(
                paper = paper,
                citationIds = if (paper.id == "P001") listOf("P002", "P003") else emptyList(),
                reviewStatus = if (paper.id == "P001") "Approved" else "Pending",
                reviewComments = if (paper.id == "P001") "Excellent research methodology" else ""
            )
        }
    }
    
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var searchType by remember { mutableStateOf("keyword") }
    var selectedPaperId by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "University",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = "University Research Management System",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Demonstrating: Adapter, Decorator, and Facade Patterns",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Browse Papers") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Adapter Demo") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Decorator Demo") }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("System Overview") }
            )
        }
        
        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> BrowsePapersTab(
                    facade = facade,
                    searchQuery = searchQuery,
                    searchType = searchType,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchTypeChange = { searchType = it },
                    selectedPaperId = selectedPaperId,
                    onPaperSelected = { selectedPaperId = it }
                )
                1 -> AdapterDemoTab(facade = facade)
                2 -> DecoratorDemoTab(facade = facade, database = database)
                3 -> SystemOverviewTab(facade = facade)
            }
        }
    }
}

@Composable
fun BrowsePapersTab(
    facade: ResearchFacade,
    searchQuery: String,
    searchType: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchTypeChange: (String) -> Unit,
    selectedPaperId: String?,
    onPaperSelected: (String?) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var linkUrl by remember { mutableStateOf("") }
    var paperTitle by remember { mutableStateOf("") }
    var paperAuthor by remember { mutableStateOf("") }
    var paperAbstract by remember { mutableStateOf("") }
    var paperKeywords by remember { mutableStateOf("") }
    var paperDepartment by remember { mutableStateOf("Computer Science") }
    var paperYear by remember { mutableStateOf("2024") }
    
    val papers = remember(searchQuery, searchType) {
        if (searchQuery.isEmpty()) {
            facade.searchResearch("", "all")
        } else {
            facade.searchResearch(searchQuery, searchType)
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search controls and Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.weight(1f)
            )
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text("Type: $searchType")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Keyword") },
                        onClick = { onSearchTypeChange("keyword"); expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Department") },
                        onClick = { onSearchTypeChange("department"); expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = { onSearchTypeChange("all"); expanded = false }
                    )
                }
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Paper")
            }
        }
        
        // Add Paper Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add New Research Paper")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .width(500.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = linkUrl,
                            onValueChange = { linkUrl = it },
                            label = { Text("Paper URL/Link (optional)") },
                            leadingIcon = {
                                Icon(Icons.Default.Link, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://example.com/paper") }
                        )
                        OutlinedTextField(
                            value = paperTitle,
                            onValueChange = { paperTitle = it },
                            label = { Text("Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = paperAuthor,
                            onValueChange = { paperAuthor = it },
                            label = { Text("Author(s) * (comma-separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Dr. John Smith, Prof. Jane Doe") }
                        )
                        OutlinedTextField(
                            value = paperAbstract,
                            onValueChange = { paperAbstract = it },
                            label = { Text("Abstract") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                        OutlinedTextField(
                            value = paperKeywords,
                            onValueChange = { paperKeywords = it },
                            label = { Text("Keywords (comma-separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("AI, Machine Learning, Education") }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = paperDepartment,
                                onValueChange = { paperDepartment = it },
                                label = { Text("Department") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = paperYear,
                                onValueChange = { paperYear = it },
                                label = { Text("Year") },
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (paperTitle.isNotEmpty() && paperAuthor.isNotEmpty()) {
                                val newId = "P${String.format("%03d", papers.size + 1)}"
                                val authors = paperAuthor.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                val keywords = paperKeywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                
                                val newPaper = ResearchPaper(
                                    id = newId,
                                    title = paperTitle,
                                    authors = authors,
                                    abstract = paperAbstract.ifEmpty { "No abstract provided" },
                                    keywords = keywords.ifEmpty { listOf("General") },
                                    publicationDate = paperYear.ifEmpty { "2024" },
                                    department = paperDepartment.ifEmpty { "Computer Science" }
                                )
                                
                                facade.addCompleteResearchPaper(newPaper)
                                
                                // Reset form
                                linkUrl = ""
                                paperTitle = ""
                                paperAuthor = ""
                                paperAbstract = ""
                                paperKeywords = ""
                                paperDepartment = "Computer Science"
                                paperYear = "2024"
                                showAddDialog = false
                            }
                        },
                        enabled = paperTitle.isNotEmpty() && paperAuthor.isNotEmpty()
                    ) {
                        Text("Add Paper")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Results count
        if (papers.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Found ${papers.size} paper(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        
        // Papers list with scrollbar
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (papers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No papers found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Try a different search query",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            items(papers) { paper ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (selectedPaperId == paper.id) 2.dp else 0.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    onClick = { onPaperSelected(if (selectedPaperId == paper.id) null else paper.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPaperId == paper.id) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (selectedPaperId == paper.id) 8.dp else 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Article,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = paper.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = paper.authors.joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            if (selectedPaperId == paper.id) {
                                Icon(
                                    imageVector = Icons.Default.ExpandLess,
                                    contentDescription = "Expanded",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Collapsed",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = paper.department,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = paper.publicationDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        if (selectedPaperId == paper.id) {
                            val details = facade.getPaperDetails(paper.id)
                            if (details != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Abstract
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Abstract",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = paper.abstract,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Keywords
                                if (paper.keywords.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Label,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "Keywords:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        paper.keywords.forEach { keyword ->
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.secondaryContainer
                                            ) {
                                                Text(
                                                    text = keyword,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                // Statistics
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Link,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                            Text(
                                                text = "${details.citationCount}",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                            Text(
                                                text = "Citations",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RateReview,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = if (details.reviewStatus.contains("Approved")) "✓" else "⏳",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = "Review",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdapterDemoTab(facade: ResearchFacade) {
    val allPapers = remember { facade.searchResearch("", "all") }
    var selectedPaperIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var outputFormat by remember { mutableStateOf("CSV") }
    var convertedOutput by remember { mutableStateOf("") }
    var showOutput by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left side - Paper Selection
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Transform,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "Adapter Pattern Demo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select papers and convert them to different formats",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Selection controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Papers (${selectedPaperIds.size} selected)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = { 
                                    selectedPaperIds = if (selectedPaperIds.size == allPapers.size) {
                                        emptySet()
                                    } else {
                                        allPapers.map { it.id }.toSet()
                                    }
                                }
                            ) {
                                Text(if (selectedPaperIds.size == allPapers.size) "Deselect All" else "Select All")
                            }
                        }
                    }
                }
            }
            
            // Papers list with checkboxes
            if (allPapers.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No papers available",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Add papers in the Browse Papers tab first",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                allPapers.forEach { paper ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            selectedPaperIds = if (selectedPaperIds.contains(paper.id)) {
                                selectedPaperIds - paper.id
                            } else {
                                selectedPaperIds + paper.id
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPaperIds.contains(paper.id))
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = selectedPaperIds.contains(paper.id),
                                onCheckedChange = {
                                    selectedPaperIds = if (it) {
                                        selectedPaperIds + paper.id
                                    } else {
                                        selectedPaperIds - paper.id
                                    }
                                }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = paper.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${paper.authors.firstOrNull()} • ${paper.department} • ${paper.publicationDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Right side - Conversion Options and Output
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Output format selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Convert To Format:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("CSV", "JSON", "XML", "BibTeX", "Markdown").forEach { format ->
                            FilterChip(
                                selected = outputFormat == format,
                                onClick = { outputFormat = format },
                                label = { Text(format) },
                                leadingIcon = if (outputFormat == format) {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // Convert button
            Button(
                onClick = {
                    if (selectedPaperIds.isNotEmpty()) {
                        val selectedPapers = allPapers.filter { it.id in selectedPaperIds }
                        val adapter = when (outputFormat) {
                            "CSV" -> PaperToCSVAdapter()
                            "JSON" -> PaperToJSONAdapter()
                            "XML" -> PaperToXMLAdapter()
                            "BibTeX" -> PaperToBibTeXAdapter()
                            "Markdown" -> PaperToMarkdownAdapter()
                            else -> PaperToCSVAdapter()
                        }
                        convertedOutput = adapter.convert(selectedPapers)
                        showOutput = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPaperIds.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Transform,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Convert ${selectedPaperIds.size} Paper(s) to $outputFormat"
                )
            }
            
            // Output display
            if (showOutput && convertedOutput.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileCopy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Converted Output ($outputFormat)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${selectedPaperIds.size} paper(s)",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        HorizontalDivider()
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp, max = 400.dp)
                                .verticalScroll(rememberScrollState()),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = convertedOutput,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "💡 Adapter Pattern: The adapter converts ResearchPaper objects (internal format) to $outputFormat format (external format) without modifying the original papers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else if (selectedPaperIds.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Select papers to convert",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DecoratorDemoTab(facade: ResearchFacade, database: ResearchDatabase) {
    var decoratedInfo by remember { mutableStateOf("") }
    var selectedPaperId by remember { mutableStateOf("P001") }
    var includeCitations by remember { mutableStateOf(true) }
    var includeReview by remember { mutableStateOf(true) }
    var includeFormatting by remember { mutableStateOf(true) }
    var formatStyle by remember { mutableStateOf("Academic") }
    
    val papers = remember { facade.searchResearch("", "all") }
    
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left side - Controls
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = "Decorator Pattern Demo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dynamically add features to research papers (citations, reviews, formatting)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Paper selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Paper:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                if (papers.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Article,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = papers.find { it.id == selectedPaperId }?.title ?: "Select a paper",
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            papers.forEach { paper ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text("${paper.id}: ${paper.title}", fontWeight = FontWeight.Medium)
                                            Text(paper.department, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = { 
                                        selectedPaperId = paper.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Decorator options
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Decorator Options:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = includeCitations,
                            onCheckedChange = { includeCitations = it }
                        )
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Citations")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = includeReview,
                            onCheckedChange = { includeReview = it }
                        )
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Peer Review")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = includeFormatting,
                            onCheckedChange = { includeFormatting = it }
                        )
                        Icon(
                            imageVector = Icons.Default.FormatPaint,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Formatting")
                    }
                }
                
                if (includeFormatting) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Format Style:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = formatStyle == "Academic",
                            onClick = { formatStyle = "Academic" },
                            label = { Text("Academic") },
                            leadingIcon = if (formatStyle == "Academic") {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                        FilterChip(
                            selected = formatStyle == "Modern",
                            onClick = { formatStyle = "Modern" },
                            label = { Text("Modern") },
                            leadingIcon = if (formatStyle == "Modern") {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = {
                val paper = papers.find { it.id == selectedPaperId } ?: return@Button
                var component: PaperComponent = BasePaperComponent(paper)
                
                if (includeFormatting) {
                    component = FormattingDecorator(component, formatStyle)
                }
                if (includeCitations) {
                    component = CitationDecorator(component, database)
                }
                if (includeReview) {
                    val review = database.getReview(paper.id) ?: "Pending review"
                    component = PeerReviewDecorator(component, "In Review", review)
                }
                
                decoratedInfo = component.getDisplayInfo()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Apply Decorators")
        }
        
        }
        
        // Right side - Preview
        if (decoratedInfo.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Decorated Paper Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    HorizontalDivider()
                    Text(
                        text = decoratedInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Empty state
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Apply decorators to see preview",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SystemOverviewTab(facade: ResearchFacade) {
    val overview = remember { facade.getSystemOverview() }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = "Facade Pattern Demo - System Overview",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Simplified interface to complex database operations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Papers",
                value = "${overview.totalPapers}",
                icon = Icons.Default.Article,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "With Citations",
                value = "${overview.papersWithCitations}",
                icon = Icons.Default.Link,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "With Reviews",
                value = "${overview.papersWithReviews}",
                icon = Icons.Default.RateReview,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Departments",
                value = "${overview.departments}",
                icon = Icons.Default.Business,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Departments List
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Department List",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider()
                overview.departmentList.forEach { dept ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = dept,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


