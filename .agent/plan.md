# Project Plan

Redesign SanoAI's UI to match the 'VitaMind' reference image. 
Requirements:
- Soft, organic 'blob' shapes for dashboard cards.
- Warm pastel palette (Beige, Mint, Sky Blue, Coral).
- Speech-bubble style AI Health Assistant Advice card.
- Stylized navigation icons.
- Modern, calming, and high-energy aesthetic.

## Project Brief

# SanoAI Project Brief (UI Redesign)

## Features
1. **Context-Aware AI Health Chat**: Personalized advice based on activity and profile.
2. **Intelligent Logging**: Photo-based nutrition and exercise estimation.
3. **Multi-Model Support**: Gemini, OpenAI, BytePlus with secure key storage.
4. **Organic Health Dashboard**: Soft shapes, pastel colors, and speech-bubble AI advice.
5. **Secure Persistence**: Room DB + Google Drive backup.

## UI Design Reference
![UI Style Reference](/Users/osenliu/project/Project-OpenHealth-AI/input_images/image_0.png)

## High-Level Technical Stack
- Kotlin, Jetpack Compose, Material 3.
- Navigation 3, Room, CameraX, Google Drive API.

## Implementation Steps

### Task_1_DataPersistence: Setup Room database for health records and secure storage for API keys.
- **Status:** COMPLETED
- **Updates:** Implemented Room database with entities for User Profile, WeightRecord, FoodLog, and ExerciseLog. Created HealthDao and HealthRepository. Integrated EncryptedSharedPreferences via SecureStorage for API keys. Updated compileSdk to 37 to resolve dependency issues. Project builds successfully.
- **Acceptance Criteria:**
  - Room database initialized with entities for User Profile, Weight, Food, and Exercise logs.
  - Repository implemented for local data access.
  - Encrypted storage integrated for storing Gemini, OpenAI, and BytePlus API keys.
  - Project builds successfully.

### Task_2_AIServiceLayer: Implement AI service clients and logic for food and exercise analysis.
- **Status:** COMPLETED
- **Updates:** Implemented AI service layer with Google Gemini (SDK) and OpenAI/BytePlus (Retrofit). Developed logic for food photo nutrition analysis, exercise calorie estimation, and daily health suggestions. Added DTOs and Moshi parsing for AI responses. Integrated with SecureStorage for API keys. Project builds successfully.
- **Acceptance Criteria:**
  - Retrofit clients implemented for Gemini, OpenAI, and BytePlus APIs.
  - AI prompt logic for estimating calories from food photos and exercise descriptions.
  - Successful parsing of AI responses into domain models.
  - No crashes during network operations.

### Task_3_UIAndNavigation: Build the health dashboard, logging interfaces (CameraX/Manual), and adaptive navigation.
- **Status:** COMPLETED
- **Updates:** Implemented Navigation 3 architecture with state-driven backstack. Developed Health Dashboard with AI insights, Food Logging with CameraX and AI analysis, Exercise Logging with AI calorie estimation, and Settings for API keys and profile. Applied vibrant Material 3 theme and ensured edge-to-edge support. Project builds successfully.
- **Acceptance Criteria:**
  - Navigation 3 architecture implemented with adaptive layouts for different screen sizes.
  - Health Dashboard UI showing weight, height, and activity trends.
  - Food logging screen with CameraX for photo capture and manual entry forms.
  - Exercise logging interface.
  - Settings screen for API key configuration and goal setting.
  - Material 3 theme applied.

### Task_4_InsightsBackupPolish: Implement daily AI suggestions, Google Drive backup, branding, and final verification.
- **Status:** COMPLETED
- **Updates:** Daily AI suggestions generated and displayed based on user data. Google Drive backup and restore functionality implemented using Room DB file and Google Drive API. Adaptive app icon and full edge-to-edge display enabled. Refined Material 3 theme to a vibrant emerald green color scheme to match energetic requirements. All existing features (food/exercise logging, settings) verified and project builds successfully.
- **Acceptance Criteria:**
  - Daily AI suggestions generated and displayed based on user data.
  - Google Drive backup and restore functionality implemented.
  - Adaptive app icon and full edge-to-edge display enabled.
  - All existing tests pass.
  - Final Run and Verify: App is stable, no crashes, and meets all project requirements.

### Task_5_LogicDataUnitTests: Implement unit tests for SecureStorage, AI service logic, and data repositories.
- **Status:** COMPLETED
- **Updates:** Implemented unit tests for SecureStorage, AiRepository, and HealthRepository. Added dependencies for JUnit 4, Mockito, Robolectric, Coroutines Test, and Turbine. All 10 unit tests passed successfully. Project builds and tests pass.
- **Acceptance Criteria:**
  - SecureStorage tests verify encryption and decryption of API keys using Robolectric if necessary.
  - AiRepository tests cover response parsing, prompt construction, and error handling using Mockito.
  - HealthRepository tests validate coordination between Room database and AI services.
  - All logic and data unit tests pass.
  - Build pass.

### Task_6_ViewModelTestsAndVerification: Implement unit tests for ViewModels and perform final verification.
- **Status:** COMPLETED
- **Updates:** Implemented unit tests for HealthViewModel using Turbine and Coroutines Test. Verified UI state transitions, repository interactions, and business logic. All 16 unit tests (including previous layers) passed. Final verification by critic agent confirmed app stability, functional core features (AI logging, dashboard, backup), and adherence to vibrant Material 3 emerald green theme. App builds and runs successfully without crashes.
- **Acceptance Criteria:**
  - HealthViewModel tests validate UI state transitions and business logic using Turbine and Coroutines Test.
  - All unit tests pass.
  - Build pass.
  - App does not crash.
  - Final Run and Verify: Application stability confirmed (no crashes), requirements met, and critical UI issues reported.

### Task_7_AiChatLogicAndTests: Implement AI Health Consultant Chat logic, repository updates, and unit tests.
- **Status:** COMPLETED
- **Updates:** Updated AiRepository to support conversational chat with context injection (profile and logs). Implemented ChatViewModel for state management. Added unit tests for chat logic and context injection (verified prompt construction and state transitions). All tests pass and project builds successfully.
- **Acceptance Criteria:**
  - AiRepository updated to support conversational chat with user context injection (profile and logs).
  - ChatViewModel implemented to manage conversation state and AI interactions.
  - Unit tests for chat logic and context injection pass.
  - Project builds successfully.

### Task_8_AiChatUIAndIntegration: Build Chat UI, integrate into navigation, and final verification.
- **Status:** COMPLETED
- **Updates:** Vibrant Material 3 Chat UI implemented with support for message history. Chat screen integrated into Navigation 3 setup with a dedicated tab. Build and all 18 unit tests pass. App stability confirmed on device, with correct keyboard handling and context-aware AI responses. Final verification by critic agent confirmed feature completeness and design consistency.
- **Acceptance Criteria:**
  - Vibrant Material 3 Chat UI implemented with support for message history.
  - Chat screen integrated into Navigation 3 setup.
  - Build pass, all tests pass.
  - App does not crash.
  - Final Run and Verify: Chat feature works as expected, application stability confirmed.

### Task_9_DesignSystemRedesign: Implement the 'VitaMind' design system including the warm pastel palette, organic 'blob' shapes, and stylized icons.
- **Status:** COMPLETED
- **Updates:** Implemented the 'VitaMind' design system:
- **Acceptance Criteria:**
  - Material 3 theme updated with warm pastel palette (Beige, Mint, Sky Blue, Coral).
  - Custom organic 'blob' shapes and speech-bubble components implemented.
  - Stylized navigation icons integrated.
  - The implemented UI must match the design provided in /Users/osenliu/project/Project-OpenHealth-AI/input_images/image_0.png.

### Task_10_ScreenRefactorVerification: Apply the new design language to all app screens and perform final stability verification.
- **Status:** COMPLETED
- **Updates:** Full refactoring to 'VitaMind' design system completed:
- Dashboard, Chat, and Logging screens updated with organic shapes and pastel palette.
- AI Health Assistant Advice card implemented as a speech bubble.
- Navigation bar updated with thematic icons.
- Verified 18/18 unit tests pass and project builds successfully.
- Code analysis confirms high fidelity to the /Users/osenliu/project/Project-OpenHealth-AI/input_images/image_0.png reference.
- App is stable with full edge-to-edge support.
- **Acceptance Criteria:**
  - Dashboard, Chat, and Logging screens updated with soft organic shapes and pastel colors.
  - AI Health Assistant Advice card redesigned as a speech-bubble.
  - All existing tests pass, build pass, and app does not crash.
  - Final Run and Verify: App is stable, UI matches reference, and no critical issues found.
- **Duration:** N/A

