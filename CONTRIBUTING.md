[🇬🇧 English](#english) | [🇹🇷 Türkçe](#turkish)

---

<a id="english"></a>

# Contributing to OleAI Android

First off, thank you for considering contributing to OleAI! Contributions like yours help open-source projects grow and improve the AI experience for Android users.

This document outlines the guidelines and workflows for contributing to this repository.

## Code of Conduct

By participating in this project, you are expected to help maintain a welcoming and respectful environment. Please be kind, collaborative, and constructive in all interactions.

## How Can I Contribute?

### Reporting Bugs

Before creating a bug report, check the [Issue Tracker](../../issues) to avoid duplicates. When reporting a bug, please include as much detail as possible:

- **Use a clear and descriptive title.**
- **Describe the exact steps to reproduce the problem.**
- **Provide specific examples**, such as screenshots, screen recordings, or Logcat output.
- **Describe the behavior you observed** and explain what is wrong with it.
- **Explain the expected behavior** and why you expected it.
- **Include environment details**, such as the Android Studio version, operating system, physical device or emulator, and Android API level.

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement issue:

- **Use a clear and descriptive title.**
- **Provide a step-by-step description** of the proposed enhancement.
- **Explain why the enhancement would be useful** to OleAI users.

---

## Development Workflow

Follow these steps when contributing code.

### 1. Fork and Clone

1. Fork the repository to your GitHub account.
2. Clone your fork to your local machine:

   ```bash
   git clone https://github.com/onuraydi/OleAI-android.git
   ```

3. Add the original repository as an upstream remote:

   ```bash
   git remote add upstream https://github.com/onuraydi/OleAI-android.git
   ```

### 2. Create a Branch

Always create a new branch for your work. **Do not commit directly to the `main` branch.**

Use the following branch naming conventions:

- `feat/` — New features (e.g., `feat/dark-mode-support`)
- `fix/` — Bug fixes (e.g., `fix/login-crash`)
- `docs/` — Documentation changes
- `refactor/` — Code refactoring

Create your branch with:

```bash
git checkout -b feat/your-feature-name
```

### 3. Commit Standards

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification to keep commit messages consistent and the project history easy to understand.

**Format:**

`<type>[optional scope]: <description>`

**Examples:**

- `feat: add voice recognition UI`
- `fix(network): handle timeout exception on API calls`
- `docs: update README with new setup instructions`
- `style: format MainActivity.kt according to ktlint`

### 4. Coding Guidelines

To maintain consistency across the codebase, please follow these guidelines:

- **Kotlin:** Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).
- **Architecture:** Follow the project's MVVM (Model-View-ViewModel) and Clean Architecture principles. Maintain a clear separation of concerns and avoid placing business logic in UI components.
- **UI:** Use **Jetpack Compose**. Prefer stateless composables where possible and ensure compatibility with both light and dark themes.
- **Linting:** Run the available lint checks and resolve relevant issues before committing.

### 5. Testing

- Write unit tests for new business logic and ViewModels in `app/src/test`.
- Write UI tests for new Compose components in `app/src/androidTest`, where appropriate.
- Ensure that all existing tests pass before submitting a pull request.

### 6. Submit a Pull Request

1. Fetch the latest changes from the upstream repository and rebase your branch:

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. Push your branch to your fork:

   ```bash
   git push origin your-branch-name
   ```

3. Open a pull request against the `main` branch of the original repository.
4. Complete the pull request template.
5. Link any relevant issues (e.g., `Closes #42`).
6. Request a review from the maintainers.

## Code Review

Once your pull request is open, maintainers will review your changes. They may request modifications or clarifications, so please respond to feedback constructively.

Once your pull request is approved, a maintainer will merge it into the `main` branch.

Thank you for contributing to OleAI Android! 🎉

---

<a id="turkish"></a>

# OleAI Android'a Katkıda Bulunma

Öncelikle OleAI projesine katkıda bulunmayı düşündüğünüz için teşekkür ederiz! Sizin gibi katkıda bulunan kişiler, açık kaynak projelerinin gelişmesine ve Android kullanıcıları için daha iyi bir yapay zekâ deneyimi sunulmasına yardımcı olur.

Bu belge, depoya katkıda bulunurken izlenmesi gereken kuralları ve geliştirme iş akışını açıklar.

## Davranış Kuralları

Projeye katılan herkesin saygılı ve kapsayıcı bir ortamın korunmasına katkıda bulunması beklenir. İletişiminizde nazik, iş birliğine açık ve yapıcı olun.

## Nasıl Katkıda Bulunabilirsiniz?

### Hata Bildirme

Tekrarlanan bildirimleri önlemek için hata raporu oluşturmadan önce [Issue Tracker](../../issues) bölümünü kontrol edin. Hata bildiriminizde mümkün olduğunca fazla ayrıntı paylaşın:

- **Açık ve açıklayıcı bir başlık kullanın.**
- **Sorunu yeniden oluşturmak için gereken adımları** eksiksiz açıklayın.
- Ekran görüntüsü, ekran kaydı veya Logcat çıktısı gibi **somut örnekler paylaşın.**
- Gözlemlediğiniz davranışı açıklayın ve bu davranışın hangi yönünün sorun olduğunu belirtin.
- Beklediğiniz davranışı ve bunun neden beklendiğini açıklayın.
- Android Studio sürümü, işletim sistemi, fiziksel cihaz veya emülatör ve Android API seviyesi gibi **ortam bilgilerini ekleyin.**

### Geliştirme Önerileri

Yeni özellik ve iyileştirme önerileri GitHub Issues üzerinden takip edilir. Bir iyileştirme önerisi oluştururken:

- **Açık ve açıklayıcı bir başlık kullanın.**
- Önerdiğiniz iyileştirmeyi adım adım açıklayın.
- Bu iyileştirmenin OleAI kullanıcıları için neden faydalı olacağını belirtin.

---

## Geliştirme İş Akışı

Kod katkısında bulunurken aşağıdaki adımları izleyin.

### 1. Fork ve Clone

1. Depoyu kendi GitHub hesabınıza fork edin.
2. Fork ettiğiniz depoyu yerel bilgisayarınıza klonlayın:

   ```bash
   git clone https://github.com/onuraydi/OleAI-android.git
   ```

3. Orijinal depoyu `upstream` uzak deposu olarak ekleyin:

   ```bash
   git remote add upstream https://github.com/onuraydi/OleAI-android.git
   ```

### 2. Branch Oluşturma

Çalışmanız için her zaman yeni bir branch oluşturun. **Doğrudan `main` branch'ine commit atmayın.**

Aşağıdaki branch isimlendirme kurallarını kullanın:

- `feat/` — Yeni özellikler (ör. `feat/dark-mode-support`)
- `fix/` — Hata düzeltmeleri (ör. `fix/login-crash`)
- `docs/` — Dokümantasyon değişiklikleri
- `refactor/` — Kodun yeniden düzenlenmesi

Branch oluşturmak için:

```bash
git checkout -b feat/ozellik-adi
```

### 3. Commit Standartları

Commit mesajlarının tutarlı ve proje geçmişinin anlaşılır olması için [Conventional Commits](https://www.conventionalcommits.org/) standardını kullanıyoruz.

**Format:**

`<type>[isteğe bağlı kapsam]: <açıklama>`

**Örnekler:**

- `feat: add voice recognition UI`
- `fix(network): handle timeout exception on API calls`
- `docs: update README with new setup instructions`
- `style: format MainActivity.kt according to ktlint`

### 4. Kodlama Kuralları

Kod tabanında tutarlılığı korumak için aşağıdaki kurallara uyun:

- **Kotlin:** Resmî [Kotlin Kodlama Kuralları](https://kotlinlang.org/docs/coding-conventions.html) yönergelerini izleyin.
- **Mimari:** Projede kullanılan MVVM (Model-View-ViewModel) ve Clean Architecture prensiplerine uyun. Sorumlulukların ayrımını koruyun ve iş mantığını UI bileşenlerine yerleştirmeyin.
- **Arayüz:** **Jetpack Compose** kullanın. Mümkün olduğunda stateless composable bileşenleri tercih edin ve hem açık hem de koyu temalarla uyumluluğu gözetin.
- **Lint kontrolleri:** Commit atmadan önce mevcut lint kontrollerini çalıştırın ve ilgili sorunları giderin.

### 5. Testler

- Yeni iş mantığı ve ViewModel bileşenleri için `app/src/test` altında birim testleri yazın.
- Uygun durumlarda yeni Compose bileşenleri için `app/src/androidTest` altında UI testleri yazın.
- Pull request göndermeden önce mevcut tüm testlerin başarıyla çalıştığından emin olun.

### 6. Pull Request Gönderme

1. Orijinal depodaki güncel değişiklikleri alın ve branch'inizi rebase edin:

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. Branch'inizi kendi fork'unuza gönderin:

   ```bash
   git push origin branch-adi
   ```

3. Orijinal deponun `main` branch'ini hedefleyen bir pull request açın.
4. Pull request şablonunu eksiksiz doldurun.
5. İlgili issue kayıtlarını bağlayın (ör. `Closes #42`).
6. Proje bakım sorumlularından (maintainers) inceleme isteyin.

## Kod İncelemesi

Pull request açıldıktan sonra bakım sorumluları değişikliklerinizi inceleyecektir. Değişiklik veya açıklama talep edilebilir; geri bildirimlere yapıcı bir şekilde yanıt verin.

Pull request'iniz onaylandıktan sonra bir bakım sorumlusu değişikliklerinizi `main` branch'ine birleştirecektir.

OleAI Android projesine katkıda bulunduğunuz için teşekkür ederiz! 🎉
