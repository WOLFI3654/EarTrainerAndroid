package de.wolfi3654.gehoerbildungandroid;

import android.graphics.drawable.Drawable;
import android.media.midi.MidiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements MidiDriver.OnMidiStartListener{



    private MidiDriver midiDriver;
    private byte[] event;
    private int[] config;

    private byte midiNote = 0x03C;
    private Step step;

    private Spinner mySpinner;
    private ImageView myImage;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySpinner = (Spinner) findViewById(R.id.spinner);
        mySpinner.setAdapter(new ArrayAdapter<Step>(this, android.R.layout.simple_spinner_item, Step.values()));

        myImage = (ImageView) findViewById(R.id.imageView);
        midiDriver = new MidiDriver();
        // Set the listener.
        midiDriver.setOnMidiStartListener(this);

        calculateRandom();
    }

    @Override
    protected void onResume() {
        midiDriver.start();

        // Get the configuration.
        config = midiDriver.config();

        // Print out the details.
        Log.d(this.getClass().getName(), "maxVoices: " + config[0]);
        Log.d(this.getClass().getName(), "numChannels: " + config[1]);
        Log.d(this.getClass().getName(), "sampleRate: " + config[2]);
        Log.d(this.getClass().getName(), "mixBufferSize: " + config[3]);

        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }

    private void playStep(){
        System.out.println(step.getName());

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    playNote(midiNote);

                    Thread.sleep(2500);
                    stopNote(midiNote);
                    Thread.sleep(500);
                    playNote((byte) (midiNote + step.getStep()));
                    Thread.sleep(2500);
                    stopNote((byte) (midiNote + step.getStep()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    private void playNote(byte midiNote) {

        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = midiNote;  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    private void stopNote(byte midiNote) {

        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = midiNote;  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    private void calculateRandom(){
        midiNote = (byte) ( 50+ThreadLocalRandom.current().nextInt(20));
        step = Step.values()[ThreadLocalRandom.current().nextInt(Step.values().length)];
        while(!step.isEnabled()) step = Step.values()[ThreadLocalRandom.current().nextInt(Step.values().length)];

    }



    public void onCheckClick(View view){

        playStep();
        boolean found = false;
        if(step.equals(mySpinner.getSelectedItem()))found = true;
        {
            System.out.println("Found: "+found);
            final boolean finalFound = found;
            Thread blinker = new Thread(new Runnable() {
                public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myImage.setImageResource(finalFound ?android.R.drawable.checkbox_on_background:android.R.drawable.presence_busy);
                            }
                        });
                        for (int i = 0; i < 4; i++) {
                            Thread.sleep(700);
                            //button.setEnabled(false);

                            Thread.sleep(700);
                            //button.setEnabled(true);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myImage.setImageResource(android.R.drawable.presence_audio_online);
                            }
                        });

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    if(finalFound) {
                        calculateRandom();
                        playStep();
                    }
                }

            });

        blinker.start();
        }

    }

    public void onToggleClick(View view){
        ((Step)this.mySpinner.getSelectedItem()).toggle();
    }

    public void onSkipClick(View view){
        calculateRandom();
        playStep();
    }

    public void onPlayClick(View view){
        playStep();
    }

    @Override
    public void onMidiStart() {
        Log.d(this.getClass().getName(), "onMidiStart()");
    }
}
