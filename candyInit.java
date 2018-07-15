import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//Design by Hsun
public class candyInit{
    private JFrame jfrm;
    private JButton confirmBtn;
    private Container jcp;
    private JLabel jlab[];
    private JTextField jtf[];
    private static Boolean debug = false;
    public candyInit(){
        String title[] ={"水果數量(4~8):",
                         "難度(0、1):"};
        String title2[] ={"4",
                          "1"};
        Font fnt = new Font("Serief", Font.PLAIN, 15);
        jfrm = new JFrame("初始選擇");
        jcp = jfrm.getContentPane();
        jcp.setLayout(null);
        confirmBtn = new JButton("確認");
        jlab = new JLabel[2];
        for(int i=0;i<2;i++){
            jlab[i] = new JLabel(title[i]);
            jlab[i].setFont(fnt);
            jcp.add(jlab[i]);
        }
        jtf = new JTextField[2];
        for(int i=0;i<2;i++){
            jtf[i] = new JTextField(title2[i],2);
            jtf[i].setFont(fnt);
            jcp.add(jtf[i]);
        }
        jlab[0].setBounds(10, 10, 120, 20);
        jlab[1].setBounds(10, 50, 120, 20);
        jtf[0].setBounds(120, 10, 30, 20);
        jtf[1].setBounds(120, 50, 30, 20);
        jcp.add(confirmBtn);
        confirmBtn.setBounds(70, 80, 60, 40);
        confirmBtn.addActionListener(new ActLis());
        jfrm.setSize(200,150);
        jfrm.setResizable(false);
        jfrm.setLocationRelativeTo(null);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setVisible(true);
    }
    class ActLis implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JButton tkbtn = (JButton)e.getSource();
            if(debug) System.out.println("ActionEvent:"+tkbtn.getText());
            if(e.getSource()==confirmBtn){
                try{
                        int hmcandy=Integer.parseInt(jtf[0].getText()),
                        hHard=Integer.parseInt(jtf[1].getText());
                    if((hmcandy>=4&&hmcandy<=8)&&(hHard==0||hHard==1||hHard==27||hHard==87)){
                        if(debug) System.out.println("game start!");
                        jfrm.setVisible(false);
                        candyLayout CL;
                        if(hHard == 0){
                            CL = new candyLayout(hmcandy, 50, 5, 50);
                        }else if(hHard == 1){
                            CL = new candyLayout(hmcandy, 35, 5, 40);
                        }else if(hHard == 27){//win
                            CL = new candyLayout(hmcandy, 10000, 1, 40);
                        }else if(hHard == 87){//lose
                            CL = new candyLayout(hmcandy, 35, 5, 1);
                        }
                    }else{
                        jtf[0].setText("4");
                        jtf[1].setText("1");
                    }
                }catch(Exception ew){
                    jtf[0].setText("4");
                    jtf[1].setText("1");
                }
                
            }
        }
    }
}