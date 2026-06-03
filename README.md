<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# NexReceipt Studio X - Smart Receipt & Invoice Designer

A professional Android app for creating, designing, and managing custom receipt and invoice templates with AI-powered capabilities.

## Features

- 🎨 **Smart Receipt Designer** - Create custom receipt templates with intuitive editor
- 📸 **Camera Integration** - Capture and process receipts from images
- 🗂️ **Template Management** - Save, preview, and organize multiple receipt templates
- 🔐 **Secure Storage** - Local Room database with encrypted preferences
- 🌓 **Dark Mode & AMOLED Support** - Beautiful UI with customizable themes
- 🧠 **AI Integration** - Powered by Google Gemini API for intelligent receipt processing
- 📡 **Network Support** - Retrofit + OkHttp for robust API communication
- 🔄 **Reactive Architecture** - Coroutines and Flow for smooth data operations

## Run Locally

**Prerequisites:** 
- [Android Studio](https://developer.android.com/studio) (Latest)
- JDK 17+
- Android SDK 36+

### Setup Steps

1. **Clone & Open Project**
   ```bash
   git clone https://github.com/rahman2008-svg/nexreceipt-studio-x.git
   ```
   - Open Android Studio
   - Select **File → Open** and choose the project directory
   - Allow Android Studio to sync Gradle files

2. **Configure Environment Variables**
   ```bash
   # Create .env file in project root
   cp .env.example .env
   ```
   - Add your Gemini API key: `GEMINI_API_KEY=your_key_here`

3. **Build & Run**
   - **Debug Build:** `./gradlew assembleDebug` (or use Android Studio's Build button)
   - **Release Build:** Set environment variables:
     ```bash
     export KEYSTORE_PATH=/path/to/keystore.jks
     export STORE_PASSWORD=your_password
     export KEY_ALIAS=your_alias
     export KEY_PASSWORD=your_key_password
     ./gradlew assembleRelease
     ```

4. **Run on Device**
   - Connect an Android device or start an emulator
   - Click **Run** in Android Studio or: `./gradlew installDebug`

## Architecture

```
app/
├── src/main/
│   ├── java/com/example/
│   │   ├── MainActivity.kt          # Main entry point with navigation
│   │   ├── data/
│   │   │   ├── AppDatabase.kt       # Room database setup
│   │   │   ├── Models.kt            # Data entities
│   │   │   ├── ReceiptDao.kt        # Database access object
│   │   │   └── ReceiptRepository.kt # Data layer abstraction
│   │   └── ui/
│   │       ├── ReceiptViewModel.kt      # Business logic & state
│   │       ├── screens/             # Composable screens
│   │       │   ├── AuthScreen
│   │       │   ├── DashboardScreen
│   │       │   ├── EditorScreen
│   │       │   ├── TemplatePreviewScreen
│   │       │   ├── AdminScreen
│   │       │   └── SettingsScreen
│   │       └── theme/               # UI theming
│   └── res/
│       └── values/                  # Resources & strings
└── build.gradle.kts                 # App-level build config
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| **UI Framework** | Jetpack Compose |
| **Navigation** | Compose Navigation |
| **State Management** | ViewModel + StateFlow |
| **Database** | Room + SQLite |
| **Networking** | Retrofit + OkHttp + Moshi |
| **Async** | Coroutines + Flow |
| **AI/ML** | Google Gemini API |
| **Build System** | Gradle 9.1.1 |
| **Kotlin Version** | 2.2.10 |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 36 (Android 15) |

## Build Configuration

### Gradle Properties
- **JVM Memory:** 4GB
- **Workers:** 4 max
- **Kotlin Compiler:** In-process execution
- **Build Caching:** Enabled
- **Configuration Cache:** Enabled

### Signing Configuration
- **Debug:** Uses default Android keystore
- **Release:** Environment variable-based (CI/CD compatible)

## Key Dependencies

- `androidx.compose.bom:2024.09.00` - Compose toolkit
- `androidx.room:2.7.0` - Database
- `com.squareup.retrofit2:2.12.0` - HTTP client
- `org.jetbrains.kotlinx:kotlinx-coroutines:1.10.2` - Async
- `com.squareup.moshi:1.15.2` - JSON parsing
- `com.google.android.gms:play-services-location:21.3.0` - Location services
- `androidx.camera:1.5.0` - Camera functionality

## Troubleshooting

### Build Fails with "Secrets Plugin Error"
- Ensure `.env.example` exists and is properly configured
- Check that `BuildConfig.DEBUG_SECRETS` is correctly set

### Navigation Runtime Error
- Verify all screen composables are implemented in `ui/screens/`
- Check route definitions match exactly in `MainActivity.kt`

### Dependency Resolution Issues
- Run: `./gradlew --refresh-dependencies`
- Delete `.gradle` folder and retry

## Development Guidelines

### Adding New Screen
1. Create composable in `ui/screens/YourScreen.kt`
2. Add route in `MainActivity.kt` NavHost
3. Update ReceiptViewModel if needed for business logic

### Database Migration
1. Increment `Room.SchemaExportDirectory` version
2. Update entity classes in `data/Models.kt`
3. Create migration in `AppDatabase.kt`

## Contributing

This is a personal project. For suggestions or issues, create a GitHub issue or contact the maintainer.

## License

This project is open source. See LICENSE file for details.

---

**Status:** Active Development  
**Last Updated:** June 2026  
**Owner:** [@rahman2008-svg](https://github.com/rahman2008-svg)
