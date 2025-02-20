import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:get_background_location/get_background_location.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _getBackgroundLocationPlugin = GetBackgroundLocation();
  String _location = 'Localização não disponível';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Função para inicializar o serviço e capturar a localização.
  Future<void> initPlatformState() async {
    try {
      // Inicia o serviço de localização
       await _getBackgroundLocationPlugin.startServiceLocation();

      // Escuta o stream de localização
     _getBackgroundLocationPlugin.getLocation().listen((location) {
        print("Localização Atualizada: $location");

        // Atualiza o estado com as novas coordenadas
        setState(() {
          _location = 'Latitude: ${location['latitude']}, Longitude: ${location['longitude']}';
        });
      });
    } on PlatformException catch (e) {
      print('Erro: ${e.message}');
      setState(() {
        _location = 'Erro ao obter localização: ${e.message}';
      });
    }

    // Se o widget for removido da árvore, não execute mais ações.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin exemplo de Localização'),
        ),
        body: Center(
          child: Text(
            'Localização Atual: \n$_location', // Exibe a localização na tela
            textAlign: TextAlign.center,
          ),
        ),
      ),
    );
  }
}
