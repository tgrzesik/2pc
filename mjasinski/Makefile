CC = mpicc

program: program.o
	$(CC) program.o -o program
program.o: program.c
	$(CC) -c program.c
clear:
	rm *.o
	rm program
run:
	mpirun -np 8 ./program
