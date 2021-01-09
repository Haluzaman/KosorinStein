package main;

import gameState.GameStateManager;
import gameState.InputHandler;
import render.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class GamePanel extends JPanel implements Runnable {

    private static final int WIDTH = 640 / 2;
    private static final int HEIGHT = 480 / 2;
    private static final int SCALE = 2;

    private Thread thread;
    private boolean isRunning = false;

    //our "canvas"
    private BufferedImage image;
    private int[] pixels; //pixels from our "canvas"


    private InputHandler inputHandler;
    private Screen screen;
    private GameStateManager gsm;

    public GamePanel(){
        initFrame();
        init();
        initThread();
    }

    private void initFrame() {
        Dimension d = new Dimension(WIDTH * SCALE,HEIGHT * SCALE);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setFocusable(true);
        this.requestFocus();
        this.setVisible(true);

    }

    private void init() {
        inputHandler = new InputHandler();
        screen = new Screen(WIDTH,HEIGHT);
        image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
//        image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        gsm = new GameStateManager(screen, inputHandler);
    }

    private void initThread(){
        if(this.thread == null){
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    private void destroyThread(){
        try{
            isRunning = false;
            thread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void update(double delta){
        gsm.update(delta);
    }

    private void draw(float dt){
        //clear screen
        screen.clear(Color.BLACK);

        //do actual drawing
        gsm.draw(dt);

        //drawing screen pixels actually on our screen
        int[] pix = screen.getPixels();
        System.arraycopy(pix, 0, this.pixels, 0, pix.length);
    }

    private void drawToScreen(){
        Graphics g2 = getGraphics();

        if(g2 == null) return;

        g2.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
        //releases memory
        g2.dispose();
    }

    @Override
    public void run() {
        long start;
        long wait;
        long elapsed;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final float ns = 1000000000.0f/60.0f;
        float delta = 0;
        int frames = 0;
        int updates = 0;

        addKeyListener(inputHandler);

        while(isRunning){
            //when the loop starts
            start = System.nanoTime();
            delta +=(start - lastTime)/ns;
            lastTime = start;



            while(delta >= 1) {
                update(delta);
                updates++;
                delta--;
            }

            draw(delta);
            drawToScreen();
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("ups : " + updates + "FPS: " + frames);
                updates = frames =  0;
            }

//            how long the update, draw, drawToScreen lasted
            elapsed = System.nanoTime() - start;
            int FPS = 60;
            long targetTime = 1000 / FPS;
            wait = targetTime - elapsed / 1000000;

            if(wait < 0) wait = 5;
            try{
                Thread.sleep(wait);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

        }

        System.exit(0);
    }


}
