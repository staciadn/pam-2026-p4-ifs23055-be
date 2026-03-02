CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL


    CREATE TABLE IF NOT EXISTS dongeng (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    judul VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    sinopsis TEXT NOT NULL,
    pesan TEXT NOT NULL,
    asal VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
