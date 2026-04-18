# Conta Dias

App Android de contagem regressiva e progressiva de eventos, construído com Jetpack Compose e Material You.

## Funcionalidades

- **Cadastro de eventos** com nome, emoji, data/hora e cor personalizada
- **Contagem regressiva** para eventos futuros ("Faltam X dias")
- **Contagem progressiva** para eventos passados ("Faz X meses")
- **Troca de unidade por gesto** — arraste horizontalmente na tela de detalhe para alternar entre segundos, minutos, horas, dias, semanas, meses e anos
- **Widget para a home screen** — mostra o próximo evento futuro diretamente na tela inicial
- **Paleta Material You** em tons de terracota e pêssego, com suporte a cor dinâmica (Android 12+)

## Telas

| Lista de eventos | Detalhe | Criar evento |
|---|---|---|
| Agrupa eventos em Futuros e Já aconteceram | Número hero com swipe para trocar unidade | Emoji picker, date picker e seletor de cor |

## Tecnologias

- **Jetpack Compose** — UI declarativa
- **Material You / Material 3** — design system com cor dinâmica
- **Navigation Compose** — navegação entre telas
- **ViewModel + StateFlow** — gerenciamento de estado
- **Glance** — widget para a home screen
- **SharedPreferences** — persistência local

## Requisitos

- Android 13+ (API 33)
- Android Studio Ladybug ou superior

## Como rodar

```bash
git clone https://github.com/pedroslima/conta-dias.git
```

Abra no Android Studio e execute em um emulador ou dispositivo com Android 13+.
