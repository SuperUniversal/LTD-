class SinhVien {
  SinhVien(this.hoTen, this.mssv, this.diemTB);

  final String hoTen;
  final String mssv;
  final double diemTB;

  String get xepLoai {
    if (diemTB >= 8.5) return 'Giỏi';
    if (diemTB >= 7.0) return 'Khá';
    if (diemTB >= 5.0) return 'Trung bình';
    return 'Yếu';
  }

  @override
  String toString() => '$mssv – $hoTen – $diemTB ($xepLoai)';
}

void main() {
  final ds = [
    SinhVien('Lê Nguyễn Hoàng Nam', '231A290021', 8.7),
    SinhVien('Trần Thị B', '2201234568', 6.4),
  ];
  for (final sv in ds) {
    print(sv);
  }
}
