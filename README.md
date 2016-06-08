#Opis algorytmu

Aplikacja zostala napisana w C i sluzy do zaprezentowania dzialania algorytmu 2pc.
Dziala on na zasadzie polaczenia klienta z serwerem i zaglosowaniu (tak lub nie).
Serwer zbiera wszystkie glosy i jesli glosy sa jednomyslne na tak, to commit
 zostaje zatwierdzony, w przeciwnym wypadku odrzucony.

Sama budowa algorytmu jest prosta, tzn. jest to zwykla wymiana komunikatow pomiedzy dwoma
aplikacjami, jednak do ich wymiany potrzebne bylo skonstruowanie struktury, ktora
umozliwialaby ta komunikacje. Z pomoca na rozwiazanie tego problemu przyszly technologie
gniazd i watkow. Jezyk C jest jezykiem stosunkowo niskopoziomowym, co sprawia, ze podczas
pisania aplikacji trzeba bylo zadbac o bardzo duzo szczegolow zwiazanych z tworzeniem polaczenia.
Z jednej strony jest to duzy naklad linii kodu, ktory nie pozostaje bez znaczenia dla
wydajnosci, z drugiej potrzebna jest duza wiedza na temat programowania i sieci komputerowych,
by aplikacja mogla bezproblemowo dzialac. Aby mozliwa byla komunikacja serwera z wieloma
klientami jednoczesnie potrzebne byly watki. Kazdy stworzony socket nasluchiwal w osobnym watku,
dzieki czemu nie dochodzilo do sytuacji, kiedy dwoch klientow odwolywalo sie jednoczesnie do
jednego gniazda.

Sredni czas dzialania aplikacji to 0,0039 sekundy. Jest to krotki czas, jednak inne
technologie moga z nim konkurowac, co przy zsumowaniu z nakladem pracy sprawia, ze
oparcie algorytmu o ta technologie nie jest najlepszym rozwiazaniem.