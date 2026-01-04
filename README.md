# Pomodoro+
Aplikasi Manajemen Waktu Produktif Berbasis Desktop dengan Metode Pomodoro
<img width="1920" height="1200" alt="image" src="https://github.com/user-attachments/assets/10d5ab35-5688-4968-a36a-e973ad04b5f5" />

## Pengenalan
Dibuat oleh Kelompok 1 Kelas 3B:
- Haidar Yahya
- Naail Safwan Saleh
- Nafira Zahra Anantha
- Sekar Ayu Kinanti
- Zaldi Arifa
## Pendahuluan
Dalam era digital yang serba cepat saat ini, manajemen waktu menjadi tantangan utama bagi banyak pelajar dan pekerja profesional. Distraksi yang konstan dan ketidakmampuan untuk memprioritaskan tugas sering kali menyebabkan penurunan produktivitas serta peningkatan stres akibat penundaan pekerjaan (procrastination). Metode Pomodoro, yang membagi waktu kerja menjadi interval fokus yang intens diselingi dengan istirahat singkat, telah terbukti secara ilmiah dapat meningkatkan konsentrasi dan mencegah kelelahan mental (burnout).

Berangkat dari permasalahan tersebut, Kelompok 1 (Kelas 3B) mengembangkan Pomodoro+, sebuah aplikasi manajemen waktu berbasis desktop. Aplikasi ini tidak hanya berfungsi sebagai timer biasa, tetapi mengintegrasikan konsep To-Do List (daftar tugas) dengan pelacakan produktivitas yang komprehensif.

Dibangun menggunakan bahasa pemrograman Java dengan antarmuka JavaFX dan arsitektur MVC (Model-View-Controller), Pomodoro+ dirancang untuk memberikan pengalaman pengguna yang intuitif dan responsif. Tujuan utama dari pengembangan aplikasi ini adalah menyediakan alat bantu yang efektif bagi pengguna untuk mengelola tugas akademik maupun pekerjaan sehari-hari secara terstruktur, memvisualisasikan pencapaian mereka, dan membangun kebiasaan kerja yang lebih sehat dan efisien.
## Fitur-Fitur Utama
### 1. Smart Pomodoro Timer
Fitur inti aplikasi yang menyediakan pengatur waktu fokus (biasanya 25 menit) dan waktu istirahat secara otomatis. Timer ini terintegrasi langsung dengan tugas yang sedang dikerjakan, memastikan pengguna benar-benar fokus pada satu pekerjaan dalam satu waktu.

### 2. Manajemen Tugas Terpadu (Integrated Task Management)
Pengguna dapat membuat, mengedit, dan menghapus tugas (CRUD Tasks). Setiap tugas memiliki detail seperti judul, deskripsi, tenggat waktu (deadline), dan kategori (misalnya: Akademik, Pekerjaan, Pribadi). Fitur ini membantu pengguna memprioritaskan apa yang harus dikerjakan sebelum memulai sesi fokus.

(Berdasarkan tabel tasks di database)

### 3. Pelacakan Produktivitas & Laporan (Productivity Tracking)
Aplikasi merekam riwayat sesi kerja pengguna secara otomatis. Fitur ini menyajikan data statistik, seperti:

Total jam fokus (Total Focus Hours).

Jumlah sesi yang diselesaikan.

Laporan harian atau mingguan untuk mengevaluasi kinerja pengguna.

(Berdasarkan tabel tracking_productivity dan report)

### 4. Sistem Misi & Gamifikasi (Missions & Gamification)
Untuk meningkatkan motivasi, Pomodoro+ menyertakan sistem misi. Pengguna ditantang untuk menyelesaikan target tertentu (misalnya: "Fokus selama 2 jam hari ini") untuk menyelesaikan misi. Hal ini membuat proses belajar atau bekerja terasa lebih menyenangkan seperti bermain game.

(Berdasarkan tabel missions)

### 5. Manajemen Akun Pengguna (User Authentication)
Sistem keamanan yang memungkinkan setiap pengguna memiliki akun pribadi dengan username dan password sendiri. Hal ini menjaga privasi data tugas dan riwayat produktivitas masing-masing pengguna agar tidak tercampur.

(Berdasarkan tabel users, serta fitur Login dan Register)
