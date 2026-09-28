import 'package:flutter_test/flutter_test.dart';

import 'package:f2/main.dart';

void main() {
  testWidgets('Màn đăng nhập hiện tiêu đề', (WidgetTester tester) async {
    await tester.pumpWidget(const MyApp());
    expect(find.text('Đăng nhập hệ thống'), findsOneWidget);
    expect(find.text('ĐĂNG NHẬP'), findsOneWidget);
  });
}
