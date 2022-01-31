
# Compilation:
#  Options de javac:
#   -d : repertoire dans lequel sont places les .class compiles
#   -classpath : repertoire dans lequel sont cherches les .class deja compiles
#   -sourcepath : repertoire dans lequel sont cherches les .java (dependances)

all:
	javac -d bin -classpath ojdbc6.jar -sourcepath src src/*


# Execution:
# on peut taper directement la ligne de commande :
#   > java -classpath bin TestGUI
# ou bien lancer l'execution en passant par ce Makefile:
#   > make ApplicationBD

ApplicationBD:
	java -classpath bin:ojdbc6.jar ApplicationBD

ApplicationUser:
		java -classpath bin:ojdbc6.jar ApplicationUser

SimpleQuery :
	java -classpath bin:ojdbc6.jar SimpleQuery

clean:
	rm -rf bin/*.class
