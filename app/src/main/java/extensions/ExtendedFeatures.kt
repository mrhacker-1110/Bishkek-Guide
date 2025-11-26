package com.example.bishkekguide.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Расширенная версия экрана контактов с возможностью звонка
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedContactsScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val contacts = remember {
        listOf(
            EnhancedContact("Скорая помощь", "103", Icons.Default.LocalHospital, "Экстренная"),
            EnhancedContact("Пожарная служба", "101", Icons.Default.LocalFireDepartment, "Экстренная"),
            EnhancedContact("Полиция", "102", Icons.Default.Shield, "Экстренная"),
            EnhancedContact("Такси Namba", "+996312510510", Icons.Default.LocalTaxi, "Такси"),
            EnhancedContact("Яндекс Такси", "+996555555555", Icons.Default.DirectionsCar, "Такси"),
            EnhancedContact("Справочная", "109", Icons.Default.Phone, "Информация")
        )
    }

    val filteredContacts = contacts.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Поисковая строка
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Поиск контактов..."
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Группировка по категориям
            val groupedContacts = filteredContacts.groupBy { it.category }

            groupedContacts.forEach { (category, categoryContacts) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(categoryContacts, key = { it.name }) { contact ->
                    ClickableContactCard(
                        contact = contact,
                        onClick = { makePhoneCall(context, contact.number) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Очистить")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun ClickableContactCard(
    contact: EnhancedContact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    contact.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    contact.number,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                Icons.Default.Phone,
                contentDescription = "Позвонить",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}

/**
 * Функция для совершения звонка
 */
fun makePhoneCall(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Расширенная версия экрана мест с детальной информацией
 */
@Composable
fun EnhancedPlacesScreen() {
    var selectedPlace by remember { mutableStateOf<EnhancedPlace?>(null) }

    val places = remember {
        listOf(
            EnhancedPlace(
                name = "Ала-Тоо площадь",
                description = "Центральная площадь Бишкека",
                emoji = "🏛️",
                details = "Главная площадь столицы с памятником Свободы, построена в 1984 году. Здесь проходят все главные празднования и мероприятия.",
                rating = 4.5f,
                openHours = "Круглосуточно",
                address = "пр. Чуй, Бишкек"
            ),
            EnhancedPlace(
                name = "Дубовый парк",
                description = "Старейший парк города",
                emoji = "🌳",
                details = "Основан в 1890 году. Здесь растут дубы возрастом более 130 лет. Популярное место отдыха жителей города.",
                rating = 4.7f,
                openHours = "06:00 - 23:00",
                address = "ул. Эркиндик, Бишкек"
            ),
            EnhancedPlace(
                name = "Ошский базар",
                description = "Крупнейший рынок Кыргызстана",
                emoji = "🛒",
                details = "Работает с 1980-х годов. Один из самых больших рынков Центральной Азии. Здесь можно найти всё - от продуктов до электроники.",
                rating = 4.3f,
                openHours = "08:00 - 19:00",
                address = "ул. Беловодская, Бишкек"
            ),
            EnhancedPlace(
                name = "Филармония",
                description = "Главный концертный зал",
                emoji = "🎭",
                details = "Кыргызская национальная филармония имени Токтогула Сатылганова. Проводятся концерты классической и народной музыки.",
                rating = 4.6f,
                openHours = "10:00 - 20:00",
                address = "пр. Чуй, 253, Бишкек"
            ),
            EnhancedPlace(
                name = "Музей изобразительных искусств",
                description = "Национальная галерея",
                emoji = "🖼️",
                details = "Более 18,000 экспонатов кыргызского и мирового искусства. Основан в 1935 году.",
                rating = 4.4f,
                openHours = "09:00 - 18:00, выходной понедельник",
                address = "ул. Юсупа Абдрахманова, 196, Бишкек"
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Популярные места",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(places) { place ->
                EnhancedPlaceCard(
                    place = place,
                    onClick = { selectedPlace = place }
                )
            }
        }

        // Модальное окно с деталями
        selectedPlace?.let { place ->
            PlaceDetailDialog(
                place = place,
                onDismiss = { selectedPlace = null }
            )
        }
    }
}

@Composable
fun EnhancedPlaceCard(
    place: EnhancedPlace,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    place.emoji,
                    fontSize = 48.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        place.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        place.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Рейтинг
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        place.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Часы работы
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        place.openHours.split(",").first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceDetailDialog(
    place: EnhancedPlace,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(place.emoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(place.name, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    place.details,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DetailRow(Icons.Default.Star, "Рейтинг", place.rating.toString())
                DetailRow(Icons.Default.Schedule, "Часы работы", place.openHours)
                DetailRow(Icons.Default.LocationOn, "Адрес", place.address)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Data classes
data class EnhancedContact(
    val name: String,
    val number: String,
    val icon: ImageVector,
    val category: String
)

data class EnhancedPlace(
    val name: String,
    val description: String,
    val emoji: String,
    val details: String,
    val rating: Float,
    val openHours: String,
    val address: String
)