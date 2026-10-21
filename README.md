[🇬🇧 English](#english) | [🇹🇷 Türkçe](#turkish)

---

<a id="english"></a>

# OleAI - Native Android Client

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg?logo=kotlin)
![Android API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?logo=android)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Modern%20UI-success)
![License](https://img.shields.io/badge/License-MIT-purple.svg)

OleAI is a modern Android application designed to provide an intuitive and powerful mobile experience for AI-powered recipe recommendations and pantry management. Built with modern Android development technologies, OleAI aims to deliver a responsive, maintainable, and user-friendly experience.

## About the Project

The OleAI Android client provides a native mobile interface for the OleAI ecosystem. It is designed to help users interact with recipe-related features and access application capabilities through a modern Android experience.

Our goal is to build a robust, scalable, and easy-to-navigate application by following Clean Architecture principles and modern Android UI guidelines.

### Key Features

- **Modern UI/UX:** Built with **Jetpack Compose** and Material Design 3 to support modern UI patterns, dynamic styling, and Dark/Light theme support where configured.
- **Robust Architecture:** Uses the MVVM (Model-View-ViewModel) architectural pattern to separate UI responsibilities from application logic and improve testability.
- **Asynchronous Processing:** Uses Kotlin Coroutines and Flows for asynchronous operations and reactive data streams.
- **Native Android Experience:** Designed specifically for Android devices using native development technologies.
- **Secure Configuration:** Sensitive configuration values should be managed outside the source code, following secure development practices.

## Built With & Tech Stack

This project uses the modern Android development stack:

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM and Clean Architecture principles
- **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)
- **Dependency Injection:** Follow the dependency injection framework configured in the project, if applicable.
- **Networking:** Follow the HTTP client and networking implementation configured in the project.
- **Build System:** [Gradle](https://gradle.org/) with Kotlin DSL (`.kts`) and Version Catalogs where configured.

## Getting Started

Follow these instructions to set up the project locally for development and testing.

### Prerequisites

- **Android Studio:** A stable version compatible with the project's Gradle and Android Gradle Plugin configuration.
- **JDK:** Java Development Kit 17, if required by the project configuration.
- **Android SDK:** The SDK platforms and build tools required by the project.
- **Git:** To clone and manage the repository.

### Local Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/onuraydi/OleAI-android.git
cd OleAI-android
```

#### 2. Open the Project in Android Studio

1. Launch Android Studio.
2. Select **Open**.
3. Navigate to the cloned repository and select the project directory.
4. Allow Android Studio to import the project.

#### 3. Configure Environment Variables and API Keys

If the application requires API credentials for its AI features, configure them according to the project's actual implementation.

- Never hardcode API keys or secrets in source files.
- Use `local.properties` only if the project's Gradle configuration supports reading the required values from that file.
- Keep local configuration files containing secrets out of version control.
- Verify the expected configuration property names and base URLs in the project's source code.

Example configuration, **only if supported by the project**:

```properties
# local.properties — DO NOT COMMIT THIS FILE
OLEAI_API_KEY=your_secret_api_key_here
BASE_URL=https://api.example.com/v1/
```

> **Security note:** Values embedded in an Android application can potentially be extracted from the application package. Do not rely on a client-side API key as the sole protection for privileged services. Sensitive third-party credentials should generally be kept on a trusted backend.

#### 4. Sync and Build

1. Select **File > Sync Project with Gradle Files**.
2. Wait for Gradle synchronization to complete.
3. Resolve any dependency or SDK configuration errors before running the application.

#### 5. Run the Application

1. Select an Android emulator or connect a physical Android device with USB debugging enabled.
2. Select the appropriate run configuration.
3. Click **Run** in Android Studio.

## Contributing

Contributions are welcome! If you have an idea for an improvement, a bug fix, or a new feature, please follow the project's contribution guidelines.

1. Fork the repository.
2. Create a feature branch:

   ```bash
   git checkout -b feature/AmazingFeature
   ```

3. Commit your changes using a descriptive commit message:

   ```bash
   git commit -m "feat: add recipe recommendation screen"
   ```

4. Push your branch:

   ```bash
   git push origin feature/AmazingFeature
   ```

5. Open a Pull Request (PR) against the appropriate branch.

Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) for detailed development and Pull Request guidelines.

## License

This project is distributed under the MIT License. See the `LICENSE` file for more details.

---

<a id="turkish"></a>

# OleAI - Yerel Android Uygulaması

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg?logo=kotlin)
![Android API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?logo=android)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Modern%20UI-success)
![License](https://img.shields.io/badge/License-MIT-purple.svg)

OleAI, yapay zekâ destekli tarif önerileri ve kiler yönetimi için sezgisel ve güçlü bir mobil deneyim sunmayı amaçlayan modern bir Android uygulamasıdır. Modern Android geliştirme teknolojileriyle oluşturulan OleAI, duyarlı, sürdürülebilir ve kullanıcı dostu bir deneyim sunmayı hedefler.

## Proje Hakkında

OleAI Android istemcisi, OleAI ekosistemi için yerel bir mobil arayüz sağlar. Kullanıcıların tariflerle ilgili özelliklerle etkileşim kurmasını ve uygulama yeteneklerine modern bir Android deneyimi üzerinden erişmesini sağlamak amacıyla tasarlanmıştır.

Amacımız, Clean Architecture prensiplerini ve modern Android arayüz yönergelerini izleyerek sağlam, ölçeklenebilir ve kolay kullanılabilir bir uygulama geliştirmektir.

### Temel Özellikler

- **Modern UI/UX:** Modern arayüz yaklaşımlarını, dinamik stillendirmeyi ve yapılandırıldığı durumlarda Koyu/Açık tema desteğini sunmak için **Jetpack Compose** ve Material Design 3 kullanır.
- **Sağlam Mimari:** Arayüz sorumluluklarını uygulama mantığından ayırmak ve test edilebilirliği artırmak için MVVM (Model-View-ViewModel) mimari desenini kullanır.
- **Asenkron İşlemler:** Asenkron işlemler ve reaktif veri akışları için Kotlin Coroutines ve Flow kullanır.
- **Yerel Android Deneyimi:** Yerel Android geliştirme teknolojileri kullanılarak Android cihazlar için tasarlanmıştır.
- **Güvenli Yapılandırma:** Hassas yapılandırma değerleri, güvenli geliştirme uygulamalarına uygun biçimde kaynak kodun dışında tutulmalıdır.

## Kullanılan Teknolojiler (Tech Stack)

Bu proje modern Android geliştirme teknolojilerinden yararlanır:

- **Programlama Dili:** [Kotlin](https://kotlinlang.org/)
- **Arayüz Araç Seti:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Mimari:** MVVM ve Clean Architecture prensipleri
- **Asenkron Programlama:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) ve [Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)
- **Bağımlılık Enjeksiyonu:** Kullanılıyorsa projede yapılandırılmış bağımlılık enjeksiyonu altyapısını izleyin.
- **Ağ İletişimi:** Projede yapılandırılmış HTTP istemcisini ve ağ iletişimi yaklaşımını izleyin.
- **Derleme Sistemi:** Yapılandırıldığı durumlarda Kotlin DSL (`.kts`) ve Version Catalogs ile [Gradle](https://gradle.org/).

## Başlarken

Geliştirme ve test amacıyla projeyi yerel ortamınızda kurmak için aşağıdaki adımları izleyin.

### Gereksinimler

- **Android Studio:** Projenin Gradle ve Android Gradle Plugin yapılandırmasıyla uyumlu, kararlı bir sürüm.
- **JDK:** Proje yapılandırması gerektiriyorsa Java Development Kit 17.
- **Android SDK:** Projenin ihtiyaç duyduğu SDK platformları ve derleme araçları.
- **Git:** Depoyu klonlamak ve yönetmek için.

### Yerel Kurulum

#### 1. Depoyu Klonlama

```bash
git clone https://github.com/onuraydi/OleAI-android.git
cd OleAI-android
```


#### 2. Projeyi Android Studio'da Açma

1. Android Studio'yu başlatın.
2. **Open** seçeneğini seçin.
3. Klonladığınız depoya gidin ve proje dizinini seçin.
4. Android Studio'nun projeyi içe aktarmasını bekleyin.

#### 3. Ortam Değişkenlerini ve API Anahtarlarını Yapılandırma

Uygulamanın yapay zekâ özellikleri için API kimlik bilgileri gerekiyorsa bunları projenin gerçek uygulama biçimine uygun şekilde yapılandırın.

- API anahtarlarını veya gizli bilgileri kaynak koduna doğrudan yazmayın.
- `local.properties` dosyasını yalnızca projenin Gradle yapılandırması gerekli değerleri bu dosyadan okuyacak şekilde hazırlanmışsa kullanın.
- Gizli bilgiler içeren yerel yapılandırma dosyalarını sürüm kontrolünün dışında tutun.
- Beklenen yapılandırma anahtarlarını ve temel URL'leri doğrulamak için projenin kaynak kodunu inceleyin.

Aşağıdaki örneği **yalnızca proje tarafından destekleniyorsa** kullanın:

```properties
# local.properties — BU DOSYAYI COMMIT ETMEYİN
OLEAI_API_KEY=your_secret_api_key_here
BASE_URL=https://api.example.com/v1/
```

> **Güvenlik notu:** Android uygulamasına gömülen değerler uygulama paketi üzerinden çıkarılabilir. Ayrıcalıklı servisleri korumak için yalnızca istemci tarafındaki bir API anahtarına güvenmeyin. Üçüncü taraf servislere ait hassas kimlik bilgileri genel olarak güvenilir bir backend üzerinde tutulmalıdır.

#### 4. Senkronizasyon ve Derleme

1. **File > Sync Project with Gradle Files** seçeneğini kullanın.
2. Gradle senkronizasyonunun tamamlanmasını bekleyin.
3. Uygulamayı çalıştırmadan önce bağımlılık veya SDK yapılandırması kaynaklı hataları giderin.

#### 5. Uygulamayı Çalıştırma

1. Bir Android emülatörü seçin veya USB hata ayıklaması etkinleştirilmiş fiziksel bir Android cihaz bağlayın.
2. Uygun çalıştırma yapılandırmasını seçin.
3. Android Studio'daki **Run** düğmesine tıklayın.

## Katkıda Bulunma

Katkılarınızı bekliyoruz! Bir iyileştirme, hata düzeltmesi veya yeni özellik öneriniz varsa projenin katkıda bulunma yönergelerini izleyin.

1. Depoyu fork edin.
2. Bir özellik branch'i oluşturun:

   ```bash
   git checkout -b feature/AmazingFeature
   ```

3. Açıklayıcı bir commit mesajı kullanarak değişikliklerinizi kaydedin:

   ```bash
   git commit -m "feat: add recipe recommendation screen"
   ```

4. Branch'inizi uzak depoya gönderin:

   ```bash
   git push origin feature/AmazingFeature
   ```

5. Uygun branch'e karşı bir Pull Request (PR) açın.

Geliştirme ve Pull Request yönergelerinin ayrıntıları için [`CONTRIBUTING.md`](CONTRIBUTING.md) dosyasını inceleyin.

## Lisans

Bu proje MIT Lisansı kapsamında dağıtılmaktadır. Ayrıntılar için `LICENSE` dosyasına bakın.
