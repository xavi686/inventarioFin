package inventario.org.inventario.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Scanner;

public class Prueba {

    public static void main(String[] args) {
            String string = "To become a programmer, you need to write code. To write code, you have to learn. To learn, you need desire.";
            String word = "code";
            int indexOfFirstWord = getIndexOfFirstWord(string, word);
            int indexOfLastWord = getIndexOfLastWord(string, word);
            System.out.println("The index of the first character of the first instance of the word \"" + word + "\" is " + indexOfFirstWord);
            System.out.println("The index of the first character of the last instance of the word \"" + word + "\" is " + indexOfLastWord);
        }

        public static int getIndexOfFirstWord(String string, String word) {
            //escribe aquí tu código
        	int index = string.indexOf(word);
            return index;
        }

        public static int getIndexOfLastWord(String string, String word) {
            //escribe aquí tu código
        	int index = string.lastIndexOf(word);
        	System.out.println("god news everyone!".replaceAll("o.", "-o-"));
            int b = 128;
            long s = 32768;
            int i = 1234567890;
            long l = 2_345_678_900L;
            return index;
        }

}