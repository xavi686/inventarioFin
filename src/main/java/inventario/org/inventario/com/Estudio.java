package inventario.org.inventario.com;

/*import java.util.ArrayList;
import java.util.List;

public class Estudio {
    public static void main(String[] args) {

    	List<String> paises = new ArrayList<>();

        // Agregar elementos a la lista
        paises.add("Colombia");
        paises.add("Brasil");
        paises.add("Argentina");
        paises.add("Perú");

        // Imprimir la lista
        System.out.println("Lista de paises: " + paises);

        // Acceder a un elemento por su posición
        String pais1 = paises.get(0);
        System.out.println("El primer país es: " + pais1);

        // Modificar un elemento en una posición específica
        paises.set(1, "México");
        paises.set(3, "Ecuador");

        // Agregar un elemento
        paises.add("Chile");

        System.out.println("Lista de paises actualizada: " + paises);

        // Eliminar un elemento por su posición
        paises.remove(2);
        System.out.println("Lista de paises después de eliminar: " + paises);

    }
}
*/

/*import java.util.Stack;

public class Estudio {
    public static void main(String[] args) {
        // Crear una pila
        Stack<String> paises = new Stack<>();

        // Agregar elementos a la pila (Push)
        paises.push("Colombia");
        paises.push("Brasil");
        paises.push("Argentina");
        paises.push("Perú");
        
        // Imprimir la pila
        System.out.println("Paises: " + paises);

        // Ver el elemento en la cima de la pila sin eliminarlo (Peek)
        String cima = paises.peek();
        System.out.println("El elemento en la cima es: " + cima);

        // Eliminar el elemento en la cima de la pila (Pop)
        String eliminado = paises.pop();
        System.out.println("Elemento eliminado: " + eliminado);

        // Imprimir la pila después de la eliminación
        System.out.println("Pila después de eliminar un elemento: " + paises);

    }
}
*/

import java.util.LinkedList;
import java.util.Queue;

public class Estudio {
    public static void main(String[] args) {
        // Crear una cola de enteros
        Queue<String> paises = new LinkedList<>();

        // Agregar elementos a la cola (Enqueue)
        paises.offer("Colombia");
        paises.offer("Brasil");
        paises.offer("Argentina");
        paises.offer("Perú");

        // Imprimir la cola
        System.out.println("Paises: " + paises);

        // Ver el primer elemento sin eliminarlo (Peek)
        String primero = paises.peek();
        System.out.println("El primer elemento es: " + primero);

        // Eliminar el primer elemento (Dequeue)
        String eliminado = paises.poll();
        System.out.println("Elemento eliminado: " + eliminado);

        // Imprimir la cola después de la eliminación
        System.out.println("Cola después de eliminar un elemento: " + paises);

    }
}
