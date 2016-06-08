/** Apka powstala dzieki githubowi (sklejka miliarda kodow) i stackoverflow
 *  bo oczywiscie nic nie chcialo dzialac jak mialo
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h> 
#include <netdb.h>
#include <time.h>

#define MAX_LINE_SIZE 50
/** print usage
 * Funkcja wyrzuca komentarz dotyczacy korzystania z aplikacji
 */
void print_usage(){
  fprintf(stderr, "Usage: ./client [hostname] [port]\n");
}
 /** Funkcja sprawdzajaca poprawnosc uruchomienia klienta
  */
void check_args(int argc, char *argv[]){
  if( argc != 3 ) {
    print_usage();
	exit(1);
  }
}
/** main function
 * 
 * powinno byc rozdzielenie na polaczenie i obsluge commitu ale wyszlo jak wyszlo.
 * 
 * Tworzenie socketow itd. Nudne rzeczy.
 * 
 * Klient podlacza sie do serwera i oczekuje na zgloszenie do glosowania. Funkcja rand generuje sygnal (tak lub nie) i przesyla do koordynatora po czym dostaje zwrotny komunikat o pomyslnosci zatwierdzenia lub odrzuceniu.
 */
int main(int argc, char *argv[])
{
 clock_t start = clock();
  
  int success;
  struct addrinfo hints;
  struct addrinfo *serverInfo;
  
  check_args(argc, argv);
  
  memset( &hints, 0, sizeof(hints) );
  hints.ai_family   = AF_INET;     
  hints.ai_socktype = SOCK_STREAM; 
  getaddrinfo(argv[1], argv[2], &hints, &serverInfo);
  
  int socketFD = socket( serverInfo->ai_family, serverInfo->ai_socktype, serverInfo->ai_protocol );
	if (socketFD == -1) {perror("Failed to create socket\n"); exit(1);}
  
printf("Waiting for coordinator request...\n");
   success = connect(socketFD, serverInfo->ai_addr, serverInfo->ai_addrlen);
	//Error checking
	if (success == -1) {perror("Failed to connect to server\n"); exit(1);}

  while(1){
  char line[MAX_LINE_SIZE];
  char text[MAX_LINE_SIZE];
  char quit[] = "quit";
  int encrypVal = -1;
  int arguments = 0;
  int len;
  char yes[] = "Y";
  char no[] = "N";

	int num_bytes_received = 0;
  	char processed[MAX_LINE_SIZE];
	num_bytes_received = recv(socketFD, processed, MAX_LINE_SIZE-1, 0);
	if(num_bytes_received == -1) {perror("Failed to properly receive data\n"); exit(1);}


	while (1){
  	//printf("Vote Initiated. Commit? \"Yes\" or \"No\" \n");
        srand( time( NULL ) );
  	int a = rand() % 2;
        if(a==0)
        {
            strncpy(line, "Yes", MAX_LINE_SIZE);
        }
        else
        {
            strncpy(line, "No", MAX_LINE_SIZE);
        }
        //fgets(line, MAX_LINE_SIZE + 1, stdin);
  	len = strlen(line);
	if(line[0] == 'Y' || line[0] == 'y' || line[0] == 'N' || line[0] == 'n') 	break;
	printf("Improper format\n");
	}

  printf("Sending response to '%s' using port '%s'... \n", argv[1], argv[2]);

if(line[0] == 'Y' || line[0] == 'y')
  	{
	success = send(socketFD, yes, len, 0);
	}

if(line[0] == 'N' || line[0] == 'n')
  	{
	success = send(socketFD, no, len, 0);
	printf("No commitement made. Exiting.\n");
	}
	if (success == -1) {perror("Failed to send data"); exit(1);}
 
	num_bytes_received = recv(socketFD, processed, MAX_LINE_SIZE-1, 0);
	if(num_bytes_received == -1) {perror("Failed to properly receive data\n"); exit(1);}

	if(processed[0] == 'C')
	printf("Commit Received!\n");

	else
	printf("Abort Received!\n");
break;

}

  freeaddrinfo(serverInfo);
  success = close(socketFD);
	if (success == -1) {perror("Failed to close socketFD\n"); exit(1);}
  
 clock_t end = clock();
 float seconds = (float)(end - start) / CLOCKS_PER_SEC;
 printf ("Your calculations took %.10lf seconds to run.\n", seconds );
  
  return EXIT_SUCCESS;
}
