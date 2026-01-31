package DataModel;

import java.sql.Date;

public class Mahasiswa {
    // ID dihapus, NIM jadi identitas utama
    private String nim;
    private String nama;
    private Date tanggalLahir;
    private String prodi;

    // Constructor
    public Mahasiswa(String nim, String nama, Date tanggalLahir, String prodi) {
        this.nim = nim;
        this.nama = nama;
        this.tanggalLahir = tanggalLahir;
        this.prodi = prodi;
    }

    // Getters (Hapus getId)
    public String getNim() { 
        return nim; 
    }
    public String getNama() { 
        return nama;
    }
    public Date getTanggalLahir() { 
        return tanggalLahir; 
    }
    public String getProdi() { 
        return prodi; 
    }
}