//�����t�m
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
    static int hmtoflo = 4,hmLab = 5,hmwinImg = 7,hmloseImg = 6,hmstage = 5,onept = 35,hmturn = 40,hmExpAni = 9,hmFrtToSpawn = 12;//onept->������������ hmturn->��l�^�X��
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
    //��l��
    public void init(){
        useUltmate = mousePrs = false;
        frmW = 880;
        frmH = 635;
        hmcandy = 40;
        btnLis = new int[1024];
        btnamount = 0;
        te = new Thread();
        final String labTit[] = {"�ثe���d�G",
                                 "�Ѿl�^�X�ơG",
                                 "�w�ϥΦ^�X�ơG",
                                 "�s���G",
                                 "�ثe����"};
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
        resetbtn.setToolTipText("���s�}�l�C��");
        restartbtn.setToolTipText("�^���ܵe���í��s�}�l�C��");
        labCp.add(resetbtn);
        labCp.add(restartbtn);
        jcp.add(labCp,BorderLayout.NORTH);
        //btn �B�z
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
        //prgressbar�B�z
        jpb.setMaximum(10000);
        jpb.setStringPainted(true);
        //=====
        jcp.add(btnCp,BorderLayout.CENTER);
        jcp.add(jpb,BorderLayout.SOUTH);
        if(debug) System.out.println(hmtoflo+"�����G_"+hmstage+"��_�@��"+onept+"��");
    }
    //���Jimage�ó]�wtext
    private void loadImage(){
        final int size = 100;
        //image ���J
        floimg = new Image[hmtoflo];
        spefloimg = new Image[hmtoflo];
        //���J���G����
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
                System.out.println("���J���G���ɵo��"+e+"�ҥ~");
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
            System.out.println("�o��"+e+"�ҥ~");
        }
        //���J�z������
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
                System.out.println("���J�z�����ɵo��"+e+"�ҥ~");
            }
        }
        //=====���J��������
        int randtmp;
        try{
            randtmp = (int)(Math.random()*(hmwinImg-1)+1);
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
            randtmp = (int)(Math.random()*(hmloseImg-1)+1);
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
            System.out.println("���J�������ɵo��"+e+"�ҥ~");
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
    //�q����s(�t��l��)
    private void passInit(){
        Boolean ctnplay = true;//�O�_�~��C��
        resetbtn.setEnabled(false);
        int stage = Integer.parseInt(logLab[0].getText())+1;
        btnamount = 0;
        mousePrs = false;
        Thread passt;
        if(stage > hmstage){
            //�}��
            ctnplay=true;
            passtr pass = new passtr();
            passt = new Thread(pass);
            passt.start();
        }else if(stage!=1){
            //����
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
    //�z���ʵe
    private void setExpGif(long time,JButton btn){
        for(int i=0;i<9;i++){
            btn.setIcon(new ImageIcon(expimg[i]));
            trSleep(time);
        }
    }
    //���Jmusic
    private void loadMusic(){
        try{
            //=====�z������
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/explosion.wav"));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //�w�Ĥj�p�A�p�G���T�ɮפ��j�A�i�H�����s�J�w�ĪŶ��C�o�Ӽƭ����ӭn���ӥγ~�ӨM�w
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            expClip = (Clip) AudioSystem.getLine(dataLineInfo);
            expClip.open(audioInputStream);
            expClip.setFramePosition(3000);
            FloatControl gainControl = (FloatControl) expClip.getControl(FloatControl.Type.MASTER_GAIN);
            //gainControl.setValue(-2.0f);

            //=====��������
            //win
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/ahhhhh.wav"));
            audioFormat = audioInputStream.getFormat();
            bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //�w�Ĥj�p�A�p�G���T�ɮפ��j�A�i�H�����s�J�w�ĪŶ��C�o�Ӽƭ����ӭn���ӥγ~�ӨM�w
            dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            winClip = (Clip) AudioSystem.getLine(dataLineInfo);
            winClip.open(audioInputStream);
            gainControl = (FloatControl) winClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-3.0f);
            
            //lose
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("resources/music/torment.wav"));
            audioFormat = audioInputStream.getFormat();
            bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //�w�Ĥj�p�A�p�G���T�ɮפ��j�A�i�H�����s�J�w�ĪŶ��C�o�Ӽƭ����ӭn���ӥγ~�ӨM�w
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
            bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //�w�Ĥj�p�A�p�G���T�ɮפ��j�A�i�H�����s�J�w�ĪŶ��C�o�Ӽƭ����ӭn���ӥγ~�ӨM�w
            dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
            bgmClip = (Clip) AudioSystem.getLine(dataLineInfo);
            bgmClip.open(audioInputStream);
            bgmClip.setLoopPoints(0, -1);
            gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-4.0f);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            
        }catch(Exception e){
            System.out.println("���Jmusic�o��"+e+"�ҥ~");
        }
    }
    //reset�򥻳B�z
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
    //event�B�z
    private void candySetBgFuc(){//���_�I�������z��
        for(int i=0;i<btnamount;i++){
            candyBtn[btnLis[i]].setContentAreaFilled(false);
			candyBtn[btnLis[i]].setBackground(new Color(99,184,255));
        }
    }
    //=====���s�ƥ�
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
                    //�p�G�ۦP
                    if(String.valueOf(candyBtn[i].getText().charAt(0)).equals(btnFir)){
                        int layer = btnLis[btnamount-1]/8;
                        Boolean avichk = false,rmchk = false,lmchk=false;
                        //layer�ˬd �P�_�O�_����
                        if((btnLis[btnamount-1]+1)/8 == layer&&!avichk){//�k��
                            if(btnLis[btnamount-1]+1 == i) avichk = true;
                            rmchk = true;
                        }
                        if((btnLis[btnamount-1]-1)/8 == layer&&!avichk){//����
                            if(btnLis[btnamount-1]-1 == i) avichk = true;
                            lmchk = true;
                        }
                        if(btnLis[btnamount-1]-8 >= 0&&!avichk){//�W��
                            if(btnLis[btnamount-1]-8 == i) avichk = true;
                            if(rmchk) if(btnLis[btnamount-1]-7 == i) avichk = true;
                            if(lmchk) if(btnLis[btnamount-1]-9 == i) avichk = true;
                        }
                        if(btnLis[btnamount-1]+8 <= 39&&!avichk){//�U��
                            if(btnLis[btnamount-1]+8 == i) avichk = true;
                            if(rmchk) if(btnLis[btnamount-1]+9 == i) avichk = true;
                            if(lmchk) if(btnLis[btnamount-1]+7 == i) avichk = true;
                        }
                        //=====
                        if(avichk){
                            Boolean dupchk = false;
                            for(int j=0;j<btnamount&&!dupchk;j++) if(btnLis[j]==i) dupchk=true;
                            if(btnamount>1&&btnLis[btnamount-2]==i){//�h�^�ˬd
                                if(debug) System.out.println("back one");
                                candyBtn[btnLis[btnamount-1]].setContentAreaFilled(false);
                                btnamount--;
                            }else if(btnamount>2&&dupchk){//���ƿ���B�z
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
                    //�p�G���P ���n�s���u
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
    //�����h���掫
    class eliminate implements Runnable{
        public eliminate(){}
        public void run(){
            int speidx[] = {-1,-1,-1};//index type �O�_�j��10(0 1)
            if(debug) System.out.println("process-btnamount:"+btnamount);
            jfrm.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            btnCp.setCursor(null);
            //=====�O�_�W�[�S����G
            if(!useUltmate&&btnamount>=5&&btnamount<hmFrtToSpawn){
                if(debug) System.out.println("�ͦ��S����G");
                speidx[0] = btnLis[btnamount-1];
                speidx[1] = Integer.parseInt(String.valueOf(candyBtn[btnLis[btnamount-1]].getText().charAt(0)));
                speidx[2] = 0;
            }else if(!useUltmate&&btnamount>=hmFrtToSpawn){
                if(debug) System.out.println("�ͦ��i�R�]��");
                speidx[0] = btnLis[btnamount-1];
                speidx[1] = Integer.parseInt(String.valueOf(candyBtn[btnLis[btnamount-1]].getText().charAt(0)));
                speidx[2] = 1;
            }
            //=====�P�_�O�_���S����Gspeidx[2]=(1,0)->true
            int btncount =btnamount,spefruitlef=0;
            if(speidx[2]!=-1) btnamount--;
            if(!useUltmate){
                for(int i=0;i<btncount;i++){
                    if(candyBtn[btnLis[i]].getText().contains("s")){
                        int tmp = Integer.parseInt(String.valueOf(candyBtn[btnLis[i]].getText().charAt(0)));
                        if(debug) System.out.println("���S����G:"+tmp+"_label:"+candyBtn[btnLis[i]].getText()+"_index:"+btnLis[i]);//<======error
                        int index = btnLis[i];
                        if(tmp%2 != 0){//�W�U
                            if(debug) System.out.println("�W�U����");
                            int inx = index-8;
                            while(inx>=0){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//�����ˬd
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//�L����
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx-=8;
                            }
                            inx = index+8;
                            while(inx<=39){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//�����ˬd
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//�L����
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx+=8;
                            }
                        }else if(tmp%2 == 0){//���k
                            if(debug) System.out.println("���k����");
                            int inx = index-1;
                            while(inx/8==index/8){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//�����ˬd
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//�L����
                                    btnLis[btnamount++] = inx;
                                    btncount++;
                                }
                                inx-=1;
                            }
                            inx = index+1;
                            while(inx/8==index/8){
                                Boolean dupchk = false;
                                for(int j=0;j<btncount&&!dupchk;j++){//�����ˬd
                                    if(btnLis[j]==inx) dupchk = true;
                                }
                                if(!dupchk){//�L����
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
            //=====�Ƨ�
            for(int i=0;i<btnamount&&!useUltmate;i++){
                int smallindex =  i;
                for(int j=i+1;j<btnamount;j++){
                    if(btnLis[smallindex]>btnLis[j]) smallindex = j;
                }
                if(smallindex!=i){
                    //�洫
                    int tmp = btnLis[smallindex];btnLis[smallindex]=btnLis[i];btnLis[i]=tmp;
                }
            }
            //=====����
            exptr expt[] = new exptr[btnamount];
            Thread exptr[] = new Thread[btnamount];
            for(int i=0;i<btnamount;i++){
                candyBtn[btnLis[i]].setText("");
                expt[i] = new exptr(50,candyBtn[btnLis[i]]);
                exptr[i] = new Thread(expt[i]);
                exptr[i].start();
            }
            //�ɥR�S����G
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
            //=====�����øɥR
            for(int i=0;i<btnamount;i++){
                int j=0;
                while(btnLis[i]-(8*(j))>=0){
                    //�p�G�Ů�쳻�h�üƲ��ͷs��
                    if((btnLis[i]-(8*(j+1)))<0){
                        int randtmp = (int)(Math.random()*hmtoflo);
                        trSleep(20);
                        candyBtn[btnLis[i]-(8*j)].setText(String.valueOf(randtmp));
                        candyBtn[btnLis[i]-(8*j)].setIcon(new ImageIcon(floimg[randtmp]));
                    }else{
                        //����-��W��(j+1)�ƻs��U����(j)
                        candyBtn[btnLis[i]-(8*(j))].setText(candyBtn[btnLis[i]-(8*(j+1))].getText());
                        candyBtn[btnLis[i]-(8*(j))].setIcon(candyBtn[btnLis[i]-(8*(j+1))].getIcon());
                        trSleep(20);
                        //��W�����M��
                        candyBtn[btnLis[i]-(8*(j+1))].setText("");
                        candyBtn[btnLis[i]-(8*(j+1))].setIcon(null);
                    }
                    j++;
                }
            }
            //===�i�ױ�
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
            //�i�ױ��P�_
            //===�^�X��
            //set
            logLab[1].setText(String.valueOf(Integer.parseInt(logLab[1].getText())-1));
            logLab[2].setText(String.valueOf(Integer.parseInt(logLab[2].getText())+1));
            //�P�_
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
                noteLab[0].setText("�P�§A���C��!�ЦA���A�F�A���D�ԡA�Ʊ�A�|���w�o�ڹC��");
                logLab[0].setText("�p�G���w�����h���X���A�Ϸ|���@����!�L���P�_���Ϥ]���P��!");
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
    //�z���h���掫
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
    //�L���h���掫
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
                noteLab[0].setText("�P�§A���C��!���߳q���A�Ʊ�A�|���w�o�ڹC��");
                logLab[0].setText("�p�G���w�����h���X���A�Ϸ|���@����!�L���P�_���Ϥ]���P��!");
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
                String tmp[] = {"��","��","�q","�L","��",logLab[0].getText(),"��","�I",
                                "�z","�`","�@","��","�F",logLab[2].getText(),"�^","�X"};
                String tmp2[] = {"�U","�@","��","�Y","�N","�b",
                                 "3��","��",
                                 "�}","�l"};
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
                    candyBtn[i].setText("�C");
                    trSleep(1000);
                }
                
            }
            
        }
    }
    //�ƥ�-�\����s
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