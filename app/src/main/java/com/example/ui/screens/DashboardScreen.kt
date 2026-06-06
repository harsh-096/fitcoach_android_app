package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DailyTask
import com.example.ui.FitViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FitViewModel,
    onScannerClicked: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val tasks by viewModel.dailyTasks.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingPlan.collectAsStateWithLifecycle()

    val dateFormatter = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val todayDate = dateFormatter.format(Date())

    var selectedMeal by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<DailyTask?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
    ) {
        // Dynamic Background Accents
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top Right Emerald
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 64.dp, y = (-64).dp)
                        .size(250.dp)
                        .background(Emerald200.copy(alpha = 0.4f), shape = CircleShape)
                        .blur(80.dp)
                )
                // Bottom Left Blue
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-64).dp, y = (-100).dp)
                        .size(300.dp)
                        .background(Blue200.copy(alpha = 0.3f), shape = CircleShape)
                        .blur(100.dp)
                )
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Surface(
                    color = White80,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BottomNavIcon(icon = Icons.Default.Dashboard, label = "Plan", selected = true)
                            BottomNavIcon(icon = Icons.Default.FitnessCenter, label = "Train", selected = false)
                            BottomNavIcon(icon = Icons.Default.MonitorWeight, label = "Progress", selected = false)
                            BottomNavIcon(icon = Icons.Default.Settings, label = "Goals", selected = false)
                        }
                    }
                }
            }
        ) { padding ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Emerald500)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("AI is building your custom plan...", color = Slate800)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp, start = 24.dp, end = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = todayDate.uppercase(),
                                    color = Slate500,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Hey, ${profile?.name?.substringBefore(" ") ?: "Athlete"}",
                                    color = Slate800,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                )
                            }
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = borderStroke(White40),
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Emerald500)
                                }
                            }
                        }
                    }

                    item {
                        ProgressCard()
                    }

                    item {
                        Text(
                            text = "Up Next",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Slate900,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    val workouts = tasks.filter { it.type == "WORKOUT" }
                    items(workouts) { task ->
                        WorkoutCard(task = task, onCheckedChange = { viewModel.toggleTask(task) })
                    }

                    val meals = tasks.filter { it.type == "MEAL" }
                    items(meals) { task ->
                        MealCard(task = task, onCheckedChange = { viewModel.toggleTask(task) }, onCardClick = { selectedMeal = task })
                    }
                    
                    item {
                        WaterTrackerCard()
                    }
                }
            }
        }
        
        if (selectedMeal != null) {
            AlertDialog(
                onDismissRequest = { selectedMeal = null },
                title = { Text(text = selectedMeal?.title ?: "Recipe", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Ingredients", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
                        Text(text = selectedMeal?.ingredients.takeIf { !it.isNullOrBlank() } ?: "None provided", fontSize = 14.sp, color = Slate800)
                        
                        Text(text = "Instructions", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
                        Text(text = selectedMeal?.recipeSteps.takeIf { !it.isNullOrBlank() } ?: "None provided", fontSize = 14.sp, color = Slate800)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedMeal = null }) {
                        Text("Close", color = Emerald500)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

private fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@Composable
fun ProgressCard() {
    Surface(
        color = White60,
        shape = RoundedCornerShape(32.dp),
        border = borderStroke(White40),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Today's Fuel", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Slate900)
                Surface(
                    color = Emerald200.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    Text(
                        "72% GOAL", 
                        color = Emerald500, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 0.72f },
                        modifier = Modifier.fillMaxSize(),
                        color = Emerald500,
                        trackColor = Slate200,
                        strokeWidth = 8.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("1,420", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        Text("KCAL LEFT", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Medium)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MacroBar(label = "Protein", value = "92g", total = "140g", progress = 0.65f, color = Blue500)
                    MacroBar(label = "Carbs", value = "120g", total = "200g", progress = 0.6f, color = Amber400)
                }
            }
        }
    }
}

@Composable
fun MacroBar(label: String, value: String, total: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate900)
            Text("$value / $total", fontSize = 12.sp, color = Slate500)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Slate200
        )
    }
}

@Composable
fun WorkoutCard(task: DailyTask, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        color = Slate800,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!task.isCompleted) },
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = White40.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task.description,
                    color = Slate400,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                shape = CircleShape,
                color = Emerald500,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (task.isCompleted) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(task: DailyTask, onCheckedChange: (Boolean) -> Unit, onCardClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        border = borderStroke(Slate100),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Orange50,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = Orange500, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                val parts = task.title.split(":", limit = 2)
                val type = parts.firstOrNull()?.trim()?.uppercase() ?: "MEAL"
                val name = parts.lastOrNull()?.trim() ?: task.title
                
                Text(
                    text = "$type • AI REC",
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Text(
                    text = name,
                    color = Slate800,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (task.calories > 0) {
                        Surface(color = Slate100, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "${task.calories} kcal",
                                color = Slate500,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (task.isCompleted) {
                         Surface(color = Emerald200.copy(alpha=0.5f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "Logged",
                                color = Emerald500,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCheckedChange(!task.isCompleted) }
                    .border(2.dp, Slate200, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Logged", tint = Emerald500, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun WaterTrackerCard() {
    Surface(
        color = Blue200.copy(alpha = 0.2f),
        border = borderStroke(Blue200.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = Blue500)
                Text("6 / 10 glasses", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate900)
            }
            Surface(
                color = Color.White,
                border = borderStroke(Blue200),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.Add, 
                    contentDescription = "Add Water", 
                    tint = Blue500,
                    modifier = Modifier.padding(8.dp).size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { }
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = label,
            tint = if (selected) Emerald500 else Slate400
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if(selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Emerald500 else Slate400
        )
    }
}
