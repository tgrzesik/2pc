#include <stdlib.h>
#include <stdio.h>
#include <mpi.h>

#define VOTING 0
#define COMMIT 1
#define ROLLBACK 0

void persistState(int state) {
}

int main(int argc, char* argv[]) {
  int n, id, state, result;
  MPI_Init(&argc, &argv);
  MPI_Comm_rank(MPI_COMM_WORLD, &id);
  MPI_Comm_size(MPI_COMM_WORLD, &n);

  double time = MPI_Wtime();
  
  state = VOTING;
  persistState(state);

  MPI_Bcast(&state, 1, MPI_INT, 0, MPI_COMM_WORLD); //rozpoczecie procesu glosowania

  //TODO:dzialanie
  if (id >= 0) { //wykonaj dzialanie zwiazane z transakcja
    state = COMMIT;
  } else { //odrzuc dzialanie i zwroc niewykonanie transakcji
    state = ROLLBACK;
  }
  persistState(state);

  MPI_Reduce(&state, &result, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);
  /*MPI_Request request;
  MPI_Status status;
  MPI_Ireduce(&state, &result, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD, &request);
  MPI_Wait(&request, &status);
  printf("My value is %d and id=%d.\n", result, id);*/
  if (id == 0) { //decyzje podejmuje tylko koordynator
    if(result == n) {
      state = COMMIT;
    } else {
      state = ROLLBACK;
    }
    persistState(state);
  }

  MPI_Bcast(&state, 1, MPI_INT, 0, MPI_COMM_WORLD); //kazdy proces dostaje wynik transakcji
  if (state == COMMIT) {
    //TODO: commit transaction
  } else {
    //TODO: deny transaction
  }
  persistState(state);
  
  time = MPI_Wtime() - time;

  if (id == 0) printf("Transaction state is %d.\n", state);
  if (id == 0) printf("Time: %f.\n", time);

  MPI_Finalize();
  return 0;
}
