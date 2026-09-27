void main() {
  var ten = 'Dart';
  final int namHoc = 2026;
  const double pi = 3.14159;
  String? biDanh;
  print('Xin chào $ten! Năm $namHoc, pi = $pi');
  print('Bí danh: ${biDanh ?? "(chưa có)"}');
  print('Độ dài bí danh: ${biDanh?.length}');
}
