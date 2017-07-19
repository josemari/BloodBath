# BloodBath
Recopilación de ejemplos que pueden ser de utilidad en el área de la programación gráfica y el desarrollo de videojuegos. Se utiliza Java y OpenGL tanto con la biblioteca LWJGL como con JOGL.
Básicamente son cuatro ejemplos:
1.- Un ejemplo de animación de un modelo del Quake II.
2.- Un ejemplo de animación de un modelo del Quake III.
3.- Dos ejemplos distintos que muestran formas diferentes de cargar y representar un nivel del Quake III. No obstante, la funcionalidad está limitada en ambos, ya que el jugador se limita a "volar" alrededor del nivel mientras se mueve, puesto que no hay gravedad.
El proyecto al compilar genera un .zip autónomo con el código compilado y los recursos que necesita. También incluye dos scripts, uno para Windows y otro para Linux. Para cambiar qué ejemplo se desea ejecutar, hay que indicar la clase principal a ejecutar en el pom.xml y recompilar el proyecto. He utilizado Maven 3, NetBeans 8.2 y Java 8.
