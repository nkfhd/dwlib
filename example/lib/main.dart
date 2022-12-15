import 'package:flutter/material.dart';

import 'package:dwlib/dwlib.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _dwlibPlugin = Dwlib();

  @override
  void initState() {
    super.initState();
  }

  _getList() async {
    var list = await _dwlibPlugin.getList();
    print(list);
  }
  _start() async {
    var list = await _dwlibPlugin.start();
    print(list);
  }
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              ElevatedButton(
                onPressed: _start,
                child: const Text('start'),
              ),
              ElevatedButton(
                onPressed: _getList,
                child: const Text('get list'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
