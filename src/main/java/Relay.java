import javafx.scene.control.TextArea;

import javax.sound.midi.*;
import java.util.Arrays;

public class Relay implements Receiver {
    private Transmitter input;
    private Receiver output;
    private TextArea outputTextArea;

    public Relay(Transmitter input, Receiver output, TextArea outputTextArea) {
        this.input = input;
        this.output = output;
        this.outputTextArea = outputTextArea;
        input.setReceiver(this);
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        byte[] b = message.getMessage();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            if (i > 0) {
                sb.append(':');
            }

            sb.append(Integer.toString(( b[i] & 0xff ) + 0x100, 16 ).substring( 1 ));
        }

        String[] noteString = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
        int octave = (b[1] / 12) - 1;
        int noteIndex = (b[1] % 12);
        String note = noteString[noteIndex];

        // Write the message to the text box
        javafx.application.Platform.runLater( () -> outputTextArea.appendText("Signed Decimal: " + Arrays.toString(message.getMessage()) + " -> Hex: " + sb.toString() + " -> Note: " + note + octave + "\n"));

        // Send the message to the output
        output.send(message, timeStamp);
    }

    @Override
    public void close() {
        input.close();
        output.close();
    }
}
