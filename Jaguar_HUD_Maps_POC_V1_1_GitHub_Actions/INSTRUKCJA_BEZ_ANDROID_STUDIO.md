# Jaguar HUD Maps Probe — APK bez instalacji Android Studio na komputerze

**Uwaga: w archiwum NIE MA jeszcze pliku APK.** Projekt jest przygotowany do zbudowania go
przez GitHub Actions. Nie sprawdzono kompilacji na rzeczywistym runnerze GitHub.

## Jak uzyskać APK wyłącznie przez przeglądarkę

1. Zaloguj się do https://github.com (konto GitHub jest wymagane).
2. Utwórz nowe **prywatne** repozytorium na https://github.com/new. Wybierz
   `Add a README file`, aby utworzyć domyślną gałąź `main`.
3. Rozpakuj `Jaguar_HUD_Maps_POC_V1_1_GitHub_Actions.zip` na komputerze.
   Nie wymaga to instalowania żadnego programu poza zwykłą obsługą ZIP.
4. W nowym repozytorium wybierz `Add file` → `Upload files`. Przeciągnij do przeglądarki
   **zawartość** rozpakowanego folderu, włącznie z ukryto nazwaną ścieżką
   `.github/workflows/zbuduj-apk.yml`. GitHub pozwala przeciągnąć foldery.
   UWAGA: nie wgrywaj samego pliku ZIP jako zamiennika plików projektu — wtedy
   kompilator nie zobaczy kodu aplikacji.
5. Zatwierdź przesyłanie (`Commit changes`). Samo zatwierdzenie uruchamia kompilację.
6. W repozytorium wybierz zakładkę `Actions` → zadanie
   `Zbuduj APK Jaguar HUD Maps Probe`. Status powinien po pewnym czasie zmienić się na
   zielony (jeśli kompilacja przebiegnie poprawnie).
7. Otwórz ukończone zadanie i w sekcji `Artifacts` pobierz
   `Jaguar-HUD-Maps-Probe-debug-APK` (ZIP z plikiem `app-debug.apk`).
8. Przenieś APK na swój prywatny telefon z Androidem i zainstaluj.
   System może wymagać świadomego zezwolenia przeglądarce lub aplikacji Pliki
   na instalowanie APK z tego źródła.

## Uwaga o uprawnieniach

Aplikacja nie potrzebuje Internetu ani dostępu do samochodu, ale wymaga zgody na
**dostęp do powiadomień**. Android może zablokować tę zgodę dla instalacji spoza sklepu;
po świadomym sprawdzeniu aplikacji można w szczegółach aplikacji zezwolić na
`Ograniczone ustawienia`, jeśli dany telefon pokazuje takie menu. Wybieraj ten dostęp
wyłącznie dla aplikacji, której kod znasz i akceptujesz.

Po teście A (Google Maps na telefonie) i B (Google Maps przez Android Auto)
wyeksportuj osobne dzienniki JSON, a potem wyłącz dostęp do powiadomień.
Dzienniki mogą zawierać fragmenty trasy oraz nazwy ulic.

## Ważne ograniczenia

- To **prototyp diagnostyczny**, a nie moduł do wyświetlania wskazówek na HUD.
- To wersja **debug APK**, podpisywana automatycznie testowym kluczem podczas kompilacji.
- Jeśli zbudujesz APK ponownie, inny podpis może uniemożliwić aktualizację w miejscu;
  wtedy usuń poprzednią wersję przed instalacją następnej (utracisz jej dziennik).
- W systemie, w którym pracuję, nie ma Android SDK, dlatego **nie potwierdziłem,
  że APK daje się skompilować lub uruchomić na telefonie**.
- W przypadku błędu otwórz czerwone zadanie w `Actions` i sprawdź logi budowania.
- Nie wysyłaj surowych dzienników zawierających trasy lub adresy do publicznego repozytorium.
