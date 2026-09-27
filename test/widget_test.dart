import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:f1_hello/main.dart';

Future<void> _loadFont() async {
  final bytes = File(r'C:\Windows\Fonts\arial.ttf').readAsBytesSync();
  final loader = FontLoader('Roboto')
    ..addFont(Future<ByteData>.value(ByteData.view(Uint8List.fromList(bytes).buffer)));
  await loader.load();
}

void main() {
  testWidgets('Hồ sơ hiện tên và tăng lượt thích', (tester) async {
    await tester.pumpWidget(const MyApp());
    expect(find.text('Lê Nguyễn Hoàng Nam'), findsOneWidget);
    expect(find.text('Lượt thích: 0'), findsOneWidget);

    await tester.tap(find.text('Thích'));
    await tester.pump();
    expect(find.text('Lượt thích: 1'), findsOneWidget);
  });

  testWidgets('giao diện tối và chip đã chọn', (tester) async {
    await _loadFont();
    tester.view.physicalSize = const Size(1080, 2400);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);

    await tester.pumpWidget(const MyApp());
    final button = find.ancestor(
      of: find.byIcon(Icons.dark_mode),
      matching: find.byType(IconButton),
    );
    tester.widget<IconButton>(button).onPressed!.call();
    await tester.pumpAndSettle();
    await tester.tap(find.text('Java'));
    await tester.pumpAndSettle();

    expect(find.text('Đã chọn: 1 kỹ năng'), findsOneWidget);
    expect(Theme.of(tester.element(find.byType(Scaffold))).brightness, Brightness.dark);
    await expectLater(
      find.byType(MaterialApp),
      matchesGoldenFile('goldens/dark.png'),
    );
  });
}
