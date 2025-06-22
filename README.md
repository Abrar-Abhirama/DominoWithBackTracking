# Domino Backtracking & Game Interaktif

## Penjelasan

Aplikasi ini adalah sebuah permainan Domino interaktif yang dibangun menggunakan **Java Swing**. Proyek ini memiliki fitur utama berupa lawan AI (komputer) yang cerdas. Kecerdasan buatan musuh ini didukung oleh **algoritma backtracking** yang memungkinkannya untuk menganalisis permainan dan menentukan langkah paling optimal untuk mencapai kemenangan.

Pemain dapat bermain melawan AI ini dalam sebuah antarmuka grafis yang lengkap, mengikuti aturan permainan domino standar. Aplikasi ini tidak hanya menangani logika permainan seperti giliran, pass, dan kondisi menang/kalah, tetapi juga mengelola visualisasi papan yang kompleks, termasuk orientasi kartu yang benar dan tata letak dinamis yang bisa "melingkar" (wrapping) saat rantai domino mencapai batas layar.

## Screenshot Aplikasi

*(Disarankan untuk mengganti gambar ini dengan screenshot final dari aplikasi Anda)*

![Screenshot Game Domino](https://i.imgur.com/YwNymf8.png)

## Fitur Utama

* **Permainan Interaktif (Player vs AI):** Bermain langsung melawan komputer yang cerdas.
* **AI Cerdas Berbasis Backtracking:** Musuh menggunakan algoritma backtracking untuk mencari jalur kemenangan terbaik di setiap giliran.
* **Aturan Permainan Domino Lengkap:**
    * Sistem giliran antara pemain dan musuh.
    * Fitur "Pass" jika pemain tidak bisa bergerak.
    * Deteksi kondisi menang/kalah saat kartu habis.
    * Penanganan kondisi "Macet" (*Stuck*), di mana pemenang ditentukan berdasarkan total skor kartu terendah.
* **Antarmuka Grafis (GUI) Responsif:**
    * Permainan sepenuhnya dikontrol melalui klik mouse.
    * Adanya sorotan (highlight) pada kartu yang dipilih.
    * Pesan status yang dinamis untuk memandu pemain.
* **Visualisasi Papan Canggih:**
    * Kartu balak (angka kembar) otomatis digambar secara vertikal.
    * Orientasi kartu di papan selalu benar sesuai koneksinya.
    * Tata letak rantai domino yang bisa melingkar (wrapping) ke baris baru saat mencapai batas layar.
* **Fitur Strategis:** Tampilan jumlah total sisa angka (pips) yang ada di tangan kedua pemain, membantu pemain menyusun strategi.

## Requirements (Kebutuhan Sistem)

* **Java Development Kit (JDK)** versi 11 atau yang lebih baru.

## How to Run (Cara Menjalankan)

Ada dua cara utama untuk mengompilasi dan menjalankan proyek ini:

#### 1. Melalui Command Line / Terminal

1.  Buka terminal atau command prompt dan navigasi ke direktori utama proyek Anda.
2.  Masuk ke folder `src`:
    ```bash
    cd src
    ```
3.  Kompilasi semua file Java di dalamnya:
    ```bash
    javac *.java
    ```
4.  Jalankan kelas utama `DominoSolverGUI` dari direktori utama:
    ```bash
    java DominoSolverGUI
    ```

#### 2. Melalui IDE (IntelliJ IDEA, Eclipse, NetBeans, dll.)

1.  Buka proyek Anda sebagai proyek Java di IDE pilihan Anda.
2.  Temukan file `DominoSolverGUI.java` di dalam struktur proyek.
3.  Klik kanan pada file tersebut dan pilih opsi **"Run"** atau **"Run 'DominoSolverGUI.main()'"**.

## How to Play (Cara Bermain)

1.  **Mulai Permainan:** Klik tombol **"Game Baru"** untuk mengocok dan membagikan kartu.
2.  **Giliran Anda:**
    * Klik pada salah satu kartu di area "Tangan Anda". Kartu tersebut akan disorot.
    * Klik pada area papan yang valid (ujung kiri atau kanan rantai domino) untuk meletakkan kartu. Jika papan kosong, klik area tengah yang ditandai.
    * Jika Anda tidak bisa bergerak, klik tombol **"Pass"**.
3.  **Giliran Musuh:** Setelah Anda bergerak, tunggu sejenak. Musuh akan secara otomatis berpikir dan meletakkan kartunya.
4.  **Menang/Kalah:** Permainan berakhir ketika salah satu pemain kehabisan kartu, atau ketika kedua pemain sama-sama melakukan "Pass" (game macet).

## Struktur Kode

Proyek ini terdiri dari 4 file Java utama di dalam folder `src`:

* **`DominoSolverGUI.java`**: Kelas utama yang membuat jendela aplikasi (`JFrame`) dan menjadi wadah untuk panel permainan.
* **`GamePanel.java`**: Jantung dari aplikasi. Kelas ini menangani semua logika permainan, giliran AI, interaksi mouse, dan semua proses penggambaran ke layar.
* **`Domino.java`**: Kelas data yang merepresentasikan satu buah kartu domino.
* **`Move.java`**: Kelas data sederhana yang digunakan oleh algoritma backtracking untuk menyimpan informasi sebuah langkah.

## Author

* **Abrar Abhirama Widyadhana**
* **13523038**
