# TRM Game & Interactive Media Showcase

Koleksi portofolio karya game interaktif dan aset digital 3D yang dikembangkan oleh mahasiswa program studi Teknologi Rekayasa Multimedia (TRM). Repositori ini berfungsi sebagai etalase karya, dokumentasi teknis, serta pusat distribusi biner mandiri untuk kebutuhan pameran akademik, demonstrasi stan, dan orientasi mahasiswa (OSPEK).

---

## Katalog Karya

| Nama Proyek | Tipe / Platform | Teknologi / Bahasa | Status | Format Distribusi |
|---|---|---|---|---|
| PlatformMazeGame | Desktop (Windows / Linux / macOS) | Java, Swing, Maven | Stabil | Kode Sumber & Eksekutabel Mandiri (.jar) |
| Souls2D | Desktop (Windows / Linux / macOS) | Java, Swing | Stabil | Kode Sumber & Eksekutabel Mandiri (.jar) |
| Nerrow | Desktop (Windows x64) | Unity Engine, C# | Stabil | Paket Mandiri (.zip) & Biner Langsung (.exe) |
| Clumsy Thief | Mobile (Android) | Android SDK, Java/Kotlin | Stabil | Paket Aplikasi Android (.apk) |
| Lingkungan Rumah | Model & Aset 3D | Blender | Selesai | File Sumber Produksi (.blend) |

---

## Unduhan Berkas dan Eksekutabel Siap Pakai

Seluruh paket hasil kompilasi, biner eksekutabel, aplikasi siap instal, serta aset 3D berukuran penuh telah diunggah dan dapat diunduh langsung melalui halaman GitHub Releases resmi:

- Tautan Rilis Resmi: [v1.0.0 - Rilis Pameran Karya](https://github.com/muadzhdz/trm-game-showcase/releases/tag/v1.0.0)

### Rincian Berkas Rilis

1. PlatformMazeGame.jar
   - Jenis: Java Runnable Archive
   - Prasyarat: Java Runtime Environment (JRE) atau Java Development Kit (JDK) versi 17 atau yang lebih baru.
   - Cara Menjalankan: Klik ganda berkas atau gunakan terminal:
     ```bash
     java -jar PlatformMazeGame.jar
     ```

2. Souls2D.jar
   - Jenis: Java Runnable Archive
   - Prasyarat: Java Runtime Environment (JRE) atau Java Development Kit (JDK) versi 17 atau yang lebih baru.
   - Cara Menjalankan: Klik ganda berkas atau gunakan terminal:
     ```bash
     java -jar Souls2D.jar
     ```

3. NerrowBuild-Windows-x64.zip
   - Jenis: Paket Lengkap Standalone Unity Windows 64-bit
   - Prasyarat: Windows 10/11 64-bit dengan dukungan DirectX 11/12.
   - Cara Menjalankan: Ekstrak arsip zip, kemudian jalankan `nerroww.exe`.

4. nerroww.exe
   - Jenis: Biner Langsung Klien Game Nerrow
   - Catatan: Menjalankan berkas ini secara terpisah membutuhkan keberadaan pustaka `UnityPlayer.dll` dan folder data terkait pada direktori kerja.

5. Clumsy-Thief.apk
   - Jenis: Paket Aplikasi Android (APK)
   - Prasyarat: Perangkat Android versi 8.0 ke atas atau emulator Android.
   - Cara Menjalankan: Lakukan instalasi langsung pada ponsel/tablet demonstrasi di stan.

6. Rumah.blend
   - Jenis: File Proyek Aset 3D Lingkungan
   - Prasyarat: Blender versi 3.6 LTS atau Blender versi 4.x ke atas.
   - Deskripsi: Model arsitektur rumah lengkap beserta hierarki objek, material, dan tata cahaya.

---

## Informasi Permainan dan Panduan Kontrol

### 1. PlatformMazeGame

Game puzzle labirin berbasis grid dua dimensi yang mengedepankan sinkronisasi arah dan ketepatan navigasi.

- Mekanisme Permainan:
  - Navigasikan karakter melintasi labirin ubin (tile-based).
  - Ambil semua kristal diamond yang tersebar di sepanjang lintasan.
  - Capai pintu keluar (Exit Door) untuk membuka stage berikutnya.
- Skema Kontrol:
  - Gerak Horizontal (Kiri / Kanan): Tombol `A` dan `D`
  - Gerak Vertikal (Atas / Bawah): Tombol Panah `UP` dan `DOWN`
- Lokasi Kode Sumber: Direktori `PlatformMazeGame/` (struktur Maven)

### 2. Souls2D

Game petualangan horor bertema penjelajahan ruang bawah tanah gelap dengan sistem kecerdasan buatan musuh bayangan dan ritual altar.

- Mekanisme Permainan:
  - Hindari patroli musuh bayangan (Shadow Enemy) yang menjelajahi lorong dan ruangan.
  - Temukan dan kumpulkan serpihan Soul yang tersembunyi pada tiap ruangan.
  - Letakkan Soul ke atas Altar yang sesuai untuk menyalakan energi lentera.
  - Buka pintu utama (Soul Door) setelah ritual selesai untuk menyelesaikan permainan.
- Skema Kontrol:
  - Pergerakan Karakter: Tombol `W`, `A`, `S`, `D` atau `Tombol Panah`
  - Interaksi / Pengambilan Aset / Altar: Tombol `E`
  - Jeda / Navigasi Menu: Tombol `ESC` / `ENTER`
- Lokasi Kode Sumber: Direktori `Souls2D/`

### 3. Nerrow

Game eksplorasi horor berbasis sudut pandang orang pertama (first-person) yang dibangun menggunakan Unity Engine.

- Mekanisme Permainan:
  - Menelusuri lorong interior dalam kondisi pencahayaan minim.
  - Interaksi fisik dengan objek lingkungan sekitar untuk memecahkan teka-teki.
- Skema Kontrol:
  - Navigasi Gerak: Tombol `W`, `A`, `S`, `D`
  - Orientasi Sudut Pandang: Gerakan Mouse
  - Interaksi Objek: Klik Kiri Mouse / Tombol `E`

### 4. Clumsy Thief

Game kasual mobile bertema manuver rintangan dengan respon refleks cepat.

- Platform Target: Android (dukungan arsitektur ARM64 dan ARMv7)
- Skema Kontrol: Input sentuh pada layar

### 5. Aset 3D Rumah

Karya pemodelan 3D berstandar industri multimedia yang dirancang untuk integrasi simulasi atau game engine.

- Kompatibilitas: Blender, Unity, Unreal Engine (ekspor FBX/glTF)
- Komponen: Mesh beresolusi terukur, material shading, dan tata letak interior-eksterior.

---

## Kompilasi dari Kode Sumber

### Kompilasi PlatformMazeGame (Maven)

Pastikan lingkungan Java 17+ dan Apache Maven telah terpasang pada sistem:

```bash
cd PlatformMazeGame
mvn clean package
java -jar target/PlatformMazeGame-1.0-SNAPSHOT.jar
```

### Kompilasi Souls2D (JDK javac)

Pastikan JDK 17+ telah terpasang pada sistem:

```bash
cd Souls2D
mkdir -p bin
javac -d bin $(find src -name "*.java")
jar cfe Souls2D.jar main.GameMain -C bin .
java -jar Souls2D.jar
```

---

## Panduan Pengoperasian Stand Pameran OSPEK

Petunjuk praktis bagi tim penjaga stan dan demonstrator karya:

1. Kesiapan Lingkungan Java:
   Verifikasi instalasi Java pada laptop atau komputer demonstrasi menggunakan perintah:
   ```bash
   java -version
   ```
2. Akses Cepat di Meja Demo:
   Letakkan berkas `PlatformMazeGame.jar` dan `Souls2D.jar` langsung pada Desktop komputer demonstrasi. Pengunjung stan dapat langsung memainkannya cukup dengan klik ganda.
3. Kios Permainan Mobile:
   Instal berkas `Clumsy-Thief.apk` pada tablet pameran yang telah diatur dalam mode layar terkunci (kiosk demo).
4. Terminal Windows:
   Buka folder hasil ekstraksi `NerrowBuild-Windows-x64.zip` dan siapkan pintasan `nerroww.exe` pada layar utama.

---

## Hak Cipta dan Lisensi

Karya-karya ini dikembangkan sebagai bagian dari tugas portofolio akademik mahasiswa program studi Teknologi Rekayasa Multimedia (TRM). Hak cipta dimiliki oleh masing-masing pencipta karya. Penggunaan untuk keperluan demonstrasi, edukasi, dan pameran diperbolehkan sesuai etika akademik.
