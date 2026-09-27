double tinhBMI({required double canNang, required double chieuCao}) =>
    canNang / (chieuCao * chieuCao);

String phanLoaiBMI(double bmi) {
  if (bmi < 18.5) return 'Thiếu cân';
  if (bmi < 23) return 'Bình thường';
  if (bmi < 25) return 'Thừa cân';
  return 'Béo phì';
}

void main() {
  final dungThuTu = tinhBMI(canNang: 60, chieuCao: 1.70);
  final doiThuTu = tinhBMI(chieuCao: 1.70, canNang: 60);
  print('Đúng thứ tự: BMI = ${dungThuTu.toStringAsFixed(1)} → ${phanLoaiBMI(dungThuTu)}');
  print('Đổi thứ tự: BMI = ${doiThuTu.toStringAsFixed(1)} → ${phanLoaiBMI(doiThuTu)}');
}
