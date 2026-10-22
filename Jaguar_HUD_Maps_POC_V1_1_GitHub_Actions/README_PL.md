# Jaguar XF X260 — diagnostyka manewrów Google Maps (V1.1)

**Cel:** sprawdzić, czy telefon z Androidem dostarcza aktualizacje manewrów Google Maps
w postaci powiadomień zarówno **bez Android Auto**, jak i **po podłączeniu Android Auto**.
To jest projekt źródłowy Android Studio, **nie gotowy plik APK**. W bieżącym środowisku
nie było zainstalowanego Android SDK ani narzędzia Gradle, dlatego nie przeprowadzono
pełnej kompilacji oraz testu na fizycznym telefonie.

## Budowanie APK bez Android Studio
Zobacz `INSTRUKCJA_BEZ_ANDROID_STUDIO.md`. Projekt zawiera teraz konfigurację GitHub Actions umożliwiającą kompilację APK online. Wciąż nie zawiera gotowego APK.

## Zawartość
- Prosta aplikacja Java bez zewnętrznych bibliotek działających w telefonie.
- `NotificationListenerService`, zbierający **tylko** pakiet `com.google.android.apps.maps`.
- Zapisywanie maks. 200 zdarzeń (tytuł, tekst, kanał, linie, czas; ewentualnie nazwy dróg).
- Podgląd najnowszego zdarzenia oraz orientacyjne rozpoznawanie strony skrętu i odległości.
- Eksport historii do JSON przez systemowy wybór pliku i kasowanie lokalnej historii.
- Bez uprawnień Internet, GPS, Bluetooth, CAN/OBD i bez wysyłania danych.

## Budowanie
1. Otwórz projekt w Android Studio (`File > Open` i katalog `Jaguar_HUD_Maps_POC_V1`).
2. Potrzebne są Android SDK Platform 35, Android Gradle Plugin 8.9.2, Gradle 8.11.1, JDK 17+.
   Projekt zawiera `gradle/wrapper/gradle-wrapper.properties`, ale **nie** zawiera
   pliku binarnego `gradle-wrapper.jar` ani skryptów wrappera; jeśli IDE nie potrafi
   zsynchronizować projektu, wygeneruj wrapper w lokalnym Gradle lub utwórz nowy projekt
   Java/Empty Views Activity i skopiuj `app/src/main` oraz źródła z tego pakietu.
3. Uruchom na telefonie z Androidem 9+; IDE zbuduje debug APK lokalnie.
   Aplikacja testowa nie wymaga publikacji w Google Play i nie pojawia się w Android Auto.
4. W aplikacji wybierz `Zezwól na dostęp do powiadomień` i włącz uprawnienie.
   Na części telefonów po instalacji spoza sklepu system blokuje tę kategorię ustawień;
   można wtedy potrzebować dodatkowego świadomego zatwierdzenia w ustawieniach aplikacji.

## Test A — bez samochodu
1. Usuń dziennik w aplikacji, włącz dostęp do powiadomień.
2. Uruchom nawigację Google Maps na telefonie, najlepiej jako pasażer lub na postoju;
   nie wymuszaj symulacji ruchu podczas prowadzenia.
3. Przejdź przez kilka manewrów (jazdę wykonuje kierowca, telefon obsługuje pasażer).
4. Sprawdź, czy licznik zdarzeń rośnie i czy `text/title` zawierają aktualne manewry.
5. Eksportuj plik jako `test_A_bez_AA.json`.

## Test B — z Android Auto w Jaguarze
1. Wymaż poprzedni dziennik. Pozostaw zezwolenie na powiadomienia.
2. Podłącz telefon do oryginalnego Android Auto. Uruchom Google Maps z trasą.
3. Podczas przejazdu nie dotykaj telefonu jako kierowca. Po zakończeniu trasy,
   na postoju, odczytaj licznik zdarzeń i eksportuj `test_B_z_AA.json`.
4. Porównaj liczby zdarzeń, zawartość tekstu, typy manewrów i częstotliwość aktualizacji.
   **Ważne:** powiadomienia na samym Android Auto i na Androidzie telefonu są różnymi
   mechanizmami. Sukces A NIE oznacza automatycznie sukcesu B.

## Kryterium przejścia do etapu 2
- PASS warunkowy: **podczas testu B** pojawiają się prawidłowe i wystarczająco częste
  zdarzenia z kierunkiem i odległością przed kolejnym manewrem.
- PARTIAL: są zdarzenia, ale bez ikon manewru, z opóźnieniami lub sporadyczne.
- STOP dla tej metody: w B brak przydatnych zdarzeń albo dane są zbyt przestarzałe.
  Wtedy należy zbadać inny kanał pozyskiwania nawigacji, nie przechodzić do CAN.

## Prywatność i bezpieczeństwo
Oprogramowanie nie modyfikuje samochodu. Wymaga jednak szerokiego **systemowego**
uprawnienia do odczytywania powiadomień (Android udziela dostępu do powiadomień wielu
aplikacji); kod zapisuje wyłącznie zdarzenia pochodzące od Google Maps. Dane mogą zawierać
nazwy ulic, a eksport może ujawniać trasę. Przed przesłaniem komuś pliku usuń dane prywatne.
Po teście odbierz uprawnienie i skasuj historię w aplikacji. Nie traktuj heurystyk
LEWO?/PRAWO? ani odległości z prototypu jako bezpiecznych wskazówek prowadzenia.

## Źródła i ograniczenia
- Oficjalne API Android NotificationListenerService:
  https://developer.android.com/reference/android/service/notification/NotificationListenerService
- Przykład rozwiązania problemu — GMapsParser (LGPL-3.0), niezależna implementacja:
  https://github.com/3v1n0/GMapsParser
- Inny prototyp z odczytem powiadomień na Androidzie 15:
  https://github.com/CitizenOneX/gmaps_nav_hud_frame
- Dokumentacja XF X260: IMC → HUDCM sterowany po HS-CAN comfort (bez publicznego
  potwierdzenia identyfikatorów i formatów konkretnych ramek):
  https://lemon-manuals.la/Jaguar/2018/XF%20R-Sport%2C%203.0L%20Eng%20VIN%20V%2C%20RWD/Repair%20and%20Diagnosis/Accessories%20%26%20Equipment/Infotainment/Information%20%26%20Entertainment%20System%20--%20XF%2FX260/Information%20And%20Entertainment%20System/Description%20And%20Operation%20%28Navigation%20System%29/Operation/Navigation%20System/

Nie kopiowano kodu z GMapsParser; ten prototyp jest własną implementacją
z wykorzystaniem publicznego API systemu Android.
