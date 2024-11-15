# Aplikasi Pengelolaan Kontak

Aplikasi ini adalah aplikasi pengelolaan kontak berbasis GUI yang menyimpan informasi kontak seperti nama, nomor telepon, dan kategori. Data disimpan dalam database SQLite dan dapat dikelompokkan berdasarkan kategori.

## Fitur Utama

- **CRUD (Create, Read, Update, Delete):** Menyediakan fitur untuk menambahkan, mengedit, dan menghapus kontak.
- **Kategori Kontak:** Pengguna dapat mengelompokkan kontak berdasarkan kategori seperti keluarga, teman, atau kerja menggunakan JComboBox.
- **Pencarian Kontak:** Fitur pencarian berdasarkan nama atau nomor telepon, dengan hasil yang ditampilkan di JTable.
- **Validasi Input:** Memastikan nomor telepon hanya berisi angka dengan panjang sesuai standar.
- **Ekspor/Impor CSV:** Memungkinkan pengguna untuk mengekspor daftar kontak ke file CSV atau mengimpor kontak dari file CSV.

## Teknologi yang Digunakan

- **Bahasa Pemrograman:** Java
- **Database:** SQLite
- **GUI Components:** JFrame, JPanel, JLabel, JTextField, JButton, JList, JComboBox, JTable, JScrollPane

## Struktur Program

1. **Deskripsi Program**
   - Aplikasi menyimpan informasi kontak ke dalam database SQLite.
   - Pengguna dapat menambahkan, mengedit, dan menghapus kontak.
   - Kontak dapat dikelompokkan berdasarkan kategori seperti keluarga, teman, atau kerja.

2. **Komponen GUI**
   - Menggunakan berbagai komponen GUI seperti JFrame, JPanel, JLabel, JTextField, JButton, JComboBox, JTable, dan JScrollPane.

3. **Logika Program**
   - Menggunakan database SQLite dengan fitur CRUD (Create, Read, Update, Delete) untuk pengelolaan data kontak.

4. **Events**
   - **ActionListener** untuk tombol Tambah, Edit, Hapus, dan Cari Kontak.
   - **ItemListener** untuk JComboBox kategori kontak.

5. **Variasi**
   - Fitur pencarian berdasarkan nama atau nomor telepon dengan hasil yang ditampilkan di JTable.
   - Validasi input nomor telepon.
   - Ekspor dan impor data kontak dalam format CSV.


## Kontribusi

Silakan buka issue atau pull request jika Anda ingin berkontribusi pada proyek ini.

---