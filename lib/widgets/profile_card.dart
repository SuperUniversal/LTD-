import 'package:flutter/material.dart';

import 'stat_box.dart';

class ProfileCard extends StatelessWidget {
  const ProfileCard({super.key});

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const CircleAvatar(child: Text('LN', style: TextStyle(fontWeight: FontWeight.w700))),
              title: const Text('Lê Nguyễn Hoàng Nam'),
              subtitle: Text('MSSV: 231A290021', style: TextStyle(fontSize: 15, color: scheme.onSurface)),
            ),
            const Divider(height: 1),
            ListTile(
              leading: const Icon(Icons.class_outlined),
              title: const Text('Lớp'),
              subtitle: Text('CNTT – LTDD', style: TextStyle(fontSize: 15, color: scheme.onSurface)),
            ),
            ListTile(
              leading: const Icon(Icons.mail_outline),
              title: const Text('Email'),
              subtitle: Text('231A290021@vhu.edu.vn', style: TextStyle(fontSize: 15, color: scheme.onSurface)),
            ),
            const Padding(
              padding: EdgeInsets.fromLTRB(16, 8, 16, 12),
              child: Row(
                children: [
                  Expanded(child: StatBox(label: 'Lab đã nộp', value: '2')),
                  SizedBox(width: 12),
                  Expanded(child: StatBox(label: 'Điểm TB lab', value: '8.5')),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
