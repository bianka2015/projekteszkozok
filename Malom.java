/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package malom;

import java.awt.BorderLayout;
import logic.Logic;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Acer
 */
public class Malom extends JFrame implements ActionListener {
    private Logic logic;
    private JButton matrix[][];
    private MatrixAction matrixAction = new MatrixAction();
    private boolean malom;
    private JLabel current = new JLabel();
    
    public Malom(Logic l) {
        logic = l;
        malom = false;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        matrix = new JButton[7][7];
        setSize(800, 600);
        controls();
        matrix();
    }
    
    public void matrix() { // Kirajzolja a palyat
        // 7x7-es matrix, de csak a malom palyajanak megfelelo pontjai lathatoak es kattinthatoak
        JPanel panel = new JPanel(new GridLayout(7, 7, 20, 20)); 
        for (int i=0; i<7; i++) {
            for (int j=0; j<7; j++) {
                JButton b = new JButton();
                b.setEnabled(false);
                b.setVisible(false);
                matrix[i][j] = b;
                matrix[i][j].setBackground(getFromLogic(i, j));
                matrix[i][j].setActionCommand(i+","+j);
                matrix[i][j].addActionListener(matrixAction);
                panel.add(matrix[i][j]);
            }
        }
        matrix[0][0].setVisible(true);
        matrix[0][0].setEnabled(true);
        matrix[0][3].setVisible(true);
        matrix[0][3].setEnabled(true);
        matrix[0][6].setVisible(true);
        matrix[0][6].setEnabled(true);
        
        matrix[1][1].setVisible(true);
        matrix[1][1].setEnabled(true);
        matrix[1][3].setVisible(true);
        matrix[1][3].setEnabled(true);
        matrix[1][5].setVisible(true);
        matrix[1][5].setEnabled(true);

        matrix[2][2].setVisible(true);
        matrix[2][2].setEnabled(true);
        matrix[2][3].setVisible(true);
        matrix[2][3].setEnabled(true);
        matrix[2][4].setVisible(true);
        matrix[2][4].setEnabled(true);
        
        for (int i=0; i<7; i++) {
            if (i != 3) {
                matrix[3][i].setVisible(true);
                matrix[3][i].setEnabled(true);
            }
        }
        
        matrix[4][2].setVisible(true);
        matrix[4][2].setEnabled(true);
        matrix[4][3].setVisible(true);
        matrix[4][3].setEnabled(true);
        matrix[4][4].setVisible(true);
        matrix[4][4].setEnabled(true);
        
        matrix[5][1].setVisible(true);
        matrix[5][1].setEnabled(true);
        matrix[5][3].setVisible(true);
        matrix[5][3].setEnabled(true);
        matrix[5][5].setVisible(true);
        matrix[5][5].setEnabled(true);
        
        matrix[6][0].setVisible(true);
        matrix[6][0].setEnabled(true);
        matrix[6][3].setVisible(true);
        matrix[6][3].setEnabled(true);
        matrix[6][6].setVisible(true);
        matrix[6][6].setEnabled(true);
                
        revalidate();
        add(panel, BorderLayout.CENTER);
    }
    
    private void refresh() {
        for(int i = 0; i < 7; i++){
            for (int j=0; j<7; j++) {
                matrix[i][j].setBackground(getFromLogic(i, j));
            }
        }
        current.setText("Current player: " + logic.getCurrentPlayer().getNumber());
    }
    
    private Color getFromLogic(int i, int j){ // Kiszinezi a palyat attol fuggoen, hogy kinek a jatekosa van ott (vagy ures)
        int c = logic.get(i, j);
        switch(c){
            case 0:
                return Color.WHITE;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.RED;
            default:
                return Color.WHITE;
        }
    }
    
    public void controls() { // Menu, uj jatek, kilepes, aktualis jatekos kiirasa
        setLayout(new BorderLayout());
        JMenuBar menubar = new JMenuBar();
        add(menubar, BorderLayout.NORTH);
        JMenu game = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(this);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        game.add(newGame);
        game.add(exit);
        menubar.add(game);
        
        JPanel panel = new JPanel();
        JLabel pl1 = new JLabel("Player 1");
        JLabel pl2 = new JLabel("Player 2");
        
        current.setText("Current player: " + logic.getCurrentPlayer().getNumber());
        pl1.setOpaque(true);
        pl2.setOpaque(true);
        pl1.setBackground(Color.BLUE);
        pl2.setBackground(Color.RED);
        panel.add(pl1);
        panel.add(pl2);
        panel.add(current);
        revalidate();
        add(panel, BorderLayout.SOUTH);

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Logic l = new Logic();
        Malom m = new Malom(l);
        m.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Menubeli gombokat figyeli
        if (e.getActionCommand().equals("Exit")) {
            System.exit(0);
        }
        else {
            logic = new Logic();
            refresh();
        }
    }
    
    public class MatrixAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent arg0) {
            String[] s = arg0.getActionCommand().split(",");
            int x = Integer.parseInt(s[0]);
            int y = Integer.parseInt(s[1]);
            JButton button = (JButton)arg0.getSource();
            if (!logic.canMove()) { // Ha nem tud lepni az aktualis jatekos, akkor a masik jon
                logic.changePlayer();
                refresh();
            }
            else {
                if (malom) { // Ha malom van, akkor a malmot kirako jatekos jon, az ellenfeltol vesz le egy babut
                    if(logic.get(x, y) != logic.getCurrentPlayer().getNumber() && logic.get(x, y) != 0) {
                        malom = !logic.remove(x, y);
                    }
                }
                else if(logic.getCurrentPlayer().getInHand() == 0 && logic.get(x, y) == logic.getCurrentPlayer().getNumber()) {
                    logic.pickUp(x, y);
                }
                else if(logic.getCurrentPlayer().getInHand() != 0 && logic.get(x, y) == 0) {
                    logic.put(x, y);
                    if (logic.malom(x, y)) {
                        malom = true;
                    }
                    if (logic.end()) {
                        JOptionPane.showMessageDialog(null, "The winner is: Player "+logic.winner(), "Game over!", JOptionPane.OK_OPTION);
                    }
                }
                if (logic.end()) { // Ha vege a jateknak, kiirja a nyertest
                    JOptionPane.showMessageDialog(null, "The winner is: Player "+logic.winner(), "Game over!", JOptionPane.OK_OPTION);
                }
            }
            refresh();
        }        
        
    }
}
