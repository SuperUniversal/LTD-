void main() {
  final diem = <double>[7.5, 4.0, 8.5, 9.0, 5.5];
  final tong = diem.reduce((a, b) => a + b);
  final tb = (tong / diem.length).toStringAsFixed(2);
  final dat = diem.where((d) => d >= 5).toList();
  print('Điểm TB: $tb; đạt: $dat');
}
