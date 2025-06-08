***********************************************************************************************************************************************************************************
Projekat mobilne aplikacije u javi - Seminarski rad
***********************************************************************************************************************************************************************************
Početna forma:
  - Prilikom pokretanja aplikacije prikazuje se navigacioni bar sa menijem i stranicom koja vrši funkcionalnost snimanja glasovnih beleški.
  - Klikom na dugme za snimanje uključuje se mikrofon i beleži audio zapis koji se čuva u memoriji uređaja u folderu aplikacije.
  - Unutar menija nalaze se još i forme: Pregled snimaka, Činjenice o mačkama, Promena režima prikaza i Izlaz.

Pregled snimaka:
  - Ova forma sadrži listu .3gp snimaka koji su sačuvani u folderu aplikacije na memoriji uređaja.
  - Tapom na određeni snimak snimak se reprodukuje. Dodatnim tapom snimak se pauzira ili startuje reprodukcija sledećeg.
  - Dugme nazad vraća korisnika na početnu formu.

Činjenice o mačkama:
  - Na ovoj formi nalazi se prikaz teksta, dugme za pregled nove činjenice o mačkama i dugme za povratak na početnu formu.
  - Tapom na dugme za prikaz činjenice o mačkama poziva se http klijent koji odlazi na API: https://catfact.ninja/fact gde preuzima random činjenicu i prikazuje u tekst polje.
  - Svakim tapom na dugme pokreće se poseban proces u drugom thread-u koji preuzima sledeću činjenicu i smeštaje u tekstualno polje.
  - Dugme nazad vraća korisnika na početnu formu.

Promena režima prikaza:
  - Ova akcija pokreće prebacivanje sa light na dark mode aplikacije i obrnuto.
  - Vrednosti za dark i light mode se smeštaju u SharedPreferences tako da se podaci čuvaju i nakon zatvaranja aplikacije.
  - Kada se aplikacija uključi ovaj podatak se preuzima iz ove memorije i u zavisnosti od toga šta je korisnik poslednje izabrao pokreće mu se željeni režim pregleda.

Izlaz:
  - Akcija izlaska iz aplikacije.
