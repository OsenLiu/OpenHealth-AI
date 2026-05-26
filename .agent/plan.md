# Project Plan

SanoAI: An Android health app with AI insights. Now featuring a high-fidelity, date-driven Dashboard with doughnut charts, macro tracking, and detailed activity logs.

## Project Brief

# SanoAI Project Brief (Deep Detail Dashboard)

## Features
1. **Calorie Doughnut Summary**: Visual summary of daily intake vs goal and exercise burn.
2. **Macronutrient Tracking**: Real-time status of Carbs, Protein, and Fats.
3. **Daily Activity Logs**: Detailed, time-stamped lists of food (with tags like "Omega-3 Rich") and exercise.
4. **Interactive Calendar**: Switch between dates to view historical logs and AI advice.
5. **Refined AI Insights**: Data-driven, personalized "Diet Prescriptions" based on daily activity.
6. **VitaMind UI**: Organic shapes, warm palette, and high-fidelity detail.

## Tech Stack
- Kotlin, Compose, Room, Navigation 3, CameraX, Gemini SDK.

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
- **Updates:** Implemented the 'VitaMind' design system: updated theme with pastel colors, created BlobShape and SpeechBubble components, and integrated stylized icons. Build pass.
- **Acceptance Criteria:**
  - Material 3 theme updated with warm pastel palette (Beige, Mint, Sky Blue, Coral).
  - Custom organic 'blob' shapes and speech-bubble components implemented.
  - Stylized navigation icons integrated.
  - The implemented UI must match the design provided in /Users/osenliu/project/Project-OpenHealth-AI/input_images/image_0.png.

### Task_10_ScreenRefactorVerification: Apply the new design language to all app screens and perform final stability verification.
- **Status:** COMPLETED
- **Updates:** Full refactoring to 'VitaMind' design system completed: Dashboard, Chat, and Logging screens now use organic shapes and pastel colors. AI Health Assistant Advice card updated to speech-bubble style. Build pass.
- **Acceptance Criteria:**
  - Dashboard, Chat, and Logging screens updated with soft organic shapes and pastel colors.
  - AI Health Assistant Advice card redesigned as a speech-bubble.
  - All existing tests pass, build pass, and app does not crash.
  - Final Run and Verify: App is stable, UI matches reference, and no critical issues found.

### Task_11_ExpandedNutritionDataAi: Expand the data layer and AI service to support 11+ nutritional fields including sugar, fiber, and minerals.
- **Status:** COMPLETED
- **Updates:** Updated FoodLog entity and FoodAnalysisResponse DTO with 11 new fields (Sugar, Fiber, and 9 minerals). Incremented Room DB version and updated AiRepository prompts to request the full nutritional breakdown. Added unit tests to verify expanded JSON parsing. All 20 unit tests pass and build is successful.
- **Acceptance Criteria:**
  - FoodLog entity and Room DB updated with Sugar, Fiber, and 9 minerals (Calcium, Copper, Iron, Magnesium, Manganese, Phosphorus, Potassium, Sodium, Zinc).
  - AiRepository prompts and parsing logic updated for the new 11+ fields.
  - Unit tests updated to cover the expanded data model and AI parsing logic.
  - Build pass.

### Task_12_ExpandedNutritionUIAndVerify: Refactor the FoodLogScreen UI to display the expanded nutrition grid in VitaMind style and perform final verification.
- **Status:** COMPLETED
- **Updates:** Refactored FoodLogScreen UI to match the "VitaMind" expanded design, including a Macronutrients section and a Micronutrients grid (9 minerals). Integrated the UI with the updated HealthViewModel and FoodLog entity to support 15+ nutritional fields. Verified that AI analysis correctly populates the expanded form and data is persisted to Room DB. Final verification by critic agent confirmed UI fidelity to the reference image and application stability. All 20 unit tests pass.
- **Acceptance Criteria:**
  - FoodLogScreen UI updated with Macronutrients and Micronutrients grid.
  - UI follows the VitaMind organic shape and pastel color aesthetic.
  - The implemented UI must match the design provided in /Users/osenliu/project/Project-OpenHealth-AI/input_images/image_0.png.
  - All tests pass, app does not crash.
  - Final Run and Verify: Expanded nutrition features work correctly, and UI matches design expectations.

### Task_13_DashboardDataAndLogic: Update data layer and ViewModel to support date-driven dashboard queries and daily nutrition/activity summaries.
- **Status:** COMPLETED
- **Updates:** Updated Room DAO with date-range queries for food and exercise logs. Implemented reactive aggregation logic in HealthViewModel to calculate daily calorie and macronutrient summaries. Integrated a date switcher into the Dashboard UI and ensured AI suggestions are linked to the selected date. All 21 unit tests pass and project builds successfully.
- **Acceptance Criteria:**
  - DAO updated to fetch food and exercise logs by specific date range.
  - ViewModel logic implemented for daily calorie and macronutrient (Carbs, Protein, Fat) aggregation.
  - Calculation logic for doughnut chart data and macro percentages implemented.
  - Date switcher (calendar) logic integrated into ViewModel.
  - Build pass.

### Task_14_DeepDetailDashboardUI: Implement the high-fidelity Deep Detail Dashboard UI with doughnut charts, detailed logs, and historical date support.
- **Status:** COMPLETED
- **Updates:** Implemented the high-fidelity Deep Detail Dashboard UI. Key components include a custom Doughnut Chart for calorie summary, a row of Macronutrient status cards (Carbs, Protein, Fats) with grams and percentages, and chronological lists for Food and Exercise logs featuring AI-generated nutritional tags. The AI Insight card was redesigned as a 'Smart Diet Prescription' matching the requested style. The dashboard supports historical date viewing via an integrated date switcher. Build is successful and all unit tests pass.
- **Acceptance Criteria:**
  - Calorie Summary card with doughnut chart and progress bars implemented (image_1 style).
  - Macronutrient Status cards with percentages (Carbs, Protein, Fats).
  - Detailed, time-stamped scrollable lists for Food and Exercise with AI-generated tags.
  - AI Insight Card redesigned as 'Smart Diet Prescription' (image_2 style).
  - The implemented UI must match the design provided in /Users/osenliu/project/Project-OpenHealth-AI/input_images/image_1.png and /Users/osenliu/project/Project-OpenHealth-AI/input_images/image_2.png.
  - All tests pass, app does not crash.
  - Final Run and Verify: Dashboard is fully functional and reflects historical data correctly.
- **Duration:** N/A

