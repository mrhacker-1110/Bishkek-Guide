package com.example.bishkekguide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bishkekguide.ui.theme.BishkekGuideTheme
import java.text.DecimalFormat

// ===============================================================================================
// 1. МЕНЕДЖЕР ЯЗЫКОВ И ПЕРЕВОД
// ===============================================================================================

sealed class Language(val code: String, val displayName: String) {
    object Russian : Language("ru", "Русский")
    object English : Language("en", "English")
}

val LocalLanguage = compositionLocalOf<MutableState<Language>> {
    error("Language not provided")
}

// Статические курсы валют (для DEMO)
object CurrencyRates {
    const val USD_TO_KGS = 89.0 // Покупка (для туриста)
    const val EUR_TO_KGS = 97.0
    const val RUB_TO_KGS = 0.95
}

fun getTranslation(key: String, lang: Language): String {
    return when (lang) {
        is Language.Russian -> when (key) {
            "BishkekGuide" -> "Гид по Бишкеку"
            "Home" -> "Главная"
            "Places" -> "Места"
            "Food" -> "Еда"
            "Services" -> "Услуги"
            "Contacts" -> "Контакты"
            "About" -> "Инфо"
            "Welcome" -> "Добро пожаловать в Бишкек! 🇰🇬"
            "Slogan" -> "Ваш карманный гид по столице Кыргызстана"
            "MainSections" -> "Основные разделы"
            "AttractionsTitle" -> "Достопримечательности"
            "AttractionsDesc" -> "Откройте для себя лучшие места города"
            "CuisineTitle" -> "Кухня"
            "CuisineDesc" -> "Попробуйте лучшие блюда Кыргызстана"
            "ServicesTitle" -> "Услуги для туриста"
            "ServicesDesc" -> "Жилье, обмен валют, транспорт"
            "ContactsTitle" -> "Полезные контакты"
            "ContactsDesc" -> "Экстренные службы всегда под рукой"
            "PopularPlaces" -> "Популярные места"
            "WhereToEat" -> "Где поесть"
            "Call" -> "Позвонить"
            "Close" -> "Закрыть"
            "OnMap" -> "На карте"
            "UsefulContacts" -> "Полезные контакты"
            "AppInfo" -> "Приложение создано для помощи туристам и гостям Бишкека. Наша цель — сделать ваше пребывание в Кыргызстане комфортным и информативным."
            "Version" -> "Версия 1.1"
            "MadeWithLove" -> "Сделано с любовью к Кыргызстану"
            "ShareApp" -> "Поделиться приложением"
            "Accommodation" -> "Жилье (Отели и Хостелы)"
            "CurrencyExchange" -> "Конвертер Валют (USD, EUR, RUB в KGS)"
            "ExchangeInfo" -> "Курс для справки. Обмен рекомендуем в официальных банках."
            "EnterAmount" -> "Введите сумму" // Упрощено для поля ввода
            "ConvertedAmount" -> "Сумма в Сомах (KGS)"
            "BishkekInfo" -> "Информация о Бишкеке"
            "GoToSite" -> "На сайт"
            "Transport" -> "Транспорт"
            "USD" -> "USD (Доллар)"
            "EUR" -> "EUR (Евро)"
            "RUB" -> "RUB (Рубль)"
            "WeatherTitle" -> "Погода в Бишкеке" // Новый перевод
            "FoodTitle" -> "Популярные блюда" // Новый перевод
            "Today" -> "Сегодня" // Новый перевод
            "CuisineNational" -> "Национальная кухня"
            "CuisineEuropean" -> "Европейская кухня"
            "CuisineUzbek" -> "Узбекская кухня"
            "CuisineAsian" -> "Азиатский фаст-фуд"
            "CuisineCoffee" -> "Кофейня"
            else -> key
        }
        is Language.English -> when (key) {
            "BishkekGuide" -> "Bishkek Guide"
            "Home" -> "Home"
            "Places" -> "Places"
            "Food" -> "Food"
            "Services" -> "Services"
            "Contacts" -> "Contacts"
            "About" -> "Info"
            "Welcome" -> "Welcome to Bishkek! 🇰🇬"
            "Slogan" -> "Your pocket guide to the capital of Kyrgyzstan"
            "MainSections" -> "Main Sections"
            "AttractionsTitle" -> "Attractions"
            "AttractionsDesc" -> "Discover the best places in the city"
            "CuisineTitle" -> "Cuisine"
            "CuisineDesc" -> "Try the best dishes of Kyrgyzstan"
            "ServicesTitle" -> "Tourist Services"
            "ServicesDesc" -> "Accommodation, currency exchange, transport"
            "ContactsTitle" -> "Useful Contacts"
            "ContactsDesc" -> "Emergency services always at hand"
            "PopularPlaces" -> "Popular Places"
            "WhereToEat" -> "Where to Eat"
            "Call" -> "Call"
            "Close" -> "Close"
            "OnMap" -> "On Map"
            "UsefulContacts" -> "Useful Contacts"
            "AppInfo" -> "The application is designed to help tourists and guests of Bishkek navigate the city. Our goal is to make your stay in Kyrgyzstan comfortable and informative."
            "Version" -> "Version 1.1"
            "MadeWithLove" -> "Made with love for Kyrgyzstan"
            "ShareApp" -> "Share App"
            "Accommodation" -> "Accommodation (Hotels and Hostels)"
            "CurrencyExchange" -> "Currency Converter (USD, EUR, RUB to KGS)"
            "ExchangeInfo" -> "Rates are for reference. We recommend exchanging currency at official banks."
            "EnterAmount" -> "Enter amount"
            "ConvertedAmount" -> "Amount in Som (KGS)"
            "BishkekInfo" -> "Information about Bishkek"
            "GoToSite" -> "Go to Site"
            "Transport" -> "Transport"
            "USD" -> "USD (Dollar)"
            "EUR" -> "EUR (Euro)"
            "RUB" -> "RUB (Ruble)"
            "WeatherTitle" -> "Weather in Bishkek"
            "FoodTitle" -> "Popular dishes"
            "Today" -> "Today"
            "CuisineNational" -> "National Cuisine"
            "CuisineEuropean" -> "European Cuisine"
            "CuisineUzbek" -> "Uzbek Cuisine"
            "CuisineAsian" -> "Asian Fast Food"
            "CuisineCoffee" -> "Coffee Shop"
            else -> key
        }
    }
}

// ===============================================================================================
// 2. MainActivity и BishkekGuideApp (Навигация)
// ===============================================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BishkekGuideTheme {
                BishkekGuideApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BishkekGuideApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    // Инициализация стейта для языка (по умолчанию Русский)
    val currentLanguage = remember { mutableStateOf<Language>(Language.Russian) }
    val lang = currentLanguage.value

    CompositionLocalProvider(LocalLanguage provides currentLanguage) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            getTranslation("BishkekGuide", lang),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val items = listOf(
                        NavigationItem(getTranslation("Home", lang), Icons.Default.Home, Screen.Home),
                        NavigationItem(getTranslation("Places", lang), Icons.Default.Place, Screen.Places),
                        NavigationItem(getTranslation("Food", lang), Icons.Default.Restaurant, Screen.Food),
                        NavigationItem(getTranslation("Services", lang), Icons.Default.BusinessCenter, Screen.Services),
                        NavigationItem(getTranslation("Contacts", lang), Icons.Default.Phone, Screen.Contacts),
                        NavigationItem(getTranslation("About", lang), Icons.Default.Info, Screen.About)
                    )

                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp) },
                            selected = currentScreen == item.screen,
                            onClick = { currentScreen = item.screen }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn() + slideInHorizontally() with fadeOut() + slideOutHorizontally()
                    }
                ) { screen ->
                    when (screen) {
                        Screen.Home -> HomeScreen()
                        Screen.Places -> PlacesScreen()
                        Screen.Contacts -> ContactsScreen()
                        Screen.Food -> FoodScreen()
                        Screen.About -> AboutScreen()
                        Screen.Services -> ServicesScreen()
                    }
                }
            }
        }
    }
}

// ===============================================================================================
// 3. ЭКРАН ГЛАВНАЯ (Home) - СЕЛЕКТОР ЯЗЫКА И БЛОК ПОГОДЫ
// ===============================================================================================

// Фиктивные данные для демонстрации интеграции с сервером
data class WeatherData(val temp: String, val condition: String, val icon: ImageVector)
val weatherDataPlaceholder = WeatherData("15°C", "Солнечно", Icons.Default.WbSunny)

@Composable
fun HomeScreen() {
    val currentLanguage = LocalLanguage.current
    val lang = currentLanguage.value
    // Фиктивные данные о погоде
    val weatherData = remember { mutableStateOf(weatherDataPlaceholder) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentPadding = PaddingValues(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. ЯЗЫКОВОЙ СЕЛЕКТОР ---
        item {
            LanguageSelector(currentLanguage)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- 2. БЛОК ПОГОДЫ И ПРИВЕТСТВИЕ (ИСПРАВЛЕННЫЙ БЛОК) ---
        item {
            // Реальная погода будет получаться с сервера здесь
            WeatherCard(weatherData.value, lang)
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- 3. БЛОК ПРИВЕТСТВИЯ ---
        item {
            Icon(
                Icons.Default.LocationCity,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                getTranslation("Welcome", lang),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                getTranslation("Slogan", lang),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- 4. БЛОК РАЗДЕЛОВ ---
        item {
            Text(
                getTranslation("MainSections", lang),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard(
                    icon = Icons.Default.Place,
                    title = getTranslation("AttractionsTitle", lang),
                    description = getTranslation("AttractionsDesc", lang)
                )

                InfoCard(
                    icon = Icons.Default.Restaurant,
                    title = getTranslation("CuisineTitle", lang),
                    description = getTranslation("CuisineDesc", lang)
                )

                InfoCard(
                    icon = Icons.Default.BusinessCenter,
                    title = getTranslation("ServicesTitle", lang),
                    description = getTranslation("ServicesDesc", lang)
                )

                InfoCard(
                    icon = Icons.Default.Phone,
                    title = getTranslation("ContactsTitle", lang),
                    description = getTranslation("ContactsDesc", lang)
                )
            }
        }
    }
}

// --- Компонент Погоды ---
@Composable
fun WeatherCard(weather: WeatherData, lang: Language) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(getTranslation("WeatherTitle", lang), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(getTranslation("Today", lang), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.width(16.dp))

            Icon(
                weather.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )

            Spacer(Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    weather.temp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    weather.condition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    // Здесь должна быть логика получения данных с API, например, с помощью Coroutines и Retrofit.
    // Пока что показываем статичные данные.
}

@Composable
fun InfoCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Компонент Выбора Языка ---
@Composable
fun LanguageSelector(currentLanguage: MutableState<Language>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        LanguageButton(
            language = Language.Russian,
            currentLanguage = currentLanguage.value,
            onClick = { currentLanguage.value = Language.Russian }
        )
        Spacer(Modifier.width(8.dp))
        LanguageButton(
            language = Language.English,
            currentLanguage = currentLanguage.value,
            onClick = { currentLanguage.value = Language.English }
        )
    }
}

@Composable
fun LanguageButton(language: Language, currentLanguage: Language, onClick: () -> Unit) {
    val isSelected = language == currentLanguage
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 4.dp else 0.dp),
    ) {
        Text(language.displayName)
    }
}


// ===============================================================================================
// 4. ЭКРАН МЕСТА (Places)
// ===============================================================================================

@Composable
fun PlacesScreen() {
    val context = LocalContext.current
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    val lang = LocalLanguage.current.value

    val places = listOf(
        Place("Ала-Тоо площадь", "Центральная и самая большая площадь Бишкека, сердце политической и культурной жизни. Здесь установлен памятник Манасу Великодушному и проходит смена почетного караула.", "🏛️",
            "пр. Чуй, Бишкек", 42.8746, 74.6098),
        Place("Дубовый парк (Парк им. Чингиза Айтматова)", "Старейший парк города, заложенный в 1890 году. Известен своей аллеей скульптур под открытым небом и старыми, величественными дубами. Идеальное место для тихих прогулок.", "🌳",
            "ул. Эркиндик, Бишкек", 42.8708, 74.6044),
        Place("Ошский базар", "Один из крупнейших и наиболее аутентичных рынков Центральной Азии. Здесь можно купить специи, сухофрукты, национальную одежду, кумыс, а также почувствовать настоящий восточный колорит.", "🛒",
            "ул. Беловодская, Бишкек", 42.8532, 74.6282),
        Place("Кыргызская национальная филармония", "Главный концертный зал страны, названный в честь Токтогула Сатылганова. Имеет прекрасную акустику и является архитектурным шедевром советского модернизма.", "🎭",
            "пр. Чуй, 253, Бишкек", 42.8766, 74.5991),
        Place("Музей изобразительных искусств", "Национальная галерея, хранящая богатую коллекцию произведений кыргызских, русских и зарубежных художников. Регулярно проводятся тематические выставки.", "🖼️",
            "ул. Юсупа Абдрахманова, 196", 42.8742, 74.6056),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    getTranslation("PopularPlaces", lang),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(places) { place ->
                PlaceCard(
                    place = place,
                    onClick = { selectedPlace = place }
                )
            }
        }

        selectedPlace?.let { place ->
            AlertDialog(
                onDismissRequest = { selectedPlace = null },
                icon = { Text(place.emoji, fontSize = 48.sp) },
                title = { Text(place.name, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(place.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(place.address, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val uri = "geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${place.name})"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                    }) {
                        Icon(Icons.Default.Map, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getTranslation("OnMap", lang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedPlace = null }) { Text(getTranslation("Close", lang)) }
                }
            )
        }
    }
}

@Composable
fun PlaceCard(place: Place, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(place.emoji, fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(place.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Подробнее", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// ===============================================================================================
// 5. ЭКРАН ЕДА (Food) - ДОБАВЛЕН СПИСОК БЛЮД
// ===============================================================================================

@Composable
fun FoodScreen() {
    val context = LocalContext.current
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    val lang = LocalLanguage.current.value

    val restaurants = listOf(
        Restaurant(
            "Фаиза", getTranslation("CuisineNational", lang), "🍖", "ул. Исанова 105", "+996312543210",
            listOf("Бешбармак", "Лагман", "Плов", "Манты", "Куурдак")
        ),
        Restaurant(
            "Navigator", getTranslation("CuisineEuropean", lang), "🍝", "пр. Чуй 219", "+996312654321",
            listOf("Стейки", "Паста Карбонара", "Цезарь Салат", "Бургеры")
        ),
        Restaurant(
            "Arzu", getTranslation("CuisineUzbek", lang), "🫓", "ул. Ибраимова 115", "+996312765432",
            listOf("Плов с бараниной", "Самса", "Шашлык", "Шурпа", "Чучвара")
        ),
        Restaurant(
            "Chicken Star", getTranslation("CuisineAsian", lang), "🍗", "Несколько филиалов", "+996555123456",
            listOf("Острая курица (Яннём)", "Кимчи", "Токпокки", "Рамён")
        ),
        Restaurant(
            "Sierra Coffee", getTranslation("CuisineCoffee", lang), "☕", "пр. Манаса 40", "+996312876543",
            listOf("Капучино", "Латте", "Сэндвичи", "Чизкейк")
        ),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(getTranslation("WhereToEat", lang), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(restaurants) { restaurant ->
                RestaurantCard(restaurant = restaurant, onClick = { selectedRestaurant = restaurant })
            }
        }

        selectedRestaurant?.let { restaurant ->
            AlertDialog(
                onDismissRequest = { selectedRestaurant = null },
                icon = { Text(restaurant.emoji, fontSize = 48.sp) },
                title = { Text(restaurant.name, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        // Тип кухни
                        Text(restaurant.cuisine, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))

                        // Популярные блюда
                        Text(getTranslation("FoodTitle", lang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        restaurant.dishes.forEach { dish ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RamenDining, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(dish, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Контакты
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(restaurant.address, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(restaurant.phone, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${restaurant.phone}") }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Phone, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getTranslation("Call", lang))
                    }
                },
                dismissButton = { TextButton(onClick = { selectedRestaurant = null }) { Text(getTranslation("Close", lang)) } }
            )
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(restaurant.emoji, fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(restaurant.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(restaurant.cuisine, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Подробнее", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// ===============================================================================================
// 6. ЭКРАН УСЛУГИ (Services) - ИСПРАВЛЕННОЕ ПОЛЕ ВВОДА В КОНВЕРТЕРЕ
// ===============================================================================================

@Composable
fun ServicesScreen() {
    val context = LocalContext.current
    val lang = LocalLanguage.current.value

    val accommodationList = listOf(
        Service("Hyatt Regency Bishkek", "Роскошный отель в центре города, международный стандарт, бассейн и спа.", "⭐️",
            "ул. Советская 191", "https://bishkek.regency.hyatt.com/"),
        Service("Hostel Interhouse", "Популярный и недорогой хостел для путешественников с общими и частными номерами, с кухней.", "🏡",
            "ул. Тоголок Молдо 122", "https://hostelinterhouse.com/"),
        Service("Hotel Dostuk", "Проверенный отель с хорошим расположением, часто используется для деловых поездок, конференц-зал.", "🏨",
            "пр. Чуй 127", "https://hoteldostuk.kg/")
    )

    val transportList = listOf(
        Service("Такси Namba", "Местное приложение такси, популярно и удобно. Вызов по телефону или через приложение.", "🚕",
            "+996312510510", "tel:+996312510510"),
        Service("Международный аэропорт Манас", "Главный аэропорт Бишкека, находится в 25 км от города. Отсюда можно добраться на такси или маршрутке.", "✈️",
            "Аэропорт Манас", "https://www.airport.kg/")
    )


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(getTranslation("ServicesTitle", lang), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        }

        // --- БЛОК 1: Конвертер Валют ---
        item {
            Text(getTranslation("CurrencyExchange", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            CurrencyConverterCard(lang)
        }

        // --- БЛОК 2: Жилье (Отели/Хостелы) ---
        item {
            Text(getTranslation("Accommodation", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(accommodationList) { service ->
            ServiceCard(service = service, lang = lang) { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }

        // --- БЛОК 3: Транспорт ---
        item {
            Text(getTranslation("Transport", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        }
        items(transportList) { service ->
            ServiceCard(service = service, lang = lang) { url ->
                val action = if (url.startsWith("tel:")) Intent.ACTION_DIAL else Intent.ACTION_VIEW
                context.startActivity(Intent(action, Uri.parse(url)))
            }
        }
    }
}

@Composable
fun CurrencyConverterCard(lang: Language) {
    // Пользователь может вводить данные здесь
    var amountText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf<Currency>(Currency.USD) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val rate = when (selectedCurrency) {
        Currency.USD -> CurrencyRates.USD_TO_KGS
        Currency.EUR -> CurrencyRates.EUR_TO_KGS
        Currency.RUB -> CurrencyRates.RUB_TO_KGS
    }
    val convertedAmount = amount * rate
    val df = remember { DecimalFormat("#,##0.00") } // Формат для красивого вывода

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Выбор Валюты
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Currency.entries.forEach { currency ->
                    CurrencyButton(currency, selectedCurrency, lang) {
                        selectedCurrency = it
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Поле для ввода суммы (ИСПРАВЛЕНО ДЛЯ РУЧНОГО ВВОДА)
            OutlinedTextField(
                value = amountText,
                onValueChange = { newValue ->
                    // Разрешаем только числа и одну точку
                    amountText = newValue.filter { it.isDigit() || it == '.' }
                },
                label = { Text("${getTranslation("EnterAmount", lang)} (${selectedCurrency.name})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = { Text(selectedCurrency.name, color = MaterialTheme.colorScheme.primary) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Результат конвертации
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(getTranslation("ConvertedAmount", lang), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${df.format(convertedAmount)} KGS",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Справочная информация
            Text(getTranslation("ExchangeInfo", lang), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun CurrencyButton(currency: Currency, selectedCurrency: Currency, lang: Language, onClick: (Currency) -> Unit) {
    val isSelected = currency == selectedCurrency
    Button(
        onClick = { onClick(currency) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        // Используем фиксированную ширину, чтобы избежать ошибки weight
        modifier = Modifier
            .width(100.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(getTranslation(currency.name, lang), maxLines = 1, fontSize = 12.sp)
    }
}


@Composable
fun ServiceCard(service: Service, lang: Language, onClick: (String) -> Unit) {
    val isCall = service.url.startsWith("tel:")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(service.url) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(service.emoji, fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(service.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(service.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                if (isCall) Icons.Default.Phone else Icons.Default.OpenInBrowser,
                contentDescription = if (isCall) getTranslation("Call", lang) else getTranslation("GoToSite", lang),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ===============================================================================================
// 7. ЭКРАН КОНТАКТЫ (Contacts)
// ===============================================================================================

@Composable
fun ContactsScreen() {
    val context = LocalContext.current
    val lang = LocalLanguage.current.value

    val contacts = listOf(
        Contact("Скорая помощь", "103", Icons.Default.LocalHospital, ContactType.EMERGENCY),
        Contact("Пожарная служба", "101", Icons.Default.LocalFireDepartment, ContactType.EMERGENCY),
        Contact("Полиция (МВД)", "102", Icons.Default.Shield, ContactType.EMERGENCY),
        Contact("Аварийная газовая служба", "104", Icons.Default.LocalGasStation, ContactType.EMERGENCY),
        Contact("Такси Namba", "+996312510510", Icons.Default.LocalTaxi, ContactType.TAXI),
        Contact("Справочная города (109)", "109", Icons.Default.Phone, ContactType.INFO)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(getTranslation("UsefulContacts", lang), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        }

        items(contacts) { contact ->
            ContactCard(
                contact = contact,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${contact.number}") }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun ContactCard(contact: Contact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when(contact.type) {
                ContactType.EMERGENCY -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                ContactType.TAXI -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                ContactType.INFO -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        when(contact.type) {
                            ContactType.EMERGENCY -> MaterialTheme.colorScheme.errorContainer
                            ContactType.TAXI -> MaterialTheme.colorScheme.tertiaryContainer
                            ContactType.INFO -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    contact.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = when(contact.type) {
                        ContactType.EMERGENCY -> MaterialTheme.colorScheme.error
                        ContactType.TAXI -> MaterialTheme.colorScheme.tertiary
                        ContactType.INFO -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(contact.number, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.Default.Phone, contentDescription = "Позвонить", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
    }
}

// ===============================================================================================
// 8. ЭКРАН ИНФО (About)
// ===============================================================================================

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val lang = LocalLanguage.current.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text(getTranslation("BishkekGuide", lang), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(getTranslation("Version", lang), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        Text(getTranslation("AppInfo", lang), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🇰🇬", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(getTranslation("MadeWithLove", lang), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Попробуй Bishkek Guide - лучший гид по Бишкеку! 🇰🇬")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться"))
            },
            modifier = Modifier.fillMaxWidth(),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(getTranslation("ShareApp", lang))
        }
    }
}


// ===============================================================================================
// 9. Data classes (Классы Данных)
// ===============================================================================================

data class Place(
    val name: String,
    val description: String,
    val emoji: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class Contact(
    val name: String,
    val number: String,
    val icon: ImageVector,
    val type: ContactType
)

enum class ContactType {
    EMERGENCY, TAXI, INFO
}

data class Restaurant(
    val name: String,
    val cuisine: String,
    val emoji: String,
    val address: String,
    val phone: String,
    val dishes: List<String> // Добавлено поле для списка блюд
)

data class Service(
    val name: String,
    val description: String,
    val emoji: String,
    val address: String,
    val url: String
)

enum class Currency {
    USD, EUR, RUB
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

sealed class Screen {
    object Home : Screen()
    object Places : Screen()
    object Contacts : Screen()
    object Food : Screen()
    object About : Screen()
    object Services : Screen()
}