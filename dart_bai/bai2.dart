double tinhBMI({required double canNang, required double chieuCao}) =>
    canNang / (chieuCao * chieuCao);

String phanLoaiBMI(double bmi) {
  if (bmi < 18.5) return 'Thiếu cân';
  if (bmi < 23) return 'Bình thường';
  if (bmi < 25) return 'Thừa cân';
  return 'Béo phì';
}

void main() {
  final bmi = tinhBMI(canNang: 60, chieuCao: 1.70);
  print('BMI = ${bmi.toStringAsFixed(1)} → ${phanLoaiBMI(bmi)}');
}
