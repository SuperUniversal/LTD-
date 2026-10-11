import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'F3_231A290021',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF0468D7)),
        inputDecorationTheme: const InputDecorationTheme(border: OutlineInputBorder()),
      ),
      home: const RegisterPage(),
    );
  }
}

/// NC4: một học phần gồm mã, tên và số tín chỉ của riêng môn đó.
class MonHoc {
  const MonHoc({required this.ma, required this.ten, required this.soTinChi});

  final String ma;
  final String ten;
  final int soTinChi;

  @override
  bool operator ==(Object other) => other is MonHoc && other.ma == ma;

  @override
  int get hashCode => ma.hashCode;
}

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _formKey = GlobalKey<FormState>();

  final _hoTenCtrl = TextEditingController();
  final _mssvCtrl = TextEditingController();
  final _emailCtrl = TextEditingController();
  final _ngaySinhCtrl = TextEditingController();

  static const _monHocs = [
    MonHoc(ma: 'INT4211', ten: 'Lập trình di động', soTinChi: 3),
    MonHoc(ma: 'INT3201', ten: 'Cơ sở dữ liệu', soTinChi: 3),
    MonHoc(ma: 'INT3306', ten: 'Trí tuệ nhân tạo', soTinChi: 3),
    MonHoc(ma: 'INT3307', ten: 'Kiểm thử phần mềm', soTinChi: 2),
  ];
  static const _buoiHocs = ['Sáng', 'Chiều', 'Tối'];

  MonHoc? _monHoc;
  DateTime? _ngaySinh;
  String _heDaoTao = 'Chính quy';
  final Set<String> _buoiDaChon = {};
  bool _nhanThongBao = true;
  double _soTinChi = 15;

  @override
  void dispose() {
    _hoTenCtrl.dispose();
    _mssvCtrl.dispose();
    _emailCtrl.dispose();
    _ngaySinhCtrl.dispose();
    super.dispose();
  }

  String? _kiemTraHoTen(String? v) {
    final s = (v ?? '').trim();
    if (s.isEmpty) return 'Vui lòng nhập họ tên';
    if (s.length < 3) return 'Họ tên phải có ít nhất 3 ký tự';
    return null;
  }

  String? _kiemTraMssv(String? v) {
    final s = (v ?? '').trim();
    if (s.isEmpty) return 'Vui lòng nhập MSSV';
    if (!RegExp(r'^\d{10}$').hasMatch(s)) return 'MSSV phải gồm đúng 10 chữ số';
    return null;
  }

  String? _kiemTraEmail(String? v) {
    final s = (v ?? '').trim();
    if (s.isEmpty) return 'Vui lòng nhập email';
    if (!RegExp(r'^[\w.\-]+@[\w\-]+\.[\w.\-]+$').hasMatch(s)) {
      return 'Email không đúng định dạng';
    }
    return null;
  }

  String? _kiemTraNgaySinh(String? v) {
    final s = (v ?? '').trim();
    if (s.isEmpty) return 'Vui lòng chọn ngày sinh';
    if (!RegExp(r'^\d{2}/\d{2}/\d{4}$').hasMatch(s)) {
      return 'Ngày sinh dạng dd/MM/yyyy';
    }
    return null;
  }

  String _dinhDangNgay(DateTime d) {
    final dd = d.day.toString().padLeft(2, '0');
    final mm = d.month.toString().padLeft(2, '0');
    return '$dd/$mm/${d.year}';
  }

  Future<void> _chonNgaySinh() async {
    final homNay = DateTime.now();
    final moc = DateTime(homNay.year, homNay.month, homNay.day);
    final d = await showDatePicker(
      context: context,
      initialDate: _ngaySinh ?? DateTime(homNay.year - 18, homNay.month, homNay.day),
      firstDate: DateTime(1970),
      lastDate: moc,
      helpText: 'Chọn ngày sinh',
      cancelText: 'Hủy',
      confirmText: 'Chọn',
    );
    if (!mounted || d == null) return;
    setState(() {
      _ngaySinh = d;
      _ngaySinhCtrl.text = _dinhDangNgay(d);
    });
  }

  void _dangKy() {
    final hopLe = _formKey.currentState!.validate();

    if (_buoiDaChon.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Hãy chọn ít nhất một buổi học')),
      );
      return;
    }
    // NC2: hệ vừa làm vừa học chỉ học được vào buổi tối.
    if (_heDaoTao == 'Vừa làm vừa học' && !_buoiDaChon.contains('Tối')) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Hệ vừa làm vừa học phải chọn buổi Tối')),
      );
      return;
    }
    if (!hopLe) return;

    final mon = _monHoc!;
    showDialog<void>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xác nhận đăng ký'),
        content: Text(
          'Họ tên: ${_hoTenCtrl.text.trim()}\n'
          'MSSV: ${_mssvCtrl.text.trim()}\n'
          'Email: ${_emailCtrl.text.trim()}\n'
          'Ngày sinh: ${_ngaySinhCtrl.text.trim()}\n'
          'Học phần: ${mon.ten} (${mon.ma}) – ${mon.soTinChi} tín chỉ\n'
          'Hệ đào tạo: $_heDaoTao\n'
          'Buổi học: ${(_buoiDaChon.toList()..sort()).join(', ')}\n'
          'Số tín chỉ đăng ký: ${_soTinChi.round()}\n'
          'Nhận thông báo: ${_nhanThongBao ? 'Có' : 'Không'}',
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Đóng')),
        ],
      ),
    );
  }

  void _lamLai() {
    _formKey.currentState!.reset();
    _hoTenCtrl.clear();
    _mssvCtrl.clear();
    _emailCtrl.clear();
    _ngaySinhCtrl.clear();
    setState(() {
      _monHoc = null;
      _ngaySinh = null;
      _heDaoTao = 'Chính quy';
      _buoiDaChon.clear();
      _nhanThongBao = true;
      _soTinChi = 15;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Lab F3 – Đăng ký học phần'),
        backgroundColor: Theme.of(context).colorScheme.primaryContainer,
      ),
      body: SafeArea(
        child: Form(
          key: _formKey,
          autovalidateMode: AutovalidateMode.onUserInteraction,
          child: ListView(
            padding: const EdgeInsets.all(20),
            children: [
              Text(
                'Lê Nguyễn Hoàng Nam – MSSV 231A290021',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _hoTenCtrl,
                textInputAction: TextInputAction.next,
                maxLength: 50,
                decoration: const InputDecoration(
                  labelText: 'Họ và tên *',
                  prefixIcon: Icon(Icons.person_outline),
                ),
                validator: _kiemTraHoTen,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _mssvCtrl,
                textInputAction: TextInputAction.next,
                keyboardType: TextInputType.number,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  LengthLimitingTextInputFormatter(10),
                ],
                decoration: const InputDecoration(
                  labelText: 'Mã số sinh viên *',
                  prefixIcon: Icon(Icons.badge_outlined),
                  helperText: 'Gồm đúng 10 chữ số',
                ),
                validator: _kiemTraMssv,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _emailCtrl,
                textInputAction: TextInputAction.next,
                keyboardType: TextInputType.emailAddress,
                decoration: const InputDecoration(
                  labelText: 'Email *',
                  prefixIcon: Icon(Icons.mail_outline),
                ),
                validator: _kiemTraEmail,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _ngaySinhCtrl,
                readOnly: true,
                onTap: _chonNgaySinh,
                decoration: const InputDecoration(
                  labelText: 'Ngày sinh *',
                  hintText: 'dd/MM/yyyy',
                  prefixIcon: Icon(Icons.cake_outlined),
                  suffixIcon: Icon(Icons.calendar_month_outlined),
                ),
                validator: _kiemTraNgaySinh,
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<MonHoc>(
                key: ValueKey(_monHoc?.ma),
                initialValue: _monHoc,
                isExpanded: true,
                decoration: const InputDecoration(
                  labelText: 'Học phần đăng ký *',
                  prefixIcon: Icon(Icons.menu_book_outlined),
                ),
                items: _monHocs
                    .map(
                      (m) => DropdownMenuItem(
                        value: m,
                        child: Text('${m.ten} (${m.soTinChi} tín chỉ)'),
                      ),
                    )
                    .toList(),
                onChanged: (v) => setState(() => _monHoc = v),
                validator: (v) => v == null ? 'Hãy chọn một học phần' : null,
              ),
              if (_monHoc != null) ...[
                const SizedBox(height: 8),
                Text(
                  'Đã chọn ${_monHoc!.ten}, mã ${_monHoc!.ma}, ${_monHoc!.soTinChi} tín chỉ.',
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ],
              const SizedBox(height: 20),
              Text('Hệ đào tạo', style: Theme.of(context).textTheme.titleMedium),
              RadioGroup<String>(
                groupValue: _heDaoTao,
                onChanged: (v) => setState(() => _heDaoTao = v ?? 'Chính quy'),
                child: const Column(
                  children: [
                    RadioListTile<String>(
                      value: 'Chính quy',
                      title: Text('Chính quy'),
                      contentPadding: EdgeInsets.zero,
                    ),
                    RadioListTile<String>(
                      value: 'Vừa làm vừa học',
                      title: Text('Vừa làm vừa học'),
                      contentPadding: EdgeInsets.zero,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Buổi học mong muốn (chọn ≥ 1)',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              Text(
                'Hệ vừa làm vừa học bắt buộc chọn buổi Tối.',
                style: Theme.of(context).textTheme.bodySmall,
              ),
              ..._buoiHocs.map(
                (b) => CheckboxListTile(
                  value: _buoiDaChon.contains(b),
                  title: Text(b),
                  contentPadding: EdgeInsets.zero,
                  controlAffinity: ListTileControlAffinity.leading,
                  onChanged: (chon) => setState(() {
                    if (chon == true) {
                      _buoiDaChon.add(b);
                    } else {
                      _buoiDaChon.remove(b);
                    }
                  }),
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Số tín chỉ đăng ký: ${_soTinChi.round()}',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              Slider(
                value: _soTinChi,
                min: 10,
                max: 24,
                divisions: 14,
                label: '${_soTinChi.round()}',
                onChanged: (v) => setState(() => _soTinChi = v),
              ),
              SwitchListTile(
                value: _nhanThongBao,
                title: const Text('Nhận thông báo qua email'),
                contentPadding: EdgeInsets.zero,
                onChanged: (v) => setState(() => _nhanThongBao = v),
              ),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: _dangKy,
                child: const Padding(
                  padding: EdgeInsets.symmetric(vertical: 12),
                  child: Text('ĐĂNG KÝ'),
                ),
              ),
              const SizedBox(height: 8),
              OutlinedButton(onPressed: _lamLai, child: const Text('Làm lại')),
            ],
          ),
        ),
      ),
    );
  }
}
