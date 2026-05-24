# MobileAI Studio

🧠 **Полноценная замена LM Studio для Android** — запускай AI-модели прямо на телефоне!

## Возможности
- 🔍 Поиск и скачивание GGUF моделей с HuggingFace Hub
- 💬 Чат-интерфейс с потоковой генерацией
- ⚡ Автоматический подбор моделей под ваше устройство
- 🔐 Полная приватность — все данные на устройстве
- 🌐 Облачная инференс через HuggingFace Inference Providers
- ⚙️ Детальная настройка параметров генерации
- 📱 Оптимизация под мобильные GPU (Vulkan)

## Технологии
- **Kotlin** + **Jetpack Compose** (Material 3)
- **llama.cpp** (C++/JNI) — нативная инференс
- **HuggingFace API** — поиск, скачивание, облачная генерация
- **Room** — локальная база данных
- **Hilt** — Dependency Injection
- **Retrofit** + **OkHttp** — сетевые запросы
- **DataStore** — настройки
- **CMake** — сборка нативного кода

## Требования
- Android 8.0 (API 26) и выше
- ARM64 архитектура
- Минимум 6 ГБ RAM для моделей 3B

## Скриншоты
[Главный экран чата] | [Обзор моделей] | [Настройки]
---|---|---

## Сборка
\`\`\`bash
./gradlew assembleDebug
\`\`\`

## Структура
\`app/src/main/java/com/mobileaistudio/\`
- \`di/\` — Hilt DI модули
- \`data/\` — Room DB, API, Repository
- \`domain/\` — бизнес-логика
- \`inference/\` — llama.cpp JNI обёртка
- \`ui/\` — Jetpack Compose экраны
- \`service/\` — Foreground сервисы
- \`cpp/\` — CMake/llama.cpp нативный код

## Лицензия
MIT License
