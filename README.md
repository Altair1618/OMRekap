<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://gitlab.informatika.org/k-02-09/omrekap.git">
    <img src="screenshots/icon_launcher.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">OMRekap</h3>

  <p align="center">
    Aplikasi Rekapitulasi Pemilihan Umum 
    <br />
    <a href="https://drive.google.com/file/d/1Z7-iwjspu0tJr2c9R2ZTLW68enhBc1zD/view?usp=drive_link"><strong>Explore the docs »</strong></a>
    <br />
  </p>
</div>

[![forthebadge](https://forthebadge.com/images/badges/made-with-kotlin.svg)](https://forthebadge.com) [![forthebadge](https://forthebadge.com/images/badges/built-for-android.svg)](https://forthebadge.com) [![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://forthebadge.com)
<br />
[![Android](https://img.shields.io/badge/Android-%233DDC84.svg?&style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-%230095D5.svg?&style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/) [![Gradle](https://img.shields.io/badge/Gradle-%2302303A.svg?&style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/) [![OpenCV](https://img.shields.io/badge/OpenCV-%23opencv.svg?&style=for-the-badge&logo=opencv&logoColor=white)](https://opencv.org/)

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#features">Features</a>
    </li>
    <li><a href="#how-to-use">How To Use</a></li>
    <li><a href="#development">Development</a></li>
    <li><a href="#contributors">Contributors</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>


# About The Project
OMRekap adalah aplikasi rekapitulasi pemilihan umum yang dibuat untuk memudahkan proses rekapitulasi suara pada pemilihan umum.
Aplikasi ini dibuat menggunakan bahasa pemrograman Kotlin dan menggunakan OpenCV untuk mendeteksi dan mengenali lembar suara.
![Application](screenshots/screenshot.png)


# Features
1. Mengambil foto kertas plano menggunakan kamera 📷 atau menggunakan galeri 🖼
2. Melihat hasil foto yang diambil pada halaman berikutnya
3. Mendeteksi dan mengenali lembar suara
4. Menampilkan hasil rekapitulasi suara
5. Menyimpan hasil rekapitulasi suara dalam bentuk foto 🖼 dan JSON 📃


# How To Use
1. Navigasi ke folder apk
2. Unduh file apk lalu pindahkan ke perangkat android
3. Buka file apk di perangkat android
4. Install aplikasi
5. Buka aplikasi

Alternatif lain untuk menjalankan aplikasi:
1. Clone repository https://gitlab.informatika.org/k-02-09/omrekap.git
2. Buka project menggunakan Android Studio
3. Run aplikasi menggunakan emulator atau perangkat android

# Development
### Clone the repository
```bash
git clone https://gitlab.informatika.org/k-02-09/omrekap
```

### Run Unit Test
```bash
./gradlew test
```

### Code Formatting
```bash
./gradlew spotlessApply
```

### Changing Formatting Configuration
* Update spotless.gradle based on Ktlint rules [here](https://pinterest.github.io/ktlint/0.50.0/rules/configuration-ktlint/)
* Clean gradle cache
```bash
./gradlew clean 
```

# Contributors
<table>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://avatars.githubusercontent.com/u/91373980?v=4"><img src="https://github.com/Altair1618" width="100px;" alt="Farhan Nabil Suryono"/><br /><sub><b>Farhan Nabil Suryono</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://avatars.githubusercontent.com/u/89065724?v=4"><img src="https://github.com/Enliven26" width="100px;" alt="Johanes Lee"/><br /><sub><b>Johanes Lee</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://avatars.githubusercontent.com/u/110590843?v=4"><img src="https://github.com/dhanikanovlisa" width="100px;" alt="Dhanika Novlisariyanti"/><br /><sub><b>Dhanika Novlisariyanti</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://avatars.githubusercontent.com/u/92362538?v=4"><img src="https://github.com/Genvictus" width="100px;" alt="Johann Christian Kandani"/><br /><sub><b>Johann Christian Kandani</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://avatars.githubusercontent.com/u/91373980?v=4"><img src="https://github.com/Michaelu670" width="100px;" alt="Michael Utama"/><br /><sub><b>Michael Utama</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
  </tbody>
</table>

# Acknowledgments
* [OpenCV](https://opencv.org/)
* [Kotlin](https://kotlinlang.org/)
* [Android](https://developer.android.com/)
