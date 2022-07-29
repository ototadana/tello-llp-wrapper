package com.xpfriend.tydrone;

import org.junit.jupiter.api.Test;

public class Example {

    @Test
    public void testGetStates() throws Exception {
        try (SimpleMain main = new SimpleMain()) {
            // start communication with Tello
            main.run();

            for (int i = 0; i < 5; i++) {
                String states = main.getStates();
                System.out.println("States: " + states);
                Thread.sleep(500);
            }

            // get more states
            main.setRichStates(true);

            for (int i = 0; i < 5; i++) {
                String states = main.getStates();
                System.out.println("States: " + states);
                Thread.sleep(500);
            }
        }
    }

    @Test
    public void testVideoRecording() throws Exception {
        try (SimpleMain main = new SimpleMain()) {

            // start communication with Tello
            main.run();

            // sleep 10s (because it takes a while for the video to arrive)
            Thread.sleep(10000);

            // start video recording
            main.setRecording(true);
            Thread.sleep(5000);

            // stop video recording
            main.setRecording(false);
            Thread.sleep(1000);
        }
    }

    @Test
    public void testPicture() throws Exception {
        try (SimpleMain main = new SimpleMain()) {

            // start communication with Tello
            main.run();
            Thread.sleep(2000);

            // take a picture
            main.entryCommand("picture");
            Thread.sleep(3000);
        }
    }

    @Test
    public void testStickCommand() throws Exception {
        try (SimpleMain main = new SimpleMain()) {
            // start communication with Tello
            main.run();

            // takeoff
            main.entryCommand("takeoff");
            Thread.sleep(5000);

            // fly right (+rx)
            main.entryCommand("stick 0.2 0 0 0 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // fly left (-rx)
            main.entryCommand("stick -0.2 0 0 0 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // fly forward (+ry)
            main.entryCommand("stick 0 0.2 0 0 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // fly backward (-ry)
            main.entryCommand("stick 0 -0.2 0 0 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // rotate clockwise (+lx)
            main.entryCommand("stick 0 0 0.5 0 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // rotate counter clockwise (-lx)
            main.entryCommand("stick 0 0 -0.5 0 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // fly up (+ly)
            main.entryCommand("stick 0 0 0 0.2 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // fly down (-ly)
            main.entryCommand("stick 0 0 0 -0.2 0");
            Thread.sleep(2000);

            // hovering
            main.entryCommand("stick 0 0 0 0 0");
            Thread.sleep(2000);

            // land
            main.entryCommand("land");
            Thread.sleep(1000);

            // I want to make sure it lands...
            main.entryCommand("land");
            Thread.sleep(1000);

            main.entryCommand("land");
            Thread.sleep(1000);
        }
    }
}
