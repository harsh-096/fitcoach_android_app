package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.FitViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: FitViewModel,
    onComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    
    val goalOptions = listOf(
        "Lose weight", "Gain weight", "Lose weight + Build muscle",
        "Gain weight + Build muscle", "Maintain current body and diet"
    )
    var goal by remember { mutableStateOf(goalOptions[0]) }
    
    var lifestyle by remember { mutableStateOf("") }
    
    val livingOptions = listOf("Living alone", "Shared home", "Other (Custom)")
    var livingSituation by remember { mutableStateOf(livingOptions[0]) }
    var customLivingSituation by remember { mutableStateOf("") }
    
    val dietOptions = listOf("Vegetarian", "Non Vegetarian", "Vegan", "Egg + Veg")
    var diet by remember { mutableStateOf(dietOptions[0]) }
    
    var restrictions by remember { mutableStateOf("") }
    var timeAvailable by remember { mutableStateOf("") }
    var flexibility by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
    ) {
        // Dynamic Background Accents
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = 64.dp, y = (-64).dp).size(250.dp).background(Emerald200.copy(alpha = 0.4f), shape = CircleShape).blur(80.dp))
            Box(modifier = Modifier.align(Alignment.BottomStart).offset(x = (-64).dp, y = (-100).dp).size(300.dp).background(Blue200.copy(alpha = 0.3f), shape = CircleShape).blur(100.dp))
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column {
                        Text(
                            text = "WELCOME TO FITCOACH",
                            color = Slate500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Let's personalize your plan",
                            color = Slate800,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = White60,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, White40),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Basic Details", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
                        
                        StyledTextField(value = name, onValueChange = { name = it }, label = "Name")
                        StyledTextField(value = age, onValueChange = { age = it }, label = "Age")
                        StyledTextField(value = height, onValueChange = { height = it }, label = "Height (e.g., 175 cm)")
                        StyledTextField(value = weight, onValueChange = { weight = it }, label = "Weight (e.g., 70 kg)")
                    }
                }
                
                Surface(
                    color = White60,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, White40),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Fitness & Lifestyle", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
                        
                        StyledDropdown(label = "Primary Goal", selectedValue = goal, options = goalOptions, onValueSelected = { goal = it })
                        
                        StyledTextField(value = lifestyle, onValueChange = { lifestyle = it }, label = "Workout Environment (e.g., Gym / Home)")
                        
                        StyledDropdown(label = "Living Situation", selectedValue = livingSituation, options = livingOptions, onValueSelected = { livingSituation = it })
                        AnimatedVisibility(visible = livingSituation == "Other (Custom)") {
                            StyledTextField(value = customLivingSituation, onValueChange = { customLivingSituation = it }, label = "Describe Living Situation")
                        }
                    }
                }
                
                Surface(
                    color = White60,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, White40),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Nutrition", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
                        
                        StyledDropdown(label = "Diet Preference", selectedValue = diet, options = dietOptions, onValueSelected = { diet = it })
                        
                        StyledTextField(value = restrictions, onValueChange = { restrictions = it }, label = "Food Allergies/Dislikes (e.g., Peanuts, Mushrooms)")
                    }
                }

                Surface(
                    color = White60,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, White40),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Schedule & Time", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
                        
                        StyledTextField(value = timeAvailable, onValueChange = { timeAvailable = it }, label = "Time Available for Transformation (e.g., 3 months)")
                        StyledTextField(value = flexibility, onValueChange = { flexibility = it }, label = "Daily Schedule Flexibility (e.g., Strict 9-5)")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val finalLivingSituation = if (livingSituation == "Other (Custom)") customLivingSituation else livingSituation
                        val profile = UserProfile(
                            name = name,
                            age = age.toIntOrNull() ?: 25,
                            height = height,
                            weight = weight,
                            goal = goal,
                            lifestyle = lifestyle,
                            livingSituation = finalLivingSituation,
                            diet = diet,
                            foodRestrictions = restrictions,
                            timeAvailable = timeAvailable,
                            scheduleFlexibility = flexibility
                        )
                        viewModel.saveProfile(profile, onSuccess = onComplete)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald500,
                        disabledContainerColor = Slate200
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = name.isNotBlank() && age.isNotBlank() && height.isNotBlank() && weight.isNotBlank()
                ) {
                    Text("GENERATE PLAN", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Slate500) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Emerald500,
            unfocusedBorderColor = Slate200,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Slate900,
            unfocusedTextColor = Slate800
        ),
        singleLine = true
    )
}

@Composable
fun StyledDropdown(label: String, selectedValue: String, options: List<String>, onValueSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Slate500, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        Surface(
            color = Color.White.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedValue, color = Slate900, fontSize = 16.sp)
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Slate500
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(0.85f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Slate800) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
