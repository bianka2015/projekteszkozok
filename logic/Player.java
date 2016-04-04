/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

/**
 *
 * @author Acer
 */
public class Player {
    private int inHand; // Kezben levo babuk szama
    private int onBoard; // Palyan levo babuk szama
    private boolean allPut; // Befejezodott-e a lerako fazis
    private boolean jumping; // Ugralhat-e
    private int number; // Azonosito
    
    public Player(int number) { // Konstruktor
        inHand = 9;
        onBoard = 0;
        allPut = false;
        jumping = false;
        this.number = number;
    }
    
    public int getInHand() { // Visszaadja a kezben levo babuk szamat
        return inHand;
    }
    
    public int getOnBoard() { // Visszaadja a palyan levo babuk szamat
        return onBoard;
    }
    
    public boolean isAllPut() { // Megadja, hogy befejezodott-e a lerako fazis
        return allPut;
    }
    
    public boolean isJumping() { // Megadja, hogy ugralhat-e
        return (allPut && onBoard+inHand==3);
    }
    
    public int getNumber() { // Visszaadja a jatekos azonositojat
        return number;
    }
    
    public void setInHand(int x) { // Beallitja a jatekos kezeben levo babuk szamat
        inHand = x;
        if (inHand == 0) {
            allPut = true;
        }
    }
    
    public void setOnBoard(int x) { // Beallitja a jatekos palyan levo babuinak a szamat
        onBoard = x;
    }
}
