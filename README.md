# 🏍️ MotoGarage — historia wersji

Aplikacja MotoGarage na Androida do zarządzania motocyklami, ich serwisem, wydatkami oraz ulubionymi torami i miejscówkami do jazdy.

**Aktualna wersja: v0.3 Beta (w trakcie testów)**

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

## 🚀 v0.3 Beta — 13.11.2026
**Usprawnienia interfejsu i historii serwisowej**

### Poprawki interfejsu
- Zmieniono sposób pozycjonowania nagłówka względem systemowego paska Androida.
- Dostosowano położenie logo, nazwy MotoGarage i przycisku ustawień.

### Usprawnienia serwisu
- Dodano automatyczne pobieranie aktualnego stanu motogodzin wybranego motocykla podczas tworzenia wpisu serwisowego.
- Zachowano możliwość ręcznej edycji wartości motogodzin.
- Uwzględniono zmianę motocykla podczas wypełniania formularza.

### Techniczne
- Przygotowano zmiany w kodzie aplikacji i konfiguracji Androida.
- Zaktualizowano numer wersji do 0.3.
- Przygotowano nową kompilację testową APK.

**Status:** Wymaga końcowego potwierdzenia działania poprawek na telefonie.

---

## 📌 Planowane usprawnienia

- Systemowe powiadomienia o zbliżającym się serwisie — odłożone na później.
- Stały podpis APK umożliwiający aktualizację aplikacji bez odinstalowywania.
- Automatyczne nazewnictwo plików instalacyjnych, np. `MotoGarage-v0.4.apk`.
- Dalsze testy stabilności zapisu danych.
- Rozbudowa funkcji na podstawie uwag użytkowników.

---

**Projekt MotoGarage — aplikacja rozwijana etapami, na podstawie testów na urządzeniach Android.**
