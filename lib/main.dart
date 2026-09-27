import 'package:flutter/material.dart';

import 'widgets/profile_card.dart';

void main() {
  runApp(const MyApp());
}

/// Widget gốc: theme Material 3, chuyển sáng/tối (NC1).
class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ThemeMode _mode = ThemeMode.light;

  void _toggleTheme() {
    setState(() {
      _mode = _mode == ThemeMode.light ? ThemeMode.dark : ThemeMode.light;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'F1_231A290021',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
      ),
      darkTheme: ThemeData(
        brightness: Brightness.dark,
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.indigo,
          brightness: Brightness.dark,
        ),
      ),
      themeMode: _mode,
      home: ProfilePage(onToggleTheme: _toggleTheme),
    );
  }
}

/// Màn hình chính: số lượt thích thay đổi nên dùng StatefulWidget.
class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key, required this.onToggleTheme});

  final VoidCallback onToggleTheme;

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  int _likes = 0;
  final Set<String> _selectedSkills = {};
  late final AppLifecycleListener _listener;

  static const _skills = ['Java', 'Dart', 'SQL', 'Git', 'UI/UX'];

  void _increase() => setState(() => _likes++);

  void _decrease() => setState(() {
        if (_likes > 0) _likes--;
      });

  void _reset() => setState(() => _likes = 0);

  @override
  void initState() {
    super.initState();
    debugPrint('[F1] initState');
    _listener = AppLifecycleListener(
      onStateChange: (state) =>
          debugPrint('[F1] AppLifecycleState: ${state.name}'),
    );
  }

  @override
  void dispose() {
    _listener.dispose();
    debugPrint('[F1] dispose');
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    final likeColor = _likes >= 10 ? Colors.red : scheme.primary;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Lab F1 – Hồ sơ của tôi'),
        backgroundColor: scheme.primaryContainer,
        actions: [
          IconButton(
            onPressed: widget.onToggleTheme,
            icon: Icon(isDark ? Icons.light_mode : Icons.dark_mode),
            tooltip: 'Đổi giao diện',
          ),
        ],
      ),
      body: Center(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const ProfileCard(
                  name: 'Lê Nguyễn Hoàng Nam',
                  studentId: '231A290021',
                  className: 'Lớp CNTT – LTDD',
                ),
                const SizedBox(height: 16),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  alignment: WrapAlignment.center,
                  children: [
                    for (final skill in _skills)
                      FilterChip(
                        label: Text(skill),
                        selected: _selectedSkills.contains(skill),
                        onSelected: (selected) {
                          setState(() {
                            if (selected) {
                              _selectedSkills.add(skill);
                            } else {
                              _selectedSkills.remove(skill);
                            }
                          });
                        },
                      ),
                  ],
                ),
                const SizedBox(height: 8),
                Text('Đã chọn: ${_selectedSkills.length} kỹ năng'),
                const SizedBox(height: 24),
                Text(
                  'Lượt thích: $_likes',
                  style: TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                    color: likeColor,
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    IconButton.filledTonal(
                      onPressed: _decrease,
                      icon: const Icon(Icons.remove),
                    ),
                    const SizedBox(width: 12),
                    FilledButton.icon(
                      onPressed: _increase,
                      icon: const Icon(Icons.favorite),
                      label: const Text('Thích'),
                    ),
                    const SizedBox(width: 12),
                    IconButton.outlined(
                      onPressed: _reset,
                      icon: const Icon(Icons.refresh),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
