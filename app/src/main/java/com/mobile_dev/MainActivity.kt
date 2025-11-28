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
import kotlin.math.pow

// Dark theme colors matching globals.css
private val DarkBackground = Color(0xFF212121)
private val LightForeground = Color(0xFFFFFFFF)
private val PrimaryColor = Color(0xFF90CAF9)
private val SecondaryColor = Color(0xFFCE93D8)
private val TertiaryColor = Color(0xFF80CBC4)
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
    tertiary = TertiaryColor,
    onTertiary = Color.Black,
    tertiaryContainer = TertiaryColor.copy(alpha = 0.3f),
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
                    ReliabilityCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun ReliabilityCalculatorScreen() {
    var selectedTask by remember { mutableStateOf(0) }

    // Task 2 states
    var zperAVal by remember { mutableStateOf("23.6") }
    var zperPVal by remember { mutableStateOf("17.6") }
    var omegaVal by remember { mutableStateOf("0.01") }
    var tvVal by remember { mutableStateOf("0.045") }
    var kpVal by remember { mutableStateOf("0.004") }
    var pmVal by remember { mutableStateOf("5120") }
    var tmVal by remember { mutableStateOf("6451") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Веб калькулятор для порівняння надійності одноколової та двоколової систем електропередачі та розрахунку збитків від перерв електропостачання у разі застосування однотрансформаторної ГТП",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Task Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Вибір завдання:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TaskSelectorButton(
                    text = "[1] Порівняння надійності систем електропередачі",
                    isSelected = selectedTask == 1,
                    onClick = { selectedTask = 1 }
                )

                TaskSelectorButton(
                    text = "[2] Розрахунок збитків від перерв електропостачання",
                    isSelected = selectedTask == 2,
                    onClick = { selectedTask = 2 }
                )
            }
        }

        // Input/Info Section
        when (selectedTask) {
            1 -> Task1Info()
            2 -> Task2Input(
                zperAVal = zperAVal,
                zperPVal = zperPVal,
                omegaVal = omegaVal,
                tvVal = tvVal,
                kpVal = kpVal,
                pmVal = pmVal,
                tmVal = tmVal,
                onZperAChange = { zperAVal = it },
                onZperPChange = { zperPVal = it },
                onOmegaChange = { omegaVal = it },
                onTvChange = { tvVal = it },
                onKpChange = { kpVal = it },
                onPmChange = { pmVal = it },
                onTmChange = { tmVal = it }
            )
        }

        // Results Section
        when (selectedTask) {
            1 -> Task1Results()
            2 -> Task2Results(
                zperA = zperAVal.toDoubleOrNull() ?: 0.0,
                zperP = zperPVal.toDoubleOrNull() ?: 0.0,
                omega = omegaVal.toDoubleOrNull() ?: 0.0,
                tv = tvVal.toDoubleOrNull() ?: 0.0,
                kp = kpVal.toDoubleOrNull() ?: 0.0,
                pm = pmVal.toDoubleOrNull() ?: 0.0,
                tm = tmVal.toDoubleOrNull() ?: 0.0
            )
        }
    }
}

@Composable
fun TaskSelectorButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(text, textAlign = TextAlign.Start)
    }
}

@Composable
fun Task1Info() {
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
            Text("Константи (всі значення задано):", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Частота відмов (ωᵢ), рік⁻¹:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("  • ПЛ-110 кВ: 0.007 (на 1 км)", fontSize = 12.sp)
            Text("  • Т-110 кВ: 0.015", fontSize = 12.sp)
            Text("  • В-110 кВ: 0.01", fontSize = 12.sp)
            Text("  • В-10 кВ (малооливний): 0.02", fontSize = 12.sp)
            Text("  • Збірні шини 10кВ: 0.03 (на 1 приєднання)", fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Час відновлення (tвi), год:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("  • ПЛ-110 кВ: 10 год", fontSize = 12.sp)
            Text("  • Т-110 кВ: 100 год", fontSize = 12.sp)
            Text("  • В-110 кВ: 30 год", fontSize = 12.sp)
            Text("  • В-10 кВ: 15 год", fontSize = 12.sp)
            Text("  • Збірні шини 10кВ: 2 год", fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Інші параметри:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("  • kmax: 43 год", fontSize = 12.sp)
            Text("  • Довжина ПЛ-110: 10 км", fontSize = 12.sp)
            Text("  • Кількість приєднань: 6", fontSize = 12.sp)
        }
    }
}

@Composable
fun Task2Input(
    zperAVal: String,
    zperPVal: String,
    omegaVal: String,
    tvVal: String,
    kpVal: String,
    pmVal: String,
    tmVal: String,
    onZperAChange: (String) -> Unit,
    onZperPChange: (String) -> Unit,
    onOmegaChange: (String) -> Unit,
    onTvChange: (String) -> Unit,
    onKpChange: (String) -> Unit,
    onPmChange: (String) -> Unit,
    onTmChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Вхідні дані:", fontWeight = FontWeight.Bold)

            InputFieldWithUnit(
                label = "Зпер.а (питомі збитки аварійні)",
                value = zperAVal,
                onValueChange = onZperAChange,
                unit = "грн/кВт·год"
            )

            InputFieldWithUnit(
                label = "Зпер.п (питомі збитки планові)",
                value = zperPVal,
                onValueChange = onZperPChange,
                unit = "грн/кВт·год"
            )

            InputFieldWithUnit(
                label = "ω (частота відмов)",
                value = omegaVal,
                onValueChange = onOmegaChange,
                unit = "рік⁻¹"
            )

            InputFieldWithUnit(
                label = "tв (час відновлення)",
                value = tvVal,
                onValueChange = onTvChange,
                unit = "року"
            )

            InputFieldWithUnit(
                label = "kп (коефіцієнт планових простоїв)",
                value = kpVal,
                onValueChange = onKpChange,
                unit = ""
            )

            InputFieldWithUnit(
                label = "Pм (максимальна потужність)",
                value = pmVal,
                onValueChange = onPmChange,
                unit = "кВт"
            )

            InputFieldWithUnit(
                label = "Tм (час використання)",
                value = tmVal,
                onValueChange = onTmChange,
                unit = "год"
            )
        }
    }
}

@Composable
fun Task1Results() {
    // Constants
    val pl110OmegaVal = 0.007
    val t110OmegaVal = 0.015
    val v110OmegaVal = 0.01
    val v10OmegaVal = 0.02
    val tiresOmegaVal = 0.03

    val pl110TviVal = 10.0
    val t110TviVal = 100.0
    val v110TviVal = 30.0
    val v10TviVal = 15.0
    val tiresTviVal = 2.0

    val plannedKMaxVal = 43.0

    val omegaSum = calcOmegaSum(pl110OmegaVal, tiresOmegaVal, t110OmegaVal, v110OmegaVal, v10OmegaVal)
    val tvos = calcTvos(
        pl110OmegaVal, tiresOmegaVal, t110OmegaVal, v110OmegaVal, v10OmegaVal,
        pl110TviVal, tiresTviVal, t110TviVal, v110TviVal, v10TviVal, omegaSum
    )
    val kaos = calcKaos(omegaSum, tvos)
    val kpos = calcKpos(plannedKMaxVal)
    val dkOmega = calcDKOmega(omegaSum, kaos, kpos)
    val dsOmega = calcDSOmega(dkOmega, v10OmegaVal)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результати [1]:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            ResultCard(
                "Частота відмов одноколової системи:",
                String.format("%.3f", omegaSum),
                "рік⁻¹",
                MaterialTheme.colorScheme.errorContainer
            )

            ResultCard(
                "Середня тривалість відновлення:",
                String.format("%.2f", tvos),
                "год",
                MaterialTheme.colorScheme.surfaceVariant
            )

            ResultCard(
                "Коефіцієнт аварійного простою одноколової системи:",
                String.format("%.6f", kaos),
                "",
                MaterialTheme.colorScheme.surfaceVariant
            )

            ResultCard(
                "Коефіцієнт планового простою одноколової системи:",
                String.format("%.6f", kpos),
                "",
                MaterialTheme.colorScheme.surfaceVariant
            )

            ResultCard(
                "Частота відмов одночасно двох кіл двоколової системи:",
                String.format("%.6f", dkOmega),
                "",
                MaterialTheme.colorScheme.tertiaryContainer
            )

            ResultCard(
                "Частота відмов двоколової системи з урахуванням секційного вимикача:",
                String.format("%.3f", dsOmega),
                "",
                MaterialTheme.colorScheme.primaryContainer
            )

            Divider()

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Висновок:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Отже, надійність двоколової системи електропередачі є значно вищою ніж одноколової.",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        "Частота відмов двоколової системи: ${String.format("%.3f", dsOmega)} << ${String.format("%.3f", omegaSum)} (одноколової)",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun Task2Results(zperA: Double, zperP: Double, omega: Double, tv: Double, kp: Double, pm: Double, tm: Double) {
    val wneda = calcWneda(omega, tv, pm, tm)
    val wnedp = calcWnedp(kp, pm, tm)
    val zper = calcZper(zperA, wneda, zperP, wnedp)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результати [2]:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            ResultCard(
                "Математичне сподівання аварійного недовідпущення електроенергії:",
                String.format("%.2f", wneda),
                "кВт·год",
                MaterialTheme.colorScheme.errorContainer
            )

            ResultCard(
                "Математичне сподівання планового недовідпущення електроенергії:",
                String.format("%.2f", wnedp),
                "кВт·год",
                MaterialTheme.colorScheme.surfaceVariant
            )

            ResultCard(
                "Математичне сподівання збитків від переривання електропостачання:",
                String.format("%.2f", zper),
                "грн",
                MaterialTheme.colorScheme.primaryContainer
            )
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
        Text(text = label, modifier = Modifier.weight(2f), fontSize = 13.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1.2f),
            singleLine = true
        )
        Text(text = unit, modifier = Modifier.weight(1f).padding(start = 4.dp), fontSize = 12.sp)
    }
}

@Composable
fun ResultCard(label: String, value: String, unit: String, backgroundColor: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = label, fontSize = 13.sp)
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = unit,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

// Task 1 calculations
fun calcOmegaSum(pl110: Double, tires: Double, t110: Double, v110: Double, v10: Double): Double {
    return pl110 * 10.0 + t110 + v110 + v10 + 6.0 * tires
}

fun calcTvos(
    pl110: Double, tires: Double, t110: Double, v110: Double, v10: Double,
    pl110Tvi: Double, tiresTvi: Double, t110Tvi: Double, v110Tvi: Double, v10Tvi: Double,
    omegaSum: Double
): Double {
    return (pl110 * 10.0 * pl110Tvi + t110 * t110Tvi + v110 * v110Tvi + v10 * v10Tvi + tires * 6.0 * tiresTvi) / omegaSum
}

fun calcKaos(omegaSum: Double, tvos: Double): Double {
    return (omegaSum * tvos) / 8760.0
}

fun calcKpos(plannedKMax: Double): Double {
    return 1.2 * (plannedKMax / 8760.0)
}

fun calcDKOmega(omegaSum: Double, kaos: Double, kpos: Double): Double {
    return 2.0 * omegaSum * (kaos + kpos)
}

fun calcDSOmega(dkOmega: Double, v10Omega: Double): Double {
    return dkOmega + v10Omega
}

// Task 2 calculations
fun calcWneda(omega: Double, tv: Double, pm: Double, tm: Double): Double {
    return omega * tv * pm * tm
}

fun calcWnedp(kp: Double, pm: Double, tm: Double): Double {
    return kp * pm * tm
}

fun calcZper(zperA: Double, wneda: Double, zperP: Double, wnedp: Double): Double {
    return zperA * wneda + zperP * wnedp
}
