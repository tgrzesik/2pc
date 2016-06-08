
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#define MAX_LINE_SIZE 50
#define LISTEN_QUEUE 5
#define NUM_THREADS 5
int success;
int num_clients = 0;
int ready_clients = 0;

/** Funkcja wyswietlajaca korzystanie z serwera
 */

void print_usage(){
  fprintf(stderr, "Usage: ./server [port]\n");
}
 
 /** Funkcja sprawdza czy program odpalono z dwoma argumentami (port).
  */
void check_args(int argc, char *argv[]){
  if( argc != 2 ) {
    print_usage();
	exit(1);
  }
}

/** server function
 * 
 * Po podlaczeniu dwoch klientow serwer rozsyla zaproszenie do glosowania.
 * Po tym porownuje wiadomosci z drugim klientem i rozsyla wiadomosc globalna do kazdego klienta (+ zabija watki)
 */

void *serverFunction(void* args){
 printf("A client connected\n");
	int recvFD;
	recvFD = (int) args;
	num_clients++;
	if(num_clients < NUM_THREADS)
		 printf("Waiting for another client\n");
	while(num_clients < NUM_THREADS)
	{}


	while(1){
   	char line[MAX_LINE_SIZE];   
   	char message[MAX_LINE_SIZE];   
	int offset = 0;
	int len;
  	char vote[] = "Vote";
	char abort[] = "Abort";
	char commit[] = "Commit";
/*
	while (1){
  	printf("Press \"V\" when ready to vote \n");
  	fgets(line, MAX_LINE_SIZE + 1, stdin);
	if(line[0] == 'V') break;
	printf("Improper format \n");
	}
*/

  	len = strlen(vote);
  	success = send(recvFD, vote, len, 0);
        
	if (success == -1) {perror("Failed to send data \n"); exit(1);}

        int ret;
        fd_set set;
        struct timeval timeout;
        FD_ZERO(&set);
        FD_SET(recvFD, &set);

        
        timeout.tv_sec = 30;
        timeout.tv_usec = 0;

        int num_bytes_received = 0;
        ret = select(recvFD+1, &set, NULL, NULL, &timeout);
        if (ret == 1)
        {
            num_bytes_received = recv(recvFD, line, MAX_LINE_SIZE-1, 0);
                if(line[0] == 'Y')
                {
                    ready_clients++;
                }

                if(line[0] == 'N')
                {
                ready_clients = -10;
	
                
               
                len = strlen(abort);
                success = send(recvFD, abort, len, 0);
                //	if (success == -1) {perror("Failed to send data"); exit(1);}

                printf("A client disconnected\n");
                num_clients--;
                success = close(recvFD);
                    if(success == -1) {perror("Failed to close socket"); exit(1);}
                pthread_exit(NULL);
                }
        } 
        else if (ret == 0) {
            ready_clients = -10;
            len = strlen(abort);
            success = send(recvFD, abort, len, 0);
            //	if (success == -1) {perror("Failed to send data"); exit(1);}

            printf("A client disconnected\n");
            num_clients--;
            success = close(recvFD);
                if(success == -1) {perror("Failed to close socket"); exit(1);}
            pthread_exit(NULL);
}
else {
    if(num_bytes_received == -1) {perror("Failed to properly receive data \n"); exit(1);}
}


while (ready_clients < NUM_THREADS)
	{
	if(ready_clients < -5 )
		{
  		len = strlen(abort);
  		success = send(recvFD, abort, len, 0);
		if (success == -1) {perror("Failed to send data"); exit(1);}

  		printf("A client disconnected\n");
		num_clients--;
    		success = close(recvFD);
	    	if(success == -1) {perror("Failed to close socket"); exit(1);}
		pthread_exit(NULL);
		}	
	}

  	len = strlen(commit);
  	success = send(recvFD, commit, len, 0);
	if (success == -1) {perror("Failed to send data"); exit(1);}
  
	}
  printf("A client disconnected\n");
	num_clients--;
    	success = close(recvFD);
	    if(success == -1) {perror("Failed to close socket"); exit(1);}
	pthread_exit(NULL);
}

/** main function
 * 
 * tworzenie socketow i watkow dla kazdego klienta
 * 
 * kompletny syf jesli chodzi o kod - mieszanka wszystkiego co sie da. mozliwe ze polowe mozna wywalic.
 */
int main(int argc, char *argv[])
{
  int created_threads = 0;
  char space[] = " ";
  int success; 
  struct addrinfo hints;
  struct addrinfo *serverInfo;
  check_args(argc, argv);
  
  memset( &hints, 0, sizeof(hints) );
  hints.ai_flags    = AI_PASSIVE;  
  hints.ai_family   = AF_INET;     
  hints.ai_socktype = SOCK_STREAM;

  getaddrinfo( NULL , argv[1], &hints, &serverInfo );
  
  int socketFD = socket( serverInfo->ai_family, serverInfo->ai_socktype, serverInfo->ai_protocol );
	if (socketFD == -1) {perror("Failed to create socket"); exit(1);}
	
  success = bind( socketFD, serverInfo->ai_addr, serverInfo->ai_addrlen );
	if (success == -1) {perror("Failed to bind to port"); exit(1);}
  
  int yes = 1;
  setsockopt( socketFD, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int) );
  
  pthread_t threads[NUM_THREADS];
  success = listen(socketFD, LISTEN_QUEUE);
	if (success == -1) {perror("Falied to begin listening"); exit(1);}


  printf("Listening in port %s... \n", argv[1]);
  fflush(stdout);
  
  freeaddrinfo(serverInfo);
  

  int recvFD;
  fd_set fds;
  FD_ZERO(&fds);
  FD_SET(socketFD, &fds);
  while (1) {
	int numOfFD = select(socketFD +1, &fds, NULL, NULL, 0);
   	if( select(FD_SETSIZE, &fds, NULL, NULL, 0) < 1)
	{
		sleep(1);
		continue;
	}

  	struct sockaddr_storage clientAddr;
  	socklen_t clientAddr_size = sizeof(clientAddr);

  	recvFD = accept(socketFD, (struct sockaddr *)&clientAddr, &clientAddr_size);
	if(recvFD == -1 ) {perror("Failed to accept a connection"); exit(1);}

pthread_create( &threads[created_threads % NUM_THREADS], NULL, serverFunction, (void *)recvFD );
created_threads++;
  }

  success = close(socketFD);
	if(success == -1) {perror("Failed to close socket"); exit(1);}
  
  return EXIT_SUCCESS;
}
