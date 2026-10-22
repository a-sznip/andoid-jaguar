# Etap 2 — Jaguar XF X260: walidacja toru HUD (bez ingerencji)

## Punkt wyjścia
- XF X260 model 2017, InControl Touch Pro, Android Auto fabryczne.
- Cyfrowe 12,3 cala pokazują fabryczną mapę.
- Fabryczny HUD najprawdopodobniej pokazuje wskazówki z nawigacji Jaguara,
  ale nie otrzymuje wskazówek Google Maps.
- W dokumentacji serwisowej wskazano IMC → HUDCM po HS-CAN comfort.
  Dokładne formaty, identyfikatory i możliwość emulowania poleceń są NIEZWERYFIKOWANE.

## Zależności i bramki
1. **Telefon:** zebrać test A i B aplikacją diagnostyczną i potwierdzić stabilność
   danych, bez wykorzystywania logów z jazdy jako podpowiedzi kierowcy.
2. **Warunki logiczne:** ustalić co robić przy utracie zdarzeń > kilka sekund,
   zmianie trasy, brakującym kierunku, zdarzeniu nieaktualnym i zerwaniu Android Auto.
   Domyślna odpowiedź systemu to brak dodatkowych wskazówek, nigdy stara strzałka.
3. **Samochód:** zlecić uprawnionemu specjaliście nieinwazyjny/pasywny pomiar CAN
   na profesjonalnym stanowisku lub w odpowiednio zabezpieczonych warunkach; skorelować
   aktywację fabrycznych wskazówek na HUD z komunikatami, bez ich nadawania.
4. **Walidacja:** oddzielić wiadomości nawigacyjne od komunikatów prędkości,
   ACC, ograniczeń prędkości, usterek i statusów krytycznych.
5. **Laboratorium:** dopiero po poznaniu protokołu i zakresu bezpieczeństwa
   można rozważyć emulację na odizolowanym module HUDCM / stanowisku, bez pojazdu.
6. **Stop:** nieznany protokół z sumami kontrolnymi/licznikami, brak stabilnych
   powiadomień z Google Maps, wpływ na dane krytyczne lub brak możliwości powrotu
   do stanu fabrycznego przekreślają projekt w tej architekturze.

## Wariant awaryjny bez integracji
Google Maps + Yanosik z ekranem centralnym i audio,
fabryczna mapa Jaguara na zegarach/HUD (bez obietnicy zgodności tras).

## Wariant komercyjny
Przed zakupem wymagać demonstracji **oryginalnego XF X260 Touch Pro z lat 2016–2019**,
manewrów z Google Maps widocznych na oryginalnym HUD oraz udokumentowanego
bezpieczeństwa, odwracalności i braku ingerencji w ostrzeżenia systemowe.
