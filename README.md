# Metodologie di Programmazione, Progetto 2: Traffic

Traffic è una reimplementazione di un vecchio gioco per Commodore 64.

## TO-DO

- Studiare la documentazione di FXGL 11.9;
- Strutturare le dinamiche di gioco;
- Dividere il progetto in parti atomiche.

01/09/2020
1.Implementare accelerazione e decelerazione:
	x modificare le funzioni accelerate() e slowDown() in modo da avere una accelerazione costante

	x modificare la hitbox del semaforo se necessario
	-controllare che le collisioni con gli incroci e tra le macchine funzionino
		Warning: spesso succede che la macchina dietro non parta !!!!


2.Aggiungere le frecce:  FATTO!!!!!
	x si devono attivare al contatto con una hitbox(possibilmente secondaria del semaforo)
	x bisogna capire quale delle due frecce attivare
	x le frecce sono due rettangoli sul veicolo che cambiano la loro visibilità ogni tot centesimi di secondo nel metodo onUpdate().
	x le frecce si devono disattivare al completamento della curva


3.Non far collidere le macchine:
	x aggiunto campo che regola il passaggio delle macchine nel momento in cui una svolta a sx

	-quando una macchina gira a sx deve recuperare il semaforo a lei opposto.
	-per non far collidere le macchine bisogna far aspettare i veicoli che girano 
	 a sx il momento in cui il semaforo opposto non collide con nessun veicolo.
	-una volta che la macchina può girare deve modificare un campo nel semaforo opposto 
	 in modo da non farsi intralciare dalle altre macchine (rendendo quindi il campo false)
	-al completamento della curva la macchina deve rendere quel campo del semaforo di nuovo true
	
## Useful links

- <a href="https://docs.google.com/document/d/1hAu8wDXjqYgv10epFFH_dbAOVaOhfp2o3LUO93ofPFU/edit#" target=”_blank”>Project sheet</a>
- <a href="https://www.youtube.com/watch?v=9wmu5R4kdY0" target=”_blank”>Gameplay Traffic</a>
- <a href="https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp" target=”_blank”>FXGL Tutorials</a>
