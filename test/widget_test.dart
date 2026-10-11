import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:f3_231a290021/main.dart';

void main() {
  testWidgets('Bấm ĐĂNG KÝ khi form trống thì báo lỗi', (WidgetTester tester) async {
    tester.view.physicalSize = const Size(800, 2400);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);

    await tester.pumpWidget(const MyApp());

    expect(find.text('Lab F3 – Đăng ký học phần'), findsOneWidget);
    expect(find.text('Lê Nguyễn Hoàng Nam – MSSV 231A290021'), findsOneWidget);

    await tester.tap(find.text('ĐĂNG KÝ'));
    await tester.pump();

    expect(find.text('Vui lòng nhập họ tên'), findsOneWidget);
    expect(find.text('Vui lòng nhập MSSV'), findsOneWidget);
    expect(find.text('Vui lòng nhập email'), findsOneWidget);
    expect(find.text('Vui lòng chọn ngày sinh'), findsOneWidget);
    expect(find.text('Hãy chọn một học phần'), findsOneWidget);
    expect(find.text('Hãy chọn ít nhất một buổi học'), findsOneWidget);
  });
}
