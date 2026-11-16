# 🏍️ MotoGarage — historia wersji

Aplikacja MotoGarage na Androida do zarządzania motocyklami, ich serwisem, wydatkami oraz ulubionymi torami i miejscówkami do jazdy.

**Aktualna wersja: v0.3.5 Beta**

---

## 🏁 v0.1 Alpha — 13.11.2026
**Pierwsza wersja aplikacji**

### Główne funkcje
- **Start** — ekran główny z podsumowaniem wybranego motocykla, przebiegiem, motogodzinami, serwisem i wydatkami.
- **Moje motocykle** — lista motocykli z możliwością przechowywania ich danych technicznych, zdjęć i notatek.
- **Historia serwisowa** — rejestrowanie napraw, przeglądów, wymian oleju, kosztów i stanów licznika.
- **Wydatki** — ewidencja kosztów eksploatacji motocykli.
- **Tory i miejscówki** — katalog miejsc do jazdy z opisami, zdjęciami i lokalizacjami.

### Wygląd i personalizacja
- Sześć motywów kolorystycznych.
- Możliwość dodawania zdjęć motocykli.
- Personalizacja wyglądu aplikacji.
- Interfejs dostosowany do urządzeń mobilnych.

### Techniczne
- Utworzono projekt Android.
- Dodano lokalne przechowywanie danych.
- Skonfigurowano GitHub Actions do kompilacji APK.
- Wygenerowano i zainstalowano pierwszą wersję testową.

### Znane ograniczenia
- Część przycisków dodawania i edycji nie reagowała.
- W aplikacji znajdował się domyślny motocykl ASIX XR99.

---

## 🔧 v0.2 Alpha — 13.11.2026
**Naprawa formularzy i zarządzania motocyklami**

### Poprawione funkcje
- Naprawiono przycisk „Edytuj motocykl” na ekranie Start.
- Naprawiono dodawanie i edycję motocykli w zakładce Moje motocykle.
- Przywrócono działanie przycisków szybkich akcji na ekranie Start.
- Naprawiono dodawanie wpisów w Historii serwisowej.
- Naprawiono dodawanie wydatków.
- Naprawiono dodawanie miejscówek.

### Zarządzanie motocyklami
- Usunięto fabrycznie dodany motocykl ASIX XR99.
- Nowy użytkownik rozpoczyna z pustą listą motocykli.
- Umożliwiono usunięcie ostatniego motocykla z garażu.
- Zachowano możliwość dodawania zdjęć oraz edytowania danych motocykla.

### Techniczne
- Usunięto konflikt w obsłudze formularzy JavaScript.
- Zachowano lokalny mechanizm przechowywania danych.
- Zaktualizowano numer wersji aplikacji.
- Przeprowadzono testy instalacji i podstawowych formularzy na telefonie.

---

### v0.3.0 — Ulepszenia interfejsu
- Prace nad położeniem górnego nagłówka.
- Dostosowanie wyglądu aplikacji do ekranu telefonu.
- Usprawnienia układu interfejsu.

### v0.3.1 — Nowy wygląd nagłówka
- Dodano ikonę MotoGarage zamiast emoji.
- Poprawiono wygląd nazwy aplikacji.
- Ujednolicono stylistykę nagłówka.
- Wprowadzono stylizowane okna potwierdzeń.
- Zachowano obsługę motogodzin i pozostałe funkcje.

### v0.3.2 — Poprawki pasków systemowych
- Poprawiono obsługę pasków systemowych Androida.
- Usunięto białą przestrzeń nad nagłówkiem.
- Dostosowano położenie interfejsu względem paska statusu.
- Poprawiono układ dolnej nawigacji.

### v0.3.3 — Finalne poprawki interfejsu
- Usunięto zbędny odstęp pod dolnym menu.
- Poprawiono położenie zakładek względem systemowego paska nawigacji.
- Zachowano poprawiony wygląd górnego nagłówka.
- Przetestowano aplikację na telefonie.
- Zaakceptowano końcowy wygląd interfejsu.

---

**Ostatnia aktualizacja:** 13.11.2026

**Status:** ✅ Wersja przetestowana i zaakceptowana.

**Uwaga:** Numery 0.3.2 i 0.3.3 odnoszą się do kolejnych etapów poprawek. Numer wersji APK może nadal wynosić 0.3.1.

---

**Projekt MotoGarage — aplikacja rozwijana etapami, na podstawie testów na urządzeniach Android.**

## v0.3.4 — 14.11.2026
- Własna ikona crossa w zakładce Motocykle.
- Spójny numer 0.3.4 w ustawieniach i konfiguracji APK (versionCode 5).
- Przyciski Dodaj zdjęcie w stylu aplikacji we wszystkich polach wyboru zdjęć, także tła.
- Nazwa wybranego pliku w formularzach.

Weryfikacja: składnia JavaScript; test APK na telefonie pozostaje do wykonania.

MotoGarage 0.3.5 — poprawki z 15.11.2026
- Skonfigurowano podpisywanie APK stałym kluczem, umożliwiające instalowanie kolejnych wersji jako aktualizacji bez odinstalowywania aplikacji.
- Dodano eksport kopii JSON przez systemowe okno Androida z wyborem miejsca zapisu.
- Komunikat o zapisaniu kopii pojawia się dopiero po zakończeniu zapisu.
- Naprawiono import kopii zapasowych — Android odczytuje teraz plik bezpośrednio, omijając problem pustej zawartości przekazywanej przez WebView.
- Dodano walidację kopii i dokładniejsze komunikaty błędów importu.
- Import wymaga potwierdzenia zastąpienia aktualnych danych.
- Sprawdzono na telefonie: instalację jako aktualizacja, eksport pliku JSON i ponowny import zapisanej kopii.

---

## 📌 Planowane usprawnienia

- Systemowe powiadomienia o zbliżającym się serwisie — odłożone na później.
- Automatyczne nazewnictwo plików instalacyjnych, np. `MotoGarage-v0.4.apk`.
- Dalsze testy stabilności zapisu danych.
- Rozbudowa funkcji na podstawie uwag użytkowników.


## v0.3.6 — język polski i angielski

- Przełącznik PL / EN w ustawieniach, zgodny z wyglądem aplikacji; aktywny przycisk używa koloru akcentu.
- Automatyczny wybór języka przy pierwszym uruchomieniu: polski dla telefonu po polsku, angielski dla pozostałych języków. Ręczny wybór jest zapamiętywany.
- Tłumaczenia ekranów, formularzy, przycisków, przypomnień, komunikatów oraz obsługi kopii zapasowych po stronie Androida.
- Daty i kwoty formatowane według wybranego języka. Waluta nadal PLN; zmiana języka nie przelicza kwot.
- Nazwy i notatki użytkownika pozostają w oryginale. Zgodność ze starszymi kopiami JSON; import nie zmienia wybranego języka.
- Wersja Androida: `versionCode 7`, `versionName 0.3.6`. Stały podpis i identyfikator aplikacji zachowane.

### Wgranie aktualizacji

Zastąp w repozytorium trzy pliki: `MotoGarage_Android/app/src/main/assets/index.html`, `MotoGarage_Android/app/src/main/java/pl/motogarage/app/MainActivity.kt` oraz `MotoGarage_Android/app/build.gradle.kts`. Zaktualizuj również ten README. Istniejący workflow GitHub Actions buduje podpisane APK z dotychczasowymi sekretami.

### Weryfikacja

Sprawdzono logikę JavaScript: sześć ekranów w EN, wszystkie formularze, zapis danych, zachowanie nazw i notatek, stabilne wartości kategorii, daty, walutę, wybór języka i pamiętanie ustawienia oraz import poprawnej i błędnej kopii JSON. Środowisko testowe symulowało DOM i pamięć aplikacji; pełny test wizualny WebView i natywnych okien importu/eksportu wymaga telefonu. W tym środowisku nie zbudowano APK.


## v0.3.7 — globalny wybór waluty

- Przyciski PLN / EUR / USD / GBP w ustawieniach, zgodne ze stylem aplikacji.
- Wybrana waluta dotyczy wszystkich kwot, historii, podsumowań i etykiet formularzy. Liczby pozostają bez zmian: 100 PLN po zmianie na EUR wyświetla się jako 100 EUR.
- Ustawienie zapisuje się lokalnie i w kopii JSON (`settings.currency`). Import przywraca walutę; starsze kopie bez tego pola domyślnie używają PLN.
- Język i walutę wybiera się niezależnie. Zgodnie z ustaleniem w interfejsie nie dodano podpowiedzi o przeliczaniu.
- Android: `versionCode 8`, `versionName 0.3.7`. Zmiany względem v0.3.6 dotyczą `index.html`, `app/build.gradle.kts` i README; plik MainActivity.kt z v0.3.6 pozostaje wymagany dla pełnej obsługi PL/EN.
- Testy logiki obejmują cztery waluty, zachowanie kwot, etykiety, zapis i odtworzenie ustawienia oraz import starszej kopii. APK nadal wymaga zbudowania i testu na telefonie.
