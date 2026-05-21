# SanoAI: AI-Powered Health & Activity Monitor

SanoAI is a modern, vibrant Android application designed to help users monitor their health and fitness journey through advanced AI integrations. By combining local tracking with state-of-the-art AI models, SanoAI provides personalized insights and effortless logging.

## ✨ Features

- **AI Health Consultant Chat**: A conversational interface where you can ask health or sports questions. The AI provides personalized advice by referencing your recent activity logs and physical profile.
- **Smart Nutrition Tracking**: Effortlessly log meals using AI-powered photo estimation (CameraX) or manual entry.
- **Activity Tracking**: Track exercises with AI-driven calorie burn calculations based on natural language descriptions.
- **Daily AI Recommendations**: Automated generation of health and fitness suggestions tailored to your goals and historical data.
- **Secure Multi-Model Support**: Integrated support for Gemini (3.5 Flash), OpenAI (GPT-5.4 Mini), and BytePlus ModelArk.
- **Google Drive Backup**: Securely back up and restore your local Room database to your private Google Drive App Data folder.
- **Privacy First**: Local-first storage with Room and encrypted API key management using `EncryptedSharedPreferences`.

## 🛠 Tech Stack

- **UI**: Jetpack Compose with Material 3 (Vibrant Emerald Theme)
- **Navigation**: Jetpack Navigation 3 (State-driven)
- **Architecture**: MVVM with Repository pattern
- **AI SDKs**: Google Generative AI (Gemini), OpenAI Protocol (Retrofit)
- **Database**: Room DB
- **Networking**: Retrofit, OkHttp, Moshi
- **Camera**: CameraX
- **Security**: Android Security Crypto (EncryptedSharedPreferences)
- **Cloud**: Google Sign-In & Google Drive API
- **Testing**: JUnit 4, Mockito, Robolectric, Turbine, Coroutines Test

## 📱 Screenshots

*Coming Soon - Vibrant Emerald Design*

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala or newer
- JDK 17+
- A Google Cloud Project for Gemini/Drive API (optional for development)
- An OpenAI or BytePlus API key (optional)

### Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone git@github.com:OsenLiu/OpenHealth-AI.git
   ```
2. **Open in Android Studio**:
   Import the project and let Gradle sync.
3. **Configure API Keys**:
   - Open the app and navigate to **Settings**.
   - Enter your Gemini, OpenAI, or BytePlus API keys. These are stored securely on your device.
4. **Google Drive Backup**:
   - In Settings, connect your Google Account to enable cloud backup.

## 🧪 Running Tests

To run the comprehensive suite of 18 unit tests:
```bash
./gradlew :app:testDebugUnitTest
```

## ⚖️ License

Distributed under the MIT License. See `LICENSE` for more information.
