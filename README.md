# 🎨 Sticker Maker (Tạo & Quản Lý Nhãn Dán Trực Quan Cho WhatsApp & Chat Apps) 📱

[![Android CI/CD](https://github.com/DoAnhThu832004/StickerMaker/actions/workflows/android.yml/badge.svg)](https://github.com/DoAnhThu832004/StickerMaker/actions/workflows/android.yml)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.02.01-4285F4.svg)
![License](https://img.shields.io/badge/License-MIT-orange.svg)

Ứng dụng Android hỗ trợ tạo, quản lý các bộ sticker tĩnh & động từ ảnh cá nhân và tích hợp xuất trực tiếp sang **WhatsApp** theo chuẩn WhatsApp Sticker API, được xây dựng theo kiến trúc **Clean Architecture** với **Jetpack Compose Material 3** hiện đại.

---

## 📖 Giới thiệu (Overview)

**Sticker Maker** là giải pháp sáng tạo và cá nhân hóa nhãn dán toàn diện trên nền tảng Android. Ứng dụng giúp người dùng nhanh chóng chuyển đổi những khoảnh khắc, ảnh kỷ niệm, meme hoặc hình ảnh yêu thích thành các bộ sưu tập sticker sống động và sẵn sàng chia sẻ vào các cuộc trò chuyện trên **WhatsApp**, **Zalo**, **Telegram**.

Không chỉ chú trọng vào sự tiện dụng, **Sticker Maker** được thiết kế với phong cách đồ họa hiện đại mang tính thẩm mỹ cao: hiệu ứng cực quang **Aurora Glow**, bề mặt kính mờ trong suốt (**Glassmorphism**), hiệu ứng chuyển cảnh khởi động điện ảnh 6 pha 3D (**Cinematic 3D Splash**), menu điều hướng hướng tâm bung nan quạt tương tác mượt mà (**Radial Floating Action Button**) cùng hộp thoại đánh giá cảm xúc biến hình sống động (**Satisfaction Slider Dialog**).

---

## 🎬 Video Demo & Trải nghiệm (Demo & Preview)

<div align="center">

> 💡 *Dưới đây là video / ảnh động mô phỏng giao diện và trải nghiệm thực tế của ứng dụng.*

<!-- Cách 1: Video MP4 / WebM phát trực tiếp trên GitHub -->
<!-- Bạn có thể kéo thả file video .mp4 vào khung soạn thảo GitHub Issue/PR hoặc tải lên thư mục docs/ rồi dán link vào đây -->
<video src="docs/demo.mp4" controls="controls" width="340" style="max-height: 600px; border-radius: 16px; box-shadow: 0 8px 24px rgba(0,0,0,0.3);">
  Trình duyệt của bạn không hỗ trợ thẻ video.
</video>

<!-- Cách 2 (Khuyên dùng): File GIF động tự động phát lặp lại -->
<!--
<img src="docs/demo.gif" width="320" alt="Sticker Maker Demo GIF" style="border-radius: 16px;" />
-->

<!-- Cách 3: Video YouTube nếu upload lên YouTube -->
<!--
[![Xem Video Demo Trên YouTube](https://img.youtube.com/vi/YOUR_VIDEO_ID/maxresdefault.jpg)](https://www.youtube.com/watch?v=YOUR_VIDEO_ID)
-->

<p align="center">
  <i>Giao diện Glassmorphism, Splash 6 pha 3D, FAB nan quạt hướng tâm và tính năng xuất trực tiếp sang WhatsApp</i>
</p>

</div>

---

## ✨ Tính năng nổi bật (Key Features)

* **📦 Tạo & Quản lý bộ Sticker tùy biến (Custom Sticker Packs):**
  * Hỗ trợ tạo không giới hạn các gói sticker tĩnh (**Regular / Static**) hoặc nhãn dán động (**Animated**).
  * Đặt tên cho từng gói, lưu trữ trạng thái vào cơ sở dữ liệu Room cục bộ, dễ dàng đổi tên hoặc xóa các gói không còn nhu cầu.

* **🖼️ Thêm nhãn dán trực quan từ bộ nhớ (Smart Image Picker & Grid):**
  * Tích hợp công cụ chọn ảnh hiện đại (`ActivityResultContracts.GetContent`) an toàn, bảo mật quyền riêng tư mà không đòi hỏi quyền nguy hiểm.
  * Hiển thị lưới 3 cột trực quan với thư viện **Coil Compose**, hỗ trợ xem trước, xóa bỏ từng sticker trong gói linh hoạt.

* **🟢 Xuất sang WhatsApp chuẩn hóa (Native WhatsApp Stickers Integration):**
  * Tích hợp đầy đủ `StickerContentProvider` chuẩn WhatsApp Sticker API, sẵn sàng kết nối trực tiếp với **WhatsApp** và **WhatsApp Business**.
  * Kiểm tra điều kiện xuất nghiêm ngặt (yêu cầu tối thiểu 3 sticker/gói theo quy chuẩn của WhatsApp) và thêm vào danh mục nhãn dán WhatsApp chỉ với 1 cú chạm.

* **🌟 Menu hướng tâm nan quạt độc đáo (Radial Floating Action Button):**
  * Thanh FAB tương tác đa trạng thái bung nan quạt theo hình học chuyển động (lấy cảm hứng từ *Lucian UX/UI Prompt #011*).
  * Tích hợp hiệu ứng hào quang (**Halo Glow**), nhịp thở êm ái (**Breathing Pulse**) và thông báo kết quả nổi (**Floating Status Note**) với 4 tùy chọn: *Sticker Động*, *Sticker Tĩnh*, *My Sticker*, *Khám phá*.

* **🎬 Màn hình khởi động điện ảnh 6 pha (Cinematic 3D Splash Screen):**
  * Dòng chữ biểu tượng được dựng 3 lớp layer không gian: *Shadow -> Border -> Face*.
  * Kịch bản chuyển động liền mạch qua 6 pha: **Intro** (bay vào & khử mờ) $\rightarrow$ **Wobble** (lắc lư) $\rightarrow$ **Scatter** (phân tán đa hướng) $\rightarrow$ **Snap** (gom tụ đàn hồi spring) $\rightarrow$ **Shimmer** (lấp lánh sao xoay) $\rightarrow$ **Exit Zoom-through 25x** (xuyên thấu vào màn hình chính).

* **🌈 Giới thiệu Onboarding đa tầng (Interactive Aurora Onboarding):**
  * Hệ thống dẫn dắt lần đầu với 3 trang giới thiệu sinh động tương ứng với 3 hero đồ họa tương tác:
    1. *Floating Tiles Hero*: Các thẻ sticker lơ lửng, chuyển động đa chiều.
    2. *Pack Stack Hero*: Chồng thẻ sticker hiệu ứng xếp lớp 3D isometric.
    3. *Platforms Hero*: Các biểu tượng nền tảng mạng xã hội và chat app thân thiện.

* **😊 Hộp thoại đánh giá cảm xúc biến hình (Satisfaction Slider Dialog):**
  * Khuôn mặt cảm xúc hoạt hình phản ứng liên tục theo thanh trượt 5 nấc (*Rất tệ $\rightarrow$ Rất tuyệt vời*), đổi màu sắc gradient từ đỏ sang vàng đến xanh lá rực rỡ (*Lucian UX/UI Prompt #380*).
  * Hiệu ứng má hồng chuyển sắc, chuyển động mắt cười vòng cung và hoạt cảnh gật đầu (**Head nod**) khi gửi đánh giá.

* **⚙️ Cài đặt, Phản hồi & Tương tác thuận tiện (Settings & Feedback):**
  * Hỗ trợ chấm 5 sao trên Google Play, chia sẻ app nhanh chóng tới bạn bè.
  * Tự động tạo thư góp ý qua email kèm thông số kỹ thuật thiết bị (*Manufacturer, Model, Android OS Version*) phục vụ việc hỗ trợ kỹ thuật nhanh chóng và chính xác.

---

## 🏛️ Kiến trúc dự án (Architecture)

Dự án tuân thủ nghiêm ngặt nguyên lý **Clean Architecture** kết hợp mô hình luồng dữ liệu một chiều **MVI / MVVM** (Unidirectional Data Flow - UDF):

```
┌────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                   │
│   Jetpack Compose (M3)  │  UiState / UiEvent / UiEffect│
│   Navigation Compose    │  ViewModels & Custom Canvas  │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                      DOMAIN LAYER                      │
│   Pure Kotlin Business Logic  │  Immutable Models      │
│   UseCases (PrepareInitialData, CompleteIntro)         │
│   Repository Interfaces                                │
└───────────────────────────▲────────────────────────────┘
                            │
┌───────────────────────────┴────────────────────────────┐
│                       DATA LAYER                       │
│   Room Database (KSP)   │  AppPreferences (SharedPrefs)│
│   Repository Implementations (Flow & Coroutines)       │
└────────────────────────────────────────────────────────┘
                            ▲
┌───────────────────────────┴────────────────────────────┐
│               CORE / INTEGRATION LAYER                 │
│   StickerContentProvider (WhatsApp Sticker API)        │
│   WhatsAppStickerHelper (Intent & Package Manager)     │
└────────────────────────────────────────────────────────┘
```

### Chi tiết các tầng:

1. **Presentation Layer:**
   * Xây dựng hoàn toàn bằng **Jetpack Compose** theo phong cách khai báo (Declarative UI) cùng **Material 3**.
   * Quản lý trạng thái thông qua `UiState` bất biến, nhận tương tác người dùng qua `UiEvent`, và phát các sự kiện một lần (One-shot event như điều hướng, thông báo) qua `UiEffect` (Coroutines Channel / Flow).
   * Điều hướng ứng dụng tập trung bằng **Navigation Compose** (`AppNavHost`).

2. **Domain Layer:**
   * Chứa các mô hình dữ liệu cốt lõi (`StickerPack`) và các interface hợp đồng Repository (`StickerRepository`, `UserPreferencesRepository`).
   * Từng nghiệp vụ được đóng gói trong các UseCase chuyên biệt: `PrepareInitialDataUseCase`, `CompleteIntroUseCase`.
   * Hoàn toàn độc lập với Android Framework và cơ sở dữ liệu.

3. **Data Layer:**
   * **Room Database (KSP Generation):** Lưu trữ các bộ sticker người dùng tạo, hỗ trợ truy vấn phản ứng qua reactive `Flow`.
   * **StickerListConverter:** TypeConverter chuyển đổi danh sách URI ảnh sang chuỗi văn bản và ngược lại.
   * **AppPreferences:** Quản lý cấu hình cục bộ và trạng thái hoàn thành Onboarding của người dùng.

4. **Core & WhatsApp Integration:**
   * **StickerContentProvider:** Cung cấp thông tin metadata gói sticker và các stream file nhãn dán cho ứng dụng WhatsApp thông qua Content Provider URI an toàn.
   * **WhatsAppStickerHelper:** Xử lý logic kiểm tra cài đặt WhatsApp / WhatsApp Business và điều hướng Intent thêm gói sticker (`ACTION_ENABLE_STICKER_PACK`).

5. **Dependency Injection:**
   * Sử dụng **Dagger Hilt** (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`) để quản lý vòng đời và tiêm phụ thuộc toàn diện.

---

## 📁 Cấu trúc thư mục (Project Structure)

```
com.thupo/
├── App.kt                               # Application class (@HiltAndroidApp)
├── MainActivity.kt                      # Single Activity chính với Jetpack Compose Host
│
├── core/
│   └── whatsapp/
│       ├── StickerContentProvider.kt    # ContentProvider chuẩn giao tiếp WhatsApp Sticker API
│       └── WhatsAppStickerHelper.kt     # Helper kiểm tra & điều hướng Intent thêm nhãn dán
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt               # Room Database cấu hình lưu trữ
│   │   ├── converter/
│   │   │   └── StickerListConverter.kt  # TypeConverter cho danh sách URI nhãn dán
│   │   ├── dao/
│   │   │   └── StickerPackDao.kt        # Data Access Object quản lý gói sticker
│   │   ├── entity/
│   │   │   └── StickerPackEntity.kt     # Entity bảng sticker_packs
│   │   └── pref/
│   │       └── AppPreferences.kt        # Quản lý cấu hình SharedPreferences
│   └── repository/
│       ├── StickerRepositoryImpl.kt     # Hiện thực hóa StickerRepository qua Room Flow
│       └── UserPreferencesRepositoryImpl.kt
│
├── di/
│   ├── DatabaseModule.kt                # Hilt Module cung cấp Room Database & DAO
│   └── RepositoryModule.kt              # Hilt Module liên kết các Repository Interface
│
├── domain/
│   ├── model/
│   │   └── StickerPack.kt               # Model dữ liệu bộ sticker (bất biến @Immutable)
│   ├── repository/
│   │   ├── StickerRepository.kt         # Interface quản lý nghiệp vụ sticker
│   │   └── UserPreferencesRepository.kt # Interface quản lý tùy chọn người dùng
│   └── usecase/
│       ├── CompleteIntroUseCase.kt      # UseCase đánh dấu hoàn thành Intro/Onboarding
│       └── PrepareInitialDataUseCase.kt # UseCase kiểm tra điểm đến khởi đầu
│
├── presentation/
│   ├── navigation/
│   │   ├── AppNavHost.kt                # Host điều hướng chính (Splash -> Intro -> Home / Detail)
│   │   └── Screen.kt                    # Sealed Class định nghĩa các Route màn hình
│   └── screens/
│       ├── home/
│       │   ├── HomeContract.kt          # MVI Contract (UiState, UiEvent)
│       │   ├── HomeScreen.kt            # Màn hình chính danh sách bộ sticker
│       │   ├── HomeViewModel.kt         # Quản lý logic màn hình Home
│       │   └── components/
│       │       ├── CreatePackDialog.kt  # Hộp thoại tạo gói sticker mới
│       │       ├── RadialFloatingActionButton.kt # FAB hướng tâm 4 phím chức năng
│       │       ├── SpeedDialFab.kt      # Menu phụ trợ
│       │       ├── StickerPackCard.kt   # Card hiển thị bộ sticker và nút thêm WhatsApp
│       │       └── StickerTitle.kt      # Tiêu đề phong cách nghệ thuật
│       │
│       ├── pack_detail/
│       │   ├── PackDetailScreen.kt      # Màn hình quản lý chi tiết nhãn dán trong gói
│       │   └── PackDetailViewModel.kt   # Logic thêm ảnh, xóa ảnh, xóa gói
│       │
│       ├── intro/
│       │   ├── IntroContract.kt         # Contract điều khiển luồng Onboarding
│       │   ├── IntroScreen.kt           # Màn hình Onboarding 3 trang lướt ngang
│       │   ├── IntroViewModel.kt        # Logic cập nhật tiến trình Onboarding
│       │   ├── model/
│       │   │   └── IntroPage.kt         # Data model trang Intro
│       │   └── components/
│       │       ├── AuroraGlow.kt        # Hiệu ứng cực quang chuyển sắc
│       │       ├── GlassCard.kt         # Thành phần giao diện Glassmorphism
│       │       ├── IntroControls.kt     # Nút chuyển tiếp và Indicator chỉ mục trang
│       │       └── heros/
│       │           ├── FloatingTilesHero.kt  # Đồ họa các ô sticker bay lơ lửng
│       │           ├── PackStackHero.kt      # Đồ họa chồng gói nhãn dán 3D
│       │           └── PlatformsHero.kt      # Đồ họa biểu tượng ứng dụng chat
│       │
│       ├── splash/
│       │   ├── SplashContact.kt         # MVI Contract màn hình Splash
│       │   ├── SplashScreen.kt          # Màn hình Splash kích hoạt chuỗi hiệu ứng
│       │   ├── SplashTransition.kt      # Quản lý chuyển cảnh xuyên thấu vào đích
│       │   ├── SplashViewModel.kt       # Điều phối thời gian và điều kiện điều hướng
│       │   ├── config/
│       │   │   └── SplashConfig.kt      # Tham số thời gian & tỷ lệ đồ họa splash
│       │   ├── model/
│       │   │   ├── Glyph.kt             # Cấu trúc Vector ký tự
│       │   │   ├── LetterAnimStat.kt    # Trạng thái animate từng chữ cái
│       │   │   └── WordMetrics.kt       # Kích thước hình học của cụm ký tự
│       │   └── components/
│       │       ├── StickerDrawingUtils.kt # Tiện ích vẽ Path, Blur, Shadow, Shimmer
│       │       ├── StickerLetterLayer.kt  # Vẽ các tầng Layer mặt chữ, viền, bóng râm
│       │       └── StickerText.kt         # Điều phối 6 pha animation Sticker Text Splash
│       │
│       └── setting/
│           ├── SettingScreen.kt         # Màn hình Cài đặt, Chia sẻ & Phản hồi
│           └── components/
│               └── SatisfactionSliderDialog.kt # Hộp thoại thanh trượt khuôn mặt biểu cảm
│
└── ui/theme/
    ├── Color.kt                         # Bảng màu sắc thiết kế
    ├── Theme.kt                         # Cấu hình MaterialTheme và Dynamic Colors
    └── Type.kt                          # Cấu hình Typography
```

---

## 🛠️ Công nghệ & Thư viện (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Phiên bản |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Kotlin | `2.2.10` |
| **UI Framework** | Jetpack Compose (BOM) | `2026.02.01` |
| **Design System** | Material 3 & Extended Icons | - |
| **Architecture** | Clean Architecture + MVI/MVVM | - |
| **Dependency Injection** | Dagger Hilt | `2.60.1` |
| **Navigation** | Navigation Compose & Hilt Navigation | `1.2.0` |
| **Database** | Room Database (KSP Kotlin CodeGen) | `2.6.1` |
| **Tải ảnh (Image Loading)** | Coil Compose | `2.7.0` |
| **Animation Engine** | Compose Animations & Lottie Compose | `6.6.0` |
| **Xử lý bất đồng bộ** | Kotlin Coroutines & Reactive Flow | `1.9.0` |
| **Tích hợp WhatsApp** | Android ContentProvider & WhatsApp Sticker API | - |
| **Build Tool** | Android Gradle Plugin (AGP) / KSP | `9.1.1` / `2.2.10-2.0.2` |

---

## 🔄 Quy trình DevOps & Tự động hóa CI/CD (DevOps & Automation Pipeline)

Dự án áp dụng quy trình thực hành kỹ thuật DevOps hiện đại từ khâu kiểm thử mã nguồn, tối ưu hóa kích thước build đến tự động hóa triển khai và phân tích hiệu năng thực nghiệm:

```
                  ┌──────────────────────────────────────────┐
                  │          Git Push / Pull Request         │
                  └────────────────────┬─────────────────────┘
                                       │
                  ┌────────────────────▼─────────────────────┐
                  │        GitHub Actions CI Pipeline        │
                  │   - Ubuntu Runner + JDK 21 (Temurin)     │
                  │   - Gradle Build Cache (setup-gradle@v4) │
                  │   - Concurrency: cancel-in-progress      │
                  └────────────────────┬─────────────────────┘
                                       │
       ┌───────────────────────────────┼───────────────────────────────┐
       │                               │                               │
┌──────▼──────┐                ┌───────▼────────┐             ┌────────▼────────┐
│ Android Lint│                │Unit Test Suite │             │  Build Release  │
│ (lintDebug) │                │(testDebugUnit) │             │ (assembleDebug) │
└──────┬──────┘                └───────┬────────┘             └────────┬────────┘
       │                               │                               │
       └───────────────────────────────┼───────────────────────────────┘
                                       │
                  ┌────────────────────▼─────────────────────┐
                  │    Automated Artifact Delivery (v4)      │
                  │   - Upload app-debug.apk (5 days cache)  │
                  └──────────────────────────────────────────┘
```

### 1. 🤖 CI/CD Automation (GitHub Actions)
* **Kích hoạt tự động (Triggers):** Tự động khởi chạy pipeline kiểm tra khi có `push` hoặc `pull_request` trên các nhánh chính (`main`, `master`).
* **Quản lý tài nguyên đồng thời (Concurrency Control):** Thiết lập `cancel-in-progress: true` để tự động hủy các tác vụ build cũ khi lập trình viên đẩy commit mới lên cùng nhánh, tiết kiệm tối đa tài nguyên runner.
* **Môi trường & Bộ nhớ đệm (Environment & Caching):**
  * Runner môi trường ảo hóa `ubuntu-latest`.
  * Khởi tạo **OpenJDK 21** (`Eclipse Temurin`) đồng bộ với Gradle Daemon JVM.
  * Tích hợp **Gradle Build Action** (`gradle/actions/setup-gradle@v4`) lưu trữ bộ đệm dependencies và build cache, giảm thời gian build qua từng lần commit.
* **Kiểm định chất lượng tĩnh & Kiểm thử (Quality Gates & Testing):**
  * **Android Lint (`lintDebug`):** Tự động phát hiện lỗi tiềm ẩn, kiểm tra tương thích API, quy chuẩn giao diện và tối ưu tài nguyên.
  * **Unit Tests (`testDebugUnitTest`):** Chạy toàn bộ bộ test case kiểm thử logic miền và UseCase.
* **Phân phối sản phẩm tự động (Artifact Upload):** Tự động đóng gói và xuất file `app-debug.apk` thông qua `actions/upload-artifact@v4` với thời gian lưu trữ 5 ngày cho các lượt push lên nhánh chính.

---

### 2. ⚡ Tối ưu hóa Bản dựng (Build Optimization & R8 Full Mode)
* **R8 Code Shrinker ở chế độ Full Mode:** Kích hoạt `android.enableR8.fullMode=true` trong `gradle.properties` kết hợp `isMinifyEnabled = true` và `isShrinkResources = true` để loại bỏ mã thừa (dead code), làm gọn tên lớp/hàm và giảm dung lượng APK tới mức tối thiểu.
* **KSP (Kotlin Symbol Processing):** Biên dịch và sinh mã trực tiếp bằng KSP cho Room Database (`arg("room.generateKotlin", "true")`), tăng tốc độ biên dịch gấp 2 lần so với KAPT truyền thống.
* **Cấu hình Daemon tối ưu:** `org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8` đảm bảo tiến trình Gradle vận hành mượt mà, tránh lỗi thiếu bộ nhớ khi xử lý đồ họa vector và tài nguyên lớn.

---

### 3. 📊 Kỹ thuật Hiệu năng & Đo kiểm Thực nghiệm (Performance Telemetry)
Dự án duy trì bộ công cụ phân tích và đo lường hiệu năng (`perf/`) giúp so sánh sự biến thiên hiệu năng trước và sau khi tối ưu hóa (**Baseline vs. After**):
* **Đo thời gian khởi động (Startup Benchmark):** Thu thập dữ liệu khởi động qua 5 chu kỳ lặp lại (`startup_run_1.txt` $\rightarrow$ `startup_run_5.txt`) để tổng hợp chỉ số thời gian Cold / Warm Start (`startup_summary.txt`).
* **Hồ sơ bộ nhớ (RAM / Memory Profiling):** Phân tích chi tiết mức tiêu thụ bộ nhớ Dalvik Heap, Native Heap và Graphics qua `adb shell dumpsys meminfo` (`ram_meminfo.txt`).
* **Tốc độ khung hình & Khử giật hình (Rendering & Jank Analysis):** Đếm số khung hình bị trễ (Janky Frames) và phân tích độ trễ Render 90th/95th/99th percentile qua `adb shell dumpsys gfxinfo` (`render_gfxinfo.txt`).
* **Nhiệt độ & Năng lượng (Thermal & Battery Stats):** Giám sát mức tiêu hao pin thiết bị qua `batterystats` (`battery.txt`) và kiểm soát hiện tượng nghẽn nhiệt qua `thermal.txt`.
* **Thực nghiệm biên dịch AOT (ART Compilation):** Thử nghiệm và đánh giá tốc độ xử lý khi biên dịch trước toàn bộ mã máy qua lệnh `adb shell cmd package compile -m speed -f com.thupo` so với chế độ `verify`.

---

## 🚀 Hướng dẫn cài đặt & Chạy ứng dụng (Getting Started)

### Yêu cầu môi trường:
* **Android Studio:** Ladybug / Meerkat / Koala hoặc phiên bản mới hơn.
* **JDK:** Java Development Kit 11, 17 hoặc 21.
* **Android SDK:**
  * `minSdk`: **26** (Android 8.0 Oreo)
  * `targetSdk`: **36** (Android 16+)
  * `compileSdk`: **36**

### Các bước cài đặt:

1. **Clone repository về máy:**
   ```bash
   git clone https://github.com/DoAnhThu832004/StickerMaker.git
   ```

2. **Mở dự án trong Android Studio:**
   * Mở Android Studio, chọn **File** $\rightarrow$ **Open...** và duyệt tới thư mục `StickerMaker`.
   * Chờ đợi Android Studio hoàn tất quá trình đồng bộ Gradle (**Gradle Sync**) và tải về các dependencies cần thiết.

3. **Build & Run:**
   * Kết nối thiết bị Android thật (đã bật chế độ *USB Debugging*) hoặc khởi chạy Máy ảo Android (Android Emulator 8.0+).
   * Nhấn nút **Run ▶️** (hoặc tổ hợp phím `Shift + F10` trên Windows / `Control + R` trên macOS).

   * Hoặc biên dịch file APK Debug thông qua Terminal:
     * Trên **Windows PowerShell:**
       ```powershell
       .\gradlew.bat assembleDebug
       ```
     * Trên **macOS / Linux:**
       ```bash
       ./gradlew assembleDebug
       ```
     * File APK hoàn chỉnh sẽ nằm tại: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🔒 Quyền hạn & Tích hợp hệ thống (Permissions & Privacy)

Ứng dụng hướng tới tiêu chuẩn bảo vệ quyền riêng tư người dùng cao nhất:

* **Tích hợp WhatsApp (`<queries>`):**
  Khai báo gói `com.whatsapp` và `com.whatsapp.w4b` trong `AndroidManifest.xml` nhằm tuân thủ chính sách hiển thị gói ứng dụng (*Package Visibility*) của Android 11+ (API 30+), giúp kiểm tra tính sẵn sàng của WhatsApp trên thiết bị trước khi thêm nhãn dán.
* **Nhà cung cấp nội dung nhãn dán (`StickerContentProvider`):**
  Cung cấp quyền truy cập an toàn cho ứng dụng WhatsApp với `android:grantUriPermissions="true"` và giới hạn quyền đọc `readPermission="com.whatsapp.sticker.READ"`.
* **Trình chọn ảnh an toàn:**
  Sử dụng chuẩn `ActivityResultContracts.GetContent()` của hệ điều hành, không yêu cầu cấp quyền đọc bộ nhớ nguy hiểm (`READ_EXTERNAL_STORAGE`), bảo mật tối đa dữ liệu hình ảnh người dùng.

---

## 🤝 Đóng góp (Contributing)

Mọi ý kiến đóng góp, báo cáo sự cố (issues) hoặc pull request nhằm hoàn thiện ứng dụng đều luôn được trân trọng và hoan nghênh:

1. Fork repository này về tài khoản của bạn.
2. Tạo nhánh tính năng mới (`git checkout -b feature/AmazingStickerFeature`).
3. Commit các thay đổi (`git commit -m 'Add some AmazingStickerFeature'`).
4. Push lên nhánh của bạn (`git push origin feature/AmazingStickerFeature`).
5. Tạo một **Pull Request** trên GitHub để cùng thảo luận.

---

## 👤 Tác giả (Author)

* **DoAnhThu832004** - [GitHub Profile](https://github.com/DoAnhThu832004)
* **Email liên hệ:** [thuanhdo83@gmail.com](mailto:thuanhdo83@gmail.com)
* **Repository:** [StickerMaker](https://github.com/DoAnhThu832004/StickerMaker)

---

⭐ **Đừng quên nhấn tặng 1 Star cho dự án nếu bạn thấy ứng dụng hữu ích và thú vị nhé!** ⭐
