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
public class Logic {
    private int[][] board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int lastPickedX;
    private int lastPickedY;
    
    public Logic() {
        // Beallitja a payla mezoit. -1, ha oda nem rakhat a jatekos (igazabaol nem resze a malom palyanak), 
        // 0, ha rakhat oda a jatekos es -2 jeloli a palya kozepet (oda sem rakhatnak babut)
        board = new int[7][7];
        for (int i=0; i<7; i++) {
            for (int j=0; j<7; j++) {
                board[i][j] = -1;
            }
        }
        
        board[0][0] = 0;
        board[0][3] = 0;
        board[0][6] = 0;
        
        board[1][1] = 0;
        board[1][3] = 0;
        board[1][5] = 0;
        
        board[2][2] = 0;
        board[2][3] = 0;
        board[2][4] = 0;
        for (int i=0; i<7; i++) {
            board[3][i] = 0;          
        }
        board[3][3] = -2;
        
        board[4][2] = 0;
        board[4][3] = 0;
        board[4][4] = 0;
        
        board[5][1] = 0;
        board[5][3] = 0;
        board[5][5] = 0;
        
        board[6][0] = 0;
        board[6][3] = 0;
        board[6][6] = 0;
        
        player1 = new Player(1);
        player2 = new Player(2);
        currentPlayer = player1;
        lastPickedX = -1;
        lastPickedY = -1;
    }
    
    public void put(int i, int j) { // A megadott sor, oszlop indexu helyre babut rak
        if (currentPlayer.isAllPut() && lastPickedX != -1) { // Mar nem a lepakolo fazisban van, hanem a mozgatoban
            move(i, j);
        }
        else if(board[i][j] == 0) { // Szabad helyre probalja rakni a babut
            board[i][j] = currentPlayer.getNumber();
            currentPlayer.setInHand(currentPlayer.getInHand()-1);
            currentPlayer.setOnBoard(currentPlayer.getOnBoard()+1);
            if (!malom(i, j)) {
                changePlayer();
            }
        }
    }
    
    public void move (int i, int j) { // Mozgato fazisban a sor, oszlop indexu helyre rakja a babut
        if (!(lastPickedX==i && lastPickedY==j)) { // Nem ugyanoda rakja, ahonnan az elobb folvette 
            boolean canPut = false;
            if (!currentPlayer.isJumping()) { // A jatekos meg nem ugralhat
                // Ures mezore akar rakni, valamint a mezo szomszedos azzal, ahonnan a legutobb folvette a babut
                if (board[i][j] == 0 &&
                    ((up(lastPickedX, lastPickedY)[0] == i && up(lastPickedX, lastPickedY)[1] == j) || 
                    (down(lastPickedX, lastPickedY)[0] == i && down(lastPickedX, lastPickedY)[1] == j) || 
                    (right(lastPickedX, lastPickedY)[0] == i && right(lastPickedX, lastPickedY)[1] == j) || 
                    (left(lastPickedX, lastPickedY)[0] == i && left(lastPickedX, lastPickedY)[1] == j))) {

                    board[i][j] = currentPlayer.getNumber();
                    canPut = true;
                }
                else { // Nem jo helyre akart rakni, visszahelyezodik a babu oda, ahonnan folvette
                    board[lastPickedX][lastPickedY] = currentPlayer.getNumber();
                    canPut = false;
                }
            }
            else if (board[i][j] == 0) { // A jatekos ugralhat es ures helyre akar rakni
                board[i][j] = currentPlayer.getNumber();
                canPut = true;
            }
            else { // Ugralhat, de nem ures helyre akar rakni, ugyhogy visszahelyezodik a babu oda, ahonnan folvette
                board[lastPickedX][lastPickedY] = currentPlayer.getNumber();
                canPut = false;
            }
            currentPlayer.setInHand(0);
            currentPlayer.setOnBoard(currentPlayer.getOnBoard()+1);
            
            if (canPut && !malom(i,j)) {
                changePlayer();
            }
            else {
                changePlayer();
                changePlayer();
            } 
        } 
        else { // Ugyanoda rakja, ahonnan folvette, ez nem minosul lepesnek, ujra ugyanaz a jatekos jon
            board[i][j] = currentPlayer.getNumber();
            currentPlayer.setInHand(0);
            currentPlayer.setOnBoard(currentPlayer.getOnBoard()+1);
        }
        lastPickedX = -1;
        lastPickedY = -1;
    }
    
    public boolean malom(int i, int j) { // Megvizsgalja, hogy a legutobb lerakott babu lerakasaval malom keletkezik-e    
        boolean horizontal = true;
        boolean vertical = true;
        if (i==lastPickedX && j==lastPickedY) {
            lastPickedX = -1;
            lastPickedY = -1;
            return false;
        }
        else if (i!=3 && j!=3) {
            horizontal = isHorizontalMalom(i, j, 0, 7);
            vertical = isVerticalMalom(i, j, 0, 7);
            return (horizontal || vertical);
        }
        else if(i==3) {
            if(j<3) {
                horizontal = isHorizontalMalom(i, j, 0, 3);
                vertical = isVerticalMalom(i, j, 0, 7);
                return (horizontal || vertical);
            }
            else {
                horizontal = isHorizontalMalom(i, j, 4, 7);
                vertical = isVerticalMalom(i, j, 0, 7);
                return (horizontal || vertical);
            }
        }
        else if(j==3) {
            if (i<3) {
                horizontal = isHorizontalMalom(i, j, 0, 7);
                vertical = isVerticalMalom(i, j, 0, 3);
                return (horizontal || vertical);
            }
            else {
                horizontal = isHorizontalMalom(i, j, 0, 7);
                vertical = isVerticalMalom(i, j, 4, 7);
                return (horizontal || vertical);
            }
        }
        return false;
    }
    
    public boolean isHorizontalMalom(int i, int j, int x, int y) { // Vizszintes iranyban letrejon-e malom
        boolean horizontal = true;
        int k=x;
        while(k<y && horizontal) {
            if (board[i][k] != -1) {
                horizontal = (board[i][j] == board[i][k]);
            }
            k = k+1;
        }     
        return horizontal;
    }
    
    public boolean isVerticalMalom(int i, int j, int x, int y) { // Fuggoleges iranyban letrejon-e malom
        boolean vertical = true;
        int k=x;
        while(k<y && vertical) {
            if (board[k][j] != -1) {
                vertical = (board[i][j] == board[k][j]);
            }
            k = k+1;
        }
        return vertical;
    }
    
    public boolean remove(int i, int j) { // Leveszi az adott sor, oszlop indexu babut a palyarol
        if (!malom(i, j)) { // Nem malomban levo babut akar levenni
            board[i][j] = 0;
            changePlayer();
            currentPlayer.setOnBoard(currentPlayer.getOnBoard()-1);
            return true;
        }
        else {
            // Van-e olyan elem, ami nincs malomban?
            // Ha nincs, akkor megis leveheti.
            // Ha van, akkor ujra az eredeti jatekos jon, amig leveheto babut nem akar levennni
            boolean l = true;
            int x = 0;
            int y = 0;
            while(x<7 && l) {
                y = 0;
                while(y<7 && l) {
                    if (board[x][y] != currentPlayer.getNumber() && board[x][y] > 0) {
                        l = malom(x, y);
                    }
                    y++;
                }
                x++;
            }
            if (l) {
                board[i][j] = 0;
                changePlayer();
                currentPlayer.setOnBoard(currentPlayer.getOnBoard()-1);
            }
            return l;
        }
    }
    

    public boolean end() { // Vege van-e a jateknak? Akkor van vege, ha valamelyik jatekos babuinak a szam 3 ala cskken
        return ((player1.getInHand() == 0 && player1.getOnBoard() < 3) ||
        (player2.getInHand() == 0 && player2.getOnBoard() < 3));   

    }
    
    public int winner() { // Megadja, ki nyerte a jatekot
        if (player1.getOnBoard()<3) {
            return 2;
        }
        else {
           return 1; 
        }
    }
    
    public int get(int i, int j) { // Megadja, hogy a palya adott mezojen kinek van a babuja, vagy ures
        return board[i][j];
    }
    
    public void pickUp(int i, int j) { // Folvesz a palyarol egy babut a kezebe a jatekos
        board[i][j] = 0;
        currentPlayer.setInHand(1);
        currentPlayer.setOnBoard(currentPlayer.getOnBoard()-1);
        lastPickedX = i;
        lastPickedY = j;
    }
    
    public Player getCurrentPlayer() { // Visszaadja az akutalis jatekost
        return currentPlayer;
    }
    
    public boolean canMove() { // Megadja, hogy tud-e mozogni a jatekos, vagy mindegyik babaja be van szorítva
        if (!currentPlayer.isAllPut()) {
            return true;
        }
        boolean l = false;
        int i=0;
        int j=0;
        while(i<7 && !l) {
            j = 0;
            while(j<7 && !l) {
                if(board[i][j] == currentPlayer.getNumber()) {
                    l = ((up(i,j)[2] == 0) || (down(i,j)[2] == 0) || (right(i,j)[2] == 0) || (left(i,j)[2] == 0));
                }
                j = j+1;
            }
            i = i+1;
        }
        return (currentPlayer.isJumping() || l);
    }
    
    public int[] up(int i, int j) { // Megadja, hogy az adott mezo felso szomszedjanak mik a koordinatai es ki all azon a mezon
        int[] coords = new int[3];
        i = i-1;
        while(i>-1 && board[i][j] == -1 && board[i][j] != -2) {
            i = i-1;
        }
        if (i>-1 && board[i][j] != -1 && board[i][j] != -2) {
            coords[0] = i;
            coords[1] = j;
            coords[2] = board[i][j];
            return coords;
        }
        else {
            coords[0] = -1;
            coords[1] = -1;
            coords[2] = -1;
            return coords;
        }
    }
    
    public int[] down(int i, int j) { // Megadja, hogy az adott mezo also szomszedjanak mik a koordinatai es ki all azon a mezon
        int[] coords = new int[3];
        i = i+1;
        while(i<7 && board[i][j] == -1 && board[i][j] != -2) {
            i = i+1;
        }
        if (!(i>6 || board[i][j] == -1) && board[i][j] != -2) {
            coords[0] = i;
            coords[1] = j;
            coords[2] = board[i][j];
            return coords;
        }
        else {
            coords[0] = -1;
            coords[1] = -1;
            coords[2] = -1;
            return coords;
        }
    }
    
    public int[] right(int i, int j) { // Megadja, hogy az adott mezo jobb oldali szomszedjanak mik a koordinatai es ki all azon a mezon
        int[] coords = new int[3];
        j = j+1;
        while(j<7 && board[i][j] == -1 && board[i][j] != -2) {
            j=j+1;
        }
        if (!(j>6 || board[i][j] == -1) && board[i][j] != -2) {
            coords[0] = i;
            coords[1] = j;
            coords[2] = board[i][j];
            return coords;
        }
        else {
            coords[0] = -1;
            coords[1] = -1;
            coords[2] = -1;
            return coords;
        }
    }
    
    public int[] left(int i, int j) { // Megadja, hogy az adott mezo bal oldali szomszedjanak mik a koordinatai es ki all azon a mezon
        int[] coords = new int[3];
        j = j-1;
        while(j>-1 && board[i][j] == -1 && board[i][j] != -2) {
            j = j-1;
        }
        if (!(j<0 || board[i][j] == -1) && board[i][j] != -2) {
            coords[0] = i;
            coords[1] = j;
            coords[2] = board[i][j];
            return coords;
        }
        else {
            coords[0] = -1;
            coords[1] = -1;
            coords[2] = -1;
            return coords;
        }
    }
    
    public void changePlayer() { // Megvaltoztatja az aktualis jatekos
        if (currentPlayer.equals(player1)) {
            currentPlayer = player2;
        }
        else {
            currentPlayer = player1;
        }
    }
}
