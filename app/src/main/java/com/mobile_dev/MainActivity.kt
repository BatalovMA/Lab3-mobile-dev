package com.mobile_dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

// Dark theme colors matching globals.css
private val DarkBackground = Color(0xFF212121)
private val LightForeground = Color(0xFFFFFFFF)
private val PrimaryColor = Color(0xFF90CAF9)
private val SecondaryColor = Color(0xFFCE93D8)
private val ErrorColor = Color(0xFFEF5350)
private val BorderColor = Color(0xFFFFFFFF)

private val AppColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.Black,
    primaryContainer = PrimaryColor.copy(alpha = 0.3f),
    onPrimaryContainer = LightForeground,
    secondary = SecondaryColor,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryColor.copy(alpha = 0.3f),
    onSecondaryContainer = LightForeground,
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF80CBC4).copy(alpha = 0.3f),
    onTertiaryContainer = LightForeground,
    error = ErrorColor,
    errorContainer = ErrorColor.copy(alpha = 0.3f),
    onErrorContainer = LightForeground,
    background = DarkBackground,
    onBackground = LightForeground,
    surface = DarkBackground,
    onSurface = LightForeground,
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = LightForeground,
    outline = BorderColor
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SolarProfitCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun SolarProfitCalculatorScreen() {
    var averagePowerVal by remember { mutableStateOf("5.0") }
    var sigma1Val by remember { mutableStateOf("1.0") }
    var sigma2Val by remember { mutableStateOf("0.25") }
    var costVal by remember { mutableStateOf("7.0") }

    val averagePower = averagePowerVal.toDoubleOrNull() ?: 5.0
    val sigma1 = sigma1Val.toDoubleOrNull() ?: 1.0
    val sigma2 = sigma2Val.toDoubleOrNull() ?: 0.25
    val cost = costVal.toDoubleOrNull() ?: 7.0

    // Power range for integration
    val powerRangeBot = 4.75
    val powerRangeTop = 5.25

    // Calculate results
    val results = remember(averagePower, sigma1, sigma2, cost) {
        calculateProfits(averagePower, sigma1, sigma2, cost, powerRangeBot, powerRangeTop)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Веб калькулятор розрахунку прибутку від сонячних електростанцій з встановленою системою прогнозування сонячної потужності",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Умова:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                InputFieldWithUnit(
                    label = "Pс (Середня потужність СЕС)",
                    value = averagePowerVal,
                    onValueChange = { averagePowerVal = it },
                    unit = "МВт"
                )

                InputFieldWithUnit(
                    label = "σ₁ (СКВ до вдосконалення)",
                    value = sigma1Val,
                    onValueChange = { sigma1Val = it },
                    unit = "МВт"
                )

                InputFieldWithUnit(
                    label = "σ₂ (СКВ після вдосконалення)",
                    value = sigma2Val,
                    onValueChange = { sigma2Val = it },
                    unit = "МВт"
                )

                InputFieldWithUnit(
                    label = "В (Тариф «зеленого» аукціону)",
                    value = costVal,
                    onValueChange = { costVal = it },
                    unit = "грн/кВт·год"
                )
            }
        }

        // Description Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Пояснення:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Розрахунок базується на системі \"зеленого\" аукціону, де:",
                    fontSize = 14.sp
                )
                Text("• σ₁ - середньоквадратичне відхилення ДО вдосконалення системи прогнозування", fontSize = 12.sp)
                Text("• σ₂ - середньоквадратичне відхилення ПІСЛЯ вдосконалення системи прогнозування", fontSize = 12.sp)
                Text("• Менше σ означає кращий прогноз і менші штрафи", fontSize = 12.sp)
                Text("• Прибуток = Виручка за продану енергію - Штрафи за неточний прогноз", fontSize = 12.sp)
            }
        }

        // Results Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Відповідь:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Result for sigma1
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Прибуток до вдосконалення (σ₁ = $sigma1 МВт):",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${String.format("%.2f", results.profit1)} тис. грн",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Result for sigma2
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Прибуток після вдосконалення (σ₂ = $sigma2 МВт):",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${String.format("%.2f", results.profit2)} тис. грн",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Divider()

                // Improvement
                val improvement = results.profit2 - results.profit1
                val improvementPercent = if (results.profit1 != 0.0) {
                    (improvement / abs(results.profit1)) * 100
                } else 0.0

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Покращення прибутку:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "+${String.format("%.2f", improvement)} тис. грн",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "(${String.format("%.1f", improvementPercent)}% покращення)",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // Detailed breakdown
                Divider()

                Text(
                    text = "Детальний розрахунок:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                DetailedResults(
                    title = "До вдосконалення (σ₁)",
                    revenue = results.revenue1,
                    fine = results.fine1,
                    profit = results.profit1
                )

                DetailedResults(
                    title = "Після вдосконалення (σ₂)",
                    revenue = results.revenue2,
                    fine = results.fine2,
                    profit = results.profit2
                )
            }
        }
    }
}

@Composable
fun InputFieldWithUnit(label: String, value: String, onValueChange: (String) -> Unit, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(2f),
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Text(
            text = unit,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun DetailedResults(title: String, revenue: Double, fine: Double, profit: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Виручка:", fontSize = 12.sp)
                Text("${String.format("%.2f", revenue)} тис. грн", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Штрафи:", fontSize = 12.sp)
                Text("${String.format("%.2f", fine)} тис. грн", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Прибуток:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${String.format("%.2f", profit)} тис. грн",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

data class ProfitResults(
    val revenue1: Double,
    val fine1: Double,
    val profit1: Double,
    val revenue2: Double,
    val fine2: Double,
    val profit2: Double
)

// Calculation functions
fun calculateProfits(
    averagePower: Double,
    sigma1: Double,
    sigma2: Double,
    cost: Double,
    powerRangeBot: Double,
    powerRangeTop: Double
): ProfitResults {
    // Calculate for sigma1
    val deltaW1 = integrate(
        { p -> calcDeltaW(p, averagePower, sigma1) },
        powerRangeBot,
        powerRangeTop,
        1000
    )
    val w1 = calcWPos(averagePower, deltaW1)
    val p1 = calcP(w1, cost)
    val w2 = calcWNeg(averagePower, deltaW1)
    val f1 = calcF(w2, cost)
    val totalProfit1 = calcTotalProfit(p1, f1)

    // Calculate for sigma2
    val deltaW2 = integrate(
        { p -> calcDeltaW(p, averagePower, sigma2) },
        powerRangeBot,
        powerRangeTop,
        1000
    )
    val w3 = calcWPos(averagePower, deltaW2)
    val p2 = calcP(w3, cost)
    val w4 = calcWNeg(averagePower, deltaW2)
    val f2 = calcF(w4, cost)
    val totalProfit2 = calcTotalProfit(p2, f2)

    return ProfitResults(
        revenue1 = p1,
        fine1 = f1,
        profit1 = totalProfit1,
        revenue2 = p2,
        fine2 = f2,
        profit2 = totalProfit2
    )
}

fun integrate(func: (Double) -> Double, start: Double, end: Double, numPoints: Int): Double {
    val step = (end - start) / numPoints
    var sum = 0.5 * (func(start) + func(end))

    var i = start
    while (i < end) {
        sum += func(i)
        i += step
    }

    return sum * step
}

fun calcDeltaW(p: Double, averagePower: Double, sigma: Double): Double {
    return (1.0 / (sigma * sqrt(2.0 * PI))) *
           exp(-((p - averagePower).pow(2)) / (2.0 * sigma.pow(2)))
}

fun calcWPos(averagePower: Double, deltaW: Double): Double {
    return averagePower * 24.0 * deltaW
}

fun calcP(w: Double, cost: Double): Double {
    return w * cost
}

fun calcWNeg(averagePower: Double, deltaW: Double): Double {
    return averagePower * 24.0 * (1.0 - deltaW)
}

fun calcF(w: Double, cost: Double): Double {
    return w * cost
}

fun calcTotalProfit(profit: Double, fine: Double): Double {
    return profit - fine
}
