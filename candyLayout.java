//版面配置
//Design by Hsun
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.sound.sampled.*;

public class candyLayout implements MouseMotionListener,MouseListener{
    private int frmW,frmH,hmcandy,btnLis[],btnamount;
    private String btnFir;
    private Boolean mousePrs,useUltmate;
    private Image floimg[],expimg[],spefloimg[],alRmfloimg,loseimg,winimg;
    private Thread te;
    private Clip expClip,winClip,loseClip,bgmClip;
    static Boolean debug = false;
    static int hmtoflo = 4,hmLab = 5,hmwinImg = 2,hmloseImg = 2,hmstage = 5,onept = 35,hmturn = 40,hmExpAni = 9,hmFrtToSpawn = 12;//onept->單顆消除分數 hmturn->初始回合數
    static String wtype = "Microsoft JhengHei";
    JFrame jfrm;
    Container jcp,btnCp,labCp;
    JButton candyBtn[],resetbtn,restartbtn;
    JLabel noteLab[],logLab[];
    JLabel picLab;
    JPanel jpl;
    JProgressBar jpb;
    public candyLayout(){
        init();
        jfrm.setSize(frmW,frmH);
        jfrm.setResizable(false);
        jfrm.setLocationRelativeTo(null);
        loadMusic();
        jfrm.setVisible(true);
        loadImage();
        passInit();
    }
    public candyLayout(int setcandy,int setpt,int setstage,int setturn){
        hmtoflo = setcandy;
        onept = setpt;
        hmstage = setstage;
        hmturn = setturn;
        init();
        jfrm.setSize(frmW,frmH);
        jfrm.setResizable(false);
        jfrm.setLocationRelativeTo(null);
        loadMusic();
        jfrm.setVisible(true);
        loadImage();
        passInit();
    }
    //初始化
    public void init(){
        useUltmate = mousePrs = false;
        frmW = 880;
        frmH = 635;
        hmcandy = 40;
        btnLis = new int[1024];
        btnamount = 0;
        te = new Thread();
        final String labTit[] = {"目前關卡：",
                                 "剩餘回合數：",
                                 "已使用回合數：",
                                 "連擊：",
                                 "目前分數"};
        jfrm = new JFrame("candyEliminate");
        candyBtn = new JButton[hmcandy];
        resetbtn = new JButton("Reset(R)");
        resetbtn.setMnemonic('r');
        resetbtn.setActionCommand("Reset");
        resetbtn.addActionListener(new ActLis());
        restartbtn = new JButton("Restart(S)");
        restartbtn.setMnemonic('S');
        restartbtn.setActionCommand("Restart");
        restartbtn.addActionListener(new ActLis());
        noteLab = new JLabel[hmLab];
        logLab = new JLabel[hmLab];
        picLab = new JLabel();
        jpl = new JPanel();
        jpb = new JProgressBar();
        jcp = jfrm.getContentPane();
        btnCp = new Container();
        labCp = new Container();
        //=====
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btnCp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCp.setLayout(new GridLayout(5,8,10,10));
        labCp.setLayout(new FlowLayout(FlowLayout.CENTER));
        jcp.setLayout(new BorderLayout());
        //=====add to layout
        //label
        Font fnt = new Font(wtype, Font.PLAIN, 20);
        for(int i=0;i<hmLab;i++){
            noteLab[i] = new JLabel(labTit[i]);
            logLab[i] = new JLabel("0");
            noteLab[i].setFont(fnt);
            logLab[i].setFont(fnt);
            labCp.add(noteLab[i]);
            labCp.add(logLab[i]);
        }
        Color clr = new Color(0,0,255),clr2 = new Color(255,0,0);
        noteLab[3].setForeground(clr);
        logLab[3].setForeground(clr);
        noteLab[4].setForeground(clr2);
        logLab[4].setForeground(clr2);
        //=====
        resetbtn.setToolTipText("重新開始遊戲");
        restartbtn.setToolTipText("回到選擇畫面並重新開始遊戲");
        labCp.add(resetbtn);
        labCp.add(restartbtn);
        jcp.add(labCp,BorderLayout.NORTH);
        //btn 處理
        for(int i=0;i<hmcandy;i++){
            candyBtn[i] = new JButton();
            btnCp.add(candyBtn[i]);
            candyBtn[i].setFont(fnt);
            candyBtn[i].addMouseMotionListener(this);
            candyBtn[i].addMouseListener(this);
            candyBtn[i].setContentAreaFilled(false);
            candyBtn[i].setBackground(new Color(99,184,255));
        }
        //=====
        //prgressbar處理
        jpb.setMaximum(10000);
        jpb.setStringPainted(true);
        //=====
        jcp.add(btnCp,BorderLayout.CENTER);
        jcp.add(jpb,BorderLayout.SOUTH);
        if(debug) System.out.println(hmtoflo+"顆水果_"+hmstage+"關_一顆"+onept+"分");
    }
    //載入image並設定text
    private void loadImage(){
        final int size = 100;
        //image 載入
        floimg = new Image[hmtoflo];
        spefloimg = new Image[hmtoflo];
        //載入水果圖檔
        for(int i=0;i<hmtoflo;i++){
            try{
                floimg[i] = ImageIO.read(getClass().getResource("resources/flower"+(i+1)+".png"));
                spefloimg[i] = ImageIO.read(getClass().getResource("resources/special/flower"+(i+1)+".png"));
                BufferedImage scaledBI = new BufferedImage(size+20, size, BufferedImage.TYPE_4BYTE_ABGR);
                BufferedImage scaledBIs = new BufferedImage(size+20, size, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g = scaledBI.createGraphics();
                Graphics2D gs = scaledBIs.createGraphics();
                g.setComposite(AlphaComposite.Src);
                gs.setComposite(AlphaComposite.Src);
                g.drawImage(floimg[i], 20, 0, size, size, null); 
                gs.drawImage(spefloimg[i], 20, 0, size, size, null); 
                floimg[i] = scaledBI;
                spefloimg[i] = scaledBIs;
            }catch(Exception e){
                System.out.println("載入水果圖檔發生"+e+"例外");
            }
        }
        try{
            alRmfloimg = ImageIO.read(getClass().getResource("resources/special/flower0.png"));
            BufferedImage scaledBI = new BufferedImage(size+10, size, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = scaledBI.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.drawImage(alRmfloimg, 12, 0, size, size, null); 
            alRmfloimg = scaledBI;
        }catch(Exception e){
            System.out.println("發生"+e+"例外");
        }
        //載入爆炸圖檔
        expimg= new Image[hmExpAni];
        for(int i=0;i<hmExpAni;i++){
            try{
                expimg[i] = ImageIO.read(getClass().getResource("resources/explosionGIF/"+(i+1)+".jpg"));
                BufferedImage scaledBI = new BufferedImage(size, size,BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g = scaledBI.createGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(expimg[i], 0, 0, size, size, null); 
                expimg[i] = scaledBI;
            }catch(Exception e){
                System.out.println("載入爆炸圖檔發生"+e+"例外");
            }
        }
        //=====載入結尾圖檔
        int randtmp;
        try{
            randtmp = (int)(Math.random()*(hmwinImg)+1);
            BufferedImage bi = ImageIO.read(getClass().getResource("resources/win/"+randtmp+".jpg"));
            winimg = bi;
            if(bi.getHeight()>500){
                BufferedImage scaledBI = new BufferedImage(btnCp.getWidth()-10, btnCp.getHeight()-10,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = scaledBI.createGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(winimg, 0, 0, btnCp.getWidth()-10, btnCp.getHeight()-10, null); 
                winimg = scaledBI;
            }
            if(debug) System.out.println("winimg:"+randtmp);
            randtmp = (int)(Math.random()*(hmloseImg)+1);
            bi = ImageIO.read(getClass().getResource("resources/lose/"+randtmp+".jpg"));
            loseimg = bi;
            if(bi.getHeight()>500){
                BufferedImage scaledBI = new BufferedImage(btnCp.getWidth()-10, btnCp.getHeight()-10,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = scaledBI.createGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(loseimg, 0, 0, btnCp.getWidth()-10, btnCp.getHeight()-10, null); 
                loseimg = scaledBI;
            }
            if(debug) System.out.println("loseimg:"+randtmp);
        }catch(Exception e){
            System.out.println("載入結尾圖檔發生"+e+"例外");
        }
        if(debug) System.out.println(btnCp.getHeight()+":"+btnCp.getWidth());
    }
    //allRespawn
    public void respawnCandy(){
        for(int i=0;i<hmcandy;i++){
            int randtmp = (int)(Math.random()*hmtoflo);
            candyBtn[i].setText(String.valueOf(randtmp));
            candyBtn[i].setIcon(new ImageIcon(floimg[randtmp]));
        }
    }
    //allRemove
    public void removeCandy(){
        for(int i=0;i<hmcandy;i++){
            candyBtn[i].setText("");
            candyBtn[i].setIcon(null);
        }
    }
    //通關刷新(含初始化)
    private void passInit(){
        Boolean ctnplay = true;//是否繼續遊玩
        resetbtn.setEnabled(false);
        int stage = Integer.parseInt(logLab[0].getText())+1;
        btnamount = 0;
        mousePrs = false;
        Thread passt;
        if(stage > hmstage){
            //破關
            ctnplay=true;
            passtr pass = new passtr();
            passt = new Thread(pass);
            passt.start();
        }else if(stage!=1){
            //接關
            removeCandy();
            passtr pass = new passtr();
            passt = new Thread(pass);
            passt.start();
            while(passt.isAlive()){
                trSleep(50);
            }
        }
        if(ctnplay){
            logLab[0].setText(String.valueOf(stage));
            logLab[1].setText(String.valueOf(hmturn/stage));
            logLab[2].setText("0");
            candySetBgFuc();
            respawnCandy();
            for(int i=0;i<hmcandy;i++){
                Font fnt = new Font(wtype, Font.PLAIN, 20);
                candyBtn[i].setFont(fnt);
            }
            jpb.setValue(0);
            resetbtn.setEnabled(true);
        }
        logLab[4].setText("0");
    }
    //爆炸動畫
    private void setExpGif(long time,JButton btn){
        for(int i=0;i<9;i++){
            btn.setIcon(new ImageIcon(expimg[i]));
            trSleep(time);
        }
    }
    //載入music
    private void loadMusic(){
        try{
            //=====爆炸音效
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/explosion.wav"));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //緩衝大小，如果音訊檔案不大，可以全部存入緩衝空間。這個數值應該要按照用途來決定
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            expClip = (Clip) AudioSystem.getLine(dataLineInfo);
            expClip.open(audioInputStream);
            expClip.setFramePosition(3000);
            FloatControl gainControl = (FloatControl) expClip.getControl(FloatControl.Type.MASTER_GAIN);
            //gainControl.setValue(-2.0f);

            //=====結尾音效
            //win
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/ahhhhh.wav"));
            audioFormat = audioInputStream.getFormat();
            bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //緩衝大小，如果音訊檔案不大，可以全部存入緩衝空間。這個數值應該要按照用途來決定
            dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            winClip = (Clip) AudioSystem.getLine(dataLineInfo);
            winClip.open(audioInputStream);
            gainControl = (FloatControl) winClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-3.0f);
            
            //lose
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/torment.wav"));
            audioFormat = audioInputStream.getFormat();
            bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //緩衝大小，如果音訊檔案不大，可以全部存入緩衝空間。這個數值應該要按照用途來決定
            dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            loseClip = (Clip) AudioSystem.getLine(dataLineInfo);
            loseClip.open(audioInputStream);
            loseClip.setFramePosition(11000);
            loseClip.setLoopPoints(11000,-1);
            gainControl = (FloatControl) loseClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-3.0f);

            //=====BGM
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/bgm.wav"));
            audioFormat = audioInputStream.getFormat();
            bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //緩衝大小，如果音訊檔案不大，可以全部存入緩衝空間。這個數值應該要按照用途來決定
            dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            bgmClip = (Clip) AudioSystem.getLine(dataLineInfo);
            bgmClip.open(audioInputStream);
            bgmClip.setLoopPoints(0, -1);
            gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-4.0f);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            
        }catch(Exception e){
            System.out.println("載入music發生"+e+"例外");
        }
    }
    //reset基本處理
    public void basicRes(){
        expClip.setFramePosition(3000);
        btnamount = 0;
        mousePrs = false;
        jpb.setValue(0);
        logLab[0].setText("1");
        logLab[1].setText(String.valueOf(hmturn));
        logLab[2].setText("0");
        logLab[4].setText("0");
        respawnCandy();
        candySetBgFuc();
    }
    //event處理
    private void candySetBgFuc(){//�庖_背景為不透明
        for(int i=0;i<btnamount;i++){
            candyBtn[btnLis[i]].setContentAreaFilled(false);
			candyBtn[btnLis[i]].setBackground(new Color(99,184,255));
        }
    }
    //=====按鈕事件
    public void mousePressed(MouseEvent e){
        if(!te.isAlive()) mousePrs = true;
        Boolean chk = true;
        if(mousePrs){
            for(int i = 0;i<hmcandy&&chk;i++){
                if(e.getComponent()==candyBtn[i]){
                    chk = false;
                    candyBtn[i].setContentAreaFilled(true);
                    btnLis[btnamount++] = i;
                    btnFir=String.valueOf(candyBtn[i].getText().charAt(0));
                    if(debug) System.out.println("Press-btnamount:"+btnamount+"-Btn:"+i+"-name:"+candyBtn[i].getText());
                }
            }
            logLab[3].setText(String.valueOf(btnamount));
        }
    }
    public void mouseEntered(MouseEvent e){
        Boolean chk = true;
        if(mousePrs){
            for(int i = 0;i<hmcandy&&chk;i++){
                if(e.getComponent()==candyBtn[i]){
                    chk = false;
                    //如果相同
                    if(String.valueOf(candyBtn[i].getText().charAt(0)).equals(btnFir)){
                        int layer = btnLis[btnamount-1]/8;
                        Boolean avichk = false,rmchk = false,lmchk=false;
                        //layer檢查 判斷是否有效
                        if((btnLis[btnamount-1]+1)/8 == layer&&!avichk){//右移
                            if(btnLis[btnamount-1]+1 == i) avichk = true;
                            rmchk = true;
                        }
                        if((btnLis[btnamount-1]-1)/8 == layer&&!avichk){//左移
                            if(btnLis[btnamount-1]-1 == i) avichk = true;
                            lmchk = true;
                        }
                        if(btnLis[btnamount-1]-8 >= 0&&!avichk){//上移
                            if(btnLis[btnamount-1]-8 == i) avichk = true;
                            if(rmchk) if(btnLis[btnamount-1]-7 == i) avichk = true;
                            if(lmchk) if(btnLis[btnamount-1]-9 == i) avichk = true;
                        }
                        if(btnLis[btnamount-1]+8 <= 39&&!avichk){//下移
                            if(btnLis[btnamount-1]+8 == i) avichk = true;
                            if(rmchk) if(btnLis[btnamount-1]+9 == i) avichk = true;
                            if(lmchk) if(btnLis[btnamount-1]+7 == i) avichk = true;
                        }
                        //=====
                        if(avichk){
                            Boolean dupchk = false;
                            for(int j=0;j<btnamount&&!dupchk;j++) if(btnLis[j]==i) dupchk=true;
                            if(btnamount>1&&btnLis[btnamount-2]==i){//退回檢查
                                if(debug) System.out.println("back one");
                                candyBtn[btnLis[btnamount-1]].setContentAreaFilled(false);
                                btnamount--;
                            }else if(btnamount>2&&dupchk){//重複選取處理
                                if(debug) System.out.println("not illegal!");
                            }else{
                                candyBtn[i].setContentAreaFilled(true);
                                btnLis[btnamount++] = i;
                            }
							if(btnamount>=2){
								candyBtn[btnLis[btnamount-2]].setBackground(new Color(255,215,0));
								candyBtn[btnLis[btnamount-1]].setBackground(new Color(99,184,255));
								if(btnamount>2) candyBtn[btnLis[btnamount-3]].setBackground(new Color(99,184,255));
							}
							logLab[3].setText(String.valueOf(btnamount));
                            if(debug) System.out.println("btnamount:"+(btnamount)+"-Btn:"+i+"-name:"+candyBtn[i].getText());
                        }else{
                            if(debug) System.out.println("not Avalible!");
                        }
                    }
                    //如果不同 不要連成線
                }
            }
        }
    }
    public void mouseReleased(MouseEvent e){
        mousePrs = false;
        candySetBgFuc();
        if(btnamount>=3&&!te.isAlive()){
            resetbtn.setEnabled(false);
            if(debug) System.out.println("IN Process!");
            eliminate exp = new eliminate();
            te = new Thread(exp);
            te.start();
        }else if(btnamount>=1&&candyBtn[btnLis[btnamount-1]].getText().equals("-1")){
            if(debug) System.out.println("All Remove!");
            useUltmate = true;
            resetbtn.setEnabled(false);
            btnamount = hmcandy;
            eliminate exp = new eliminate();
            te = new Thread(exp);
            te.start();
        }else if(btnamount<3){
            btnamount = 0;
        }
        logLab[3].setText("0");
    }
    public void mouseExited(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    //=====Other Function
    private void trSleep(long time){
        try{
            Thread.sleep(time);
        }catch(Exception e){}
    }
    //=====inner class for multi thread
    //消除多執行��
    class eliminate implements Runnable{
        public eliminate(){}
        public void run(){
            int speidx[] = {-1,-1,-1};//index type 是否大於10(0 1)
            if(debug) System.out.println("process-btnamount:"+btnamount);
            jfrm.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            btnCp.setCursor(null);
            //=====是否增加特殊水果
            if(!useUltmate&&btnamount>=5&&btnamount<hmFrtToSpawn){
                if(debug) System.out.println("生成特殊水果");
                speidx[0] = btnLis[btnamount-1];
                speidx[1] = Integer.parseInt(String.valueOf(candyBtn[btnLis[btnamount-1]].getText().charAt(0)));
                speidx[2] = 0;
            }else if(!useUltmate&&btnamount>=hmFrtToSpawn){
                if(debug) System.out.println("生成可愛魔王");
                speidx[0] = btnLis[btnamount-1];
                speidx[1] = Integer.parseInt(String.valueOf(candyBtn[btnLis[btnamount-1]].getText().charAt(0)));
                speidx[2] = 1;
            }
            //=====判斷是否有特殊水果speidx[2]=(1,0)->true
            int btncount =btnamount,spefruitlef=0;
            if(speidx[2]!=-1) btnamount--;
            if(!useUltmate){
                for(int i=0;i<btncount;i++){
                    if(candyBtn[btnLis[i]].getText().contains("s")){
                        int tmp = Integer.parseInt(String.valueOf(candyBtn[btnLis[i]].getText().charAt(0)));
                        if(debug) System.out.println("有特殊水果:"+tmp+"_label:"+candyBtn[btnLis[i]].getText()+"_index:"+btnLis[i]);//<======error
                        int index = btnLis[i];
                        if(tmp%2 != 0){//上下
                            if(debug) System.out.println("上下全消");
                            int inx = index-8;
                            while(inx>=0){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//重複檢查
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//無重複
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx-=8;
                            }
                            inx = index+8;
                            while(inx<=39){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//重複檢查
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//無重複
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx+=8;
                            }
                        }else if(tmp%2 == 0){//左右
                            if(debug) System.out.println("左右全消");
                            int inx = index-1;
                            while(inx/8==index/8){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//重複檢查
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//無重複
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx-=1;
                            }
                            inx = index+1;
                            while(inx/8==index/8){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//重複檢查
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//無重複
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx+=1;
                            }
                        }
                    }else if(candyBtn[btnLis[i]].getText().equals("-1")){
                        useUltmate = true;
                    }
                }
            }
            if(useUltmate){
                logLab[1].setText(String.valueOf(Integer.parseInt(logLab[1].getText())+1));
                logLab[2].setText(String.valueOf(Integer.parseInt(logLab[2].getText())-1));
                spefruitlef-=5;
                btncount = btnamount = hmcandy;
                for(int i = 0;i<btncount;i++){
                    btnLis[i] = i;
                    if(candyBtn[btnLis[i]].getText().contains("s")){
                        if(debug) System.out.println("1s");
                        spefruitlef++;
                    }else if(candyBtn[btnLis[i]].getText().equals("-1")){
                        if(debug) System.out.println("5s");
                        spefruitlef+=5;
                    }
                }
                useUltmate = false;
            }
            //=====排序
            for(int i=0;i<btnamount&&!useUltmate;i++){
                int smallindex =  i;
                for(int j=i+1;j<btnamount;j++){
                    if(btnLis[smallindex]>btnLis[j]) smallindex = j;
                }
                if(smallindex!=i){
                    //交換
                    int tmp = btnLis[smallindex];btnLis[smallindex]=btnLis[i];btnLis[i]=tmp;
                }
            }
            //=====消除
            exptr expt[] = new exptr[btnamount];
            Thread exptr[] = new Thread[btnamount];
            for(int i=0;i<btnamount;i++){
                candyBtn[btnLis[i]].setText("");
                expt[i] = new exptr(50,candyBtn[btnLis[i]]);
                exptr[i] = new Thread(expt[i]);
                exptr[i].start();
            }
            //補充特殊水果
            if(speidx[2]!=-1){
                trSleep(20);
                if(speidx[2] == 1){
                    candyBtn[speidx[0]].setText(String.valueOf("-1"));
                    candyBtn[speidx[0]].setIcon(new ImageIcon(alRmfloimg));
                }else{
                    candyBtn[speidx[0]].setText(String.valueOf(speidx[1]+"s"));
                    candyBtn[speidx[0]].setIcon(new ImageIcon(spefloimg[speidx[1]]));
                }
            }
            //---
            while(exptr[btnamount-1].isAlive()){
                trSleep(50);
            }
            
            expClip.setFramePosition(3000);
            //=====掉落並補充
            for(int i=0;i<btnamount;i++){
                int j=0;
                while(btnLis[i]-(8*(j))>=0){
                    //如果空格到頂則亂數產生新的
                    if((btnLis[i]-(8*(j+1)))<0){
                        int randtmp = (int)(Math.random()*hmtoflo);
                        trSleep(20);
                        candyBtn[btnLis[i]-(8*j)].setText(String.valueOf(randtmp));
                        candyBtn[btnLis[i]-(8*j)].setIcon(new ImageIcon(floimg[randtmp]));
                    }else{
                        //掉落-把上面(j+1)複製到下面的(j)
                        candyBtn[btnLis[i]-(8*(j))].setText(candyBtn[btnLis[i]-(8*(j+1))].getText());
                        candyBtn[btnLis[i]-(8*(j))].setIcon(candyBtn[btnLis[i]-(8*(j+1))].getIcon());
                        trSleep(20);
                        //把上面的清空
                        candyBtn[btnLis[i]-(8*(j+1))].setText("");
                        candyBtn[btnLis[i]-(8*(j+1))].setIcon(null);
                    }
                    j++;
                }
            }
            //===進度條
            if(speidx[2]!=-1) btnamount++;
            //set
            int ptgot;
            if(btnamount>5){
                ptgot = ((5*onept)+((btnamount-5)*(1+(btnamount/10))*onept)+(spefruitlef*8*onept));
            }else{
                ptgot = btnamount*onept+(spefruitlef*8*onept);
            }
            jpb.setValue(jpb.getValue()+ptgot);
            logLab[4].setText(String.valueOf(jpb.getValue()));
            if(debug) System.out.println("btnamount:"+btnamount+"_got:"+ptgot+"_Extragot:"+(spefruitlef*8*onept));
            //進度條判斷
            //===回合數
            //set
            logLab[1].setText(String.valueOf(Integer.parseInt(logLab[1].getText())-1));
            logLab[2].setText(String.valueOf(Integer.parseInt(logLab[2].getText())+1));
            //判斷
            if(jpb.getValue()==10000){
                passInit();
            }else if(logLab[1].getText().equals("0")){
                if(debug) System.out.println("lose");
                jcp.remove(jpb);
                labCp.remove(logLab[0]);
                for(int i = 1;i<hmLab;i++){
                    labCp.remove(noteLab[i]);
                    labCp.remove(logLab[i]);
                }
                labCp.remove(resetbtn);
                Font fnt2 = new Font(wtype, Font.PLAIN, 28);
                noteLab[0].setFont(fnt2);
                logLab[0].setFont(fnt2);
                noteLab[0].setText("感謝你的遊玩!請再接再厲再次挑戰，希望你會喜歡這款遊戲");
                logLab[0].setText("如果喜歡不妨多玩幾次，圖會不一樣呦!過關與否的圖也不同呦!");
                Container cptmp = new Container();
                cptmp.add(logLab[0]);
                cptmp.setLayout(new FlowLayout());
                jcp.add(cptmp,BorderLayout.SOUTH);
                for(int i = 0;i<hmcandy;i++) btnCp.remove(candyBtn[i]);
                btnCp.setLayout(new FlowLayout());
                btnCp.add(picLab);
                picLab.setIcon(new ImageIcon(loseimg));
                bgmClip.stop();
                loseClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            //=====
            btnamount = 0;
            jfrm.setCursor(null);
            btnCp.setCursor(new Cursor(Cursor.HAND_CURSOR));
            resetbtn.setEnabled(true);
        }
    }
    //爆炸多執行��
    class exptr implements Runnable{
        JButton jbtn;
        long time;
        exptr(long t ,JButton btn){
            jbtn = btn;
            time = t;
        }
        public void run(){
            expClip.start();
            setExpGif(time,jbtn);
			trSleep(50);
			jbtn.setIcon(null);
        }
    }
    //過關多執行��
    class passtr implements Runnable{
        public passtr(){}
        public void run(){
            int slpt = 150;
            if(Integer.parseInt(logLab[0].getText()) > hmstage){
                if(debug) System.out.println("win!");
                jcp.remove(jpb);
                labCp.remove(logLab[0]);
                for(int i = 1;i<hmLab;i++){
                    labCp.remove(noteLab[i]);
                    labCp.remove(logLab[i]);
                }
                labCp.remove(resetbtn);
                Font fnt2 = new Font(wtype, Font.PLAIN, 30);
                noteLab[0].setFont(fnt2);
                logLab[0].setFont(fnt2);
                noteLab[0].setText("感謝你的遊玩!恭喜通關，希望你會喜歡這款遊戲");
                logLab[0].setText("如果喜歡不妨多玩幾次，圖會不一樣呦!過關與否的圖也不同呦!");
                Container cptmp = new Container();
                cptmp.add(logLab[0]);
                cptmp.setLayout(new FlowLayout());
                jcp.add(cptmp,BorderLayout.SOUTH);
                for(int i = 0;i<hmcandy;i++) btnCp.remove(candyBtn[i]);
                btnCp.setLayout(new FlowLayout());
                btnCp.add(picLab);
                picLab.setIcon(new ImageIcon(winimg));
                bgmClip.stop();
                winClip.loop(Clip.LOOP_CONTINUOUSLY);
            }else{
                if(debug) System.out.println("IN passtr");
                String tmp[] = {"恭","喜","通","過","第",logLab[0].getText(),"關","！",
                                "您","總","共","花","了",logLab[2].getText(),"回","合"};
                String tmp2[] = {"下","一","關","即","將","在",
                                 "3秒","後",
                                 "開","始"};
                for(int i=0;i<hmcandy;i++){
                    Font fnt2 = new Font(wtype, Font.PLAIN, 40);
                    candyBtn[i].setFont(fnt2);
                }
                for(int i=0;i<tmp.length;i++){
                    candyBtn[i].setText(tmp[i]);
                    trSleep(slpt);
                }
                for(int i=17;i<17+6;i++){
                    candyBtn[i].setText(tmp2[i-17]);
                    trSleep(slpt);
                }
                for(int i=27;i<27+2;i++){
                    candyBtn[i].setText(tmp2[i-27+6]);
                    trSleep(slpt);
                }
                for(int i=35;i<35+2;i++){
                    candyBtn[i].setText(tmp2[i-35+8]);
                    trSleep(slpt);
                }
                for(int i=37;i<37+3;i++){
                    candyBtn[i].setText("。");
                    trSleep(1000);
                }
                
            }
            
        }
    }
    //事件-功能按鈕
    class ActLis implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(debug) System.out.println("ActionEvent:"+e.getActionCommand());
            if(e.getActionCommand().equals("Reset")){
                basicRes();
            }else if(e.getActionCommand().equals("Restart")){
                bgmClip.close();
                winClip.close();
                loseClip.close();
                expClip.close();
                jfrm.dispose();
                candyInit CI = new candyInit();
            }
        }
    }
}