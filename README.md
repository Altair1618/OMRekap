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
    <a href="https://drive.google.com/file/d/17xJabhFr3tFBLDdku4rONEyfcQLBBodp/view?usp=sharing"><strong>Explore the docs »</strong></a>
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
1. Akses  halaman https://bit.ly/OMRekap  menggunakan browser.
2. Terdapat 2 folder pada drive tersebut. Anda dapat memilih menggunakan Universal Build dengn size .apk lebih besar atau memilih Slim Build dengan ukuran lebih kecil sesuai dengan arsitektur prosesor Anda.
3. Unduh file .apk yang tersedia di dalam folder tersebut ke perangkat Android.
4. Lakukan instalasi file .apk tersebut pada perangkat Android.

Sebagai alternatif, langkah-langkah berikut juga dapat dilakukan dalam instalasi aplikasi ini.
1. Akses halaman https://gitlab.informatika.org/k-02-09/omrekap.git  
2. Unduh source code berupa .zip atau .rar dari rilis tersebut.
3. Buka proyek tersebut menggunakan Android Studio.
4. Lakukan build dan jalankan aplikasi.

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
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Altair16181"><img src="https://avatars.githubusercontent.com/u/91373980?v=4" width="100px;" alt="Farhan Nabil Suryono"/><br /><sub><b>Farhan Nabil Suryono</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Enliven26"><img src="https://avatars.githubusercontent.com/u/89065724?v=4" width="100px;" alt="Johanes Lee"/><br /><sub><b>Johanes Lee</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/dhanikanovlisa"><img src="https://avatars.githubusercontent.com/u/110590843?v=4" width="100px;" alt="Dhanika Novlisariyanti"/><br /><sub><b>Dhanika Novlisariyanti</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Genvictus"><img src="https://avatars.githubusercontent.com/u/92362538?v=4" width="100px;" alt="Johann Christian Kandani"/><br /><sub><b>Johann Christian Kandani</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Michaelu670"><img src="https://avatars.githubusercontent.com/u/91373980?v=4" width="100px;" alt="Michael Utama"/><br /><sub><b>Michael Utama</b></sub></a><br /><a href="https://github.com/codesandbox/codesandbox-client/issues?q=author%3ACompuIves" title="Bug reports">🐛</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Code">💻</a> <a href="#design-CompuIves" title="Design">🎨</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Documentation">📖</a> <a href="#infra-CompuIves" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/codesandbox/codesandbox-client/pulls?q=is%3Apr+reviewed-by%3ACompuIves" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/codesandbox/codesandbox-client/commits?author=CompuIves" title="Tests">⚠️</a> <a href="#tool-CompuIves" title="Tools">🔧</a></td>
  </tbody>
</table>

# Acknowledgments
* [OpenCV](https://opencv.org/)
* [Kotlin](https://kotlinlang.org/)
* [Android](https://developer.android.com/)
