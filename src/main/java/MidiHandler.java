import javafx.scene.control.TextArea;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

/***
 * A class to handle the MIDI Devices available to the system
 */
public class MidiHandler {
    // private MidiDevice.Info[] deviceInfo = MidiSystem.getMidiDeviceInfo();
    private List<MidiDevice> inputDevices = new ArrayList<>();
    private List<MidiDevice> outputDevices = new ArrayList<>();
    private List<String> inputDeviceNames = new ArrayList<>();
    private List<String> outputDeviceNames = new ArrayList<>();
    private Relay relay;
    private MidiDevice inputMD;
    private MidiDevice outputMD;
    private List<VirtualMIDIThread> virtualMIDIS = new ArrayList<>();

    public MidiHandler() {
        updateAvailableDevices();
    }

    /***
     * Updates the available device list and splits the inputs and outputs
     */
    public void updateAvailableDevices() {
        MidiDevice.Info[] deviceInfo = MidiSystem.getMidiDeviceInfo();

        // Clear the lists
        inputDevices.clear();
        outputDevices.clear();
        inputDeviceNames.clear();
        outputDeviceNames.clear();


        // Iterate over the available devices and check whether they are an input or output
        for (MidiDevice.Info info : deviceInfo) {
            try (MidiDevice md = MidiSystem.getMidiDevice(info)) {
                // If it has receivers it can be an output
                if (md.getMaxReceivers() != 0) {
                    outputDevices.add(md);
                    outputDeviceNames.add(md.getDeviceInfo().getName());
                }

                // If it has transmitters it can be an input
                if (md.getMaxTransmitters() != 0) {
                    inputDevices.add(md);
                    inputDeviceNames.add(md.getDeviceInfo().getName());
                }
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(int inputIndex, int outputIndex, TextArea outputTextArea) throws MidiUnavailableException{
        inputMD = inputDevices.get(inputIndex);
        outputMD = outputDevices.get(outputIndex);
        // Open the devices
        inputMD.open();
        outputMD.open();

        // Create the transmitter and receiver
        Transmitter input = inputMD.getTransmitter();
        Receiver output = outputMD.getReceiver();

        // Make relay
        relay = new Relay(input, output, outputTextArea);
    }

    public void stop() {
        if (inputMD != null) {
            inputMD.close();
        }

        if (outputMD != null) {
            outputMD.close();
        }

        if (relay != null) {
            relay.close();
        }

        inputMD = null;
        outputMD = null;
        relay = null;
    }

    /***
     * Input Devices
     *
     * @return The List of available input devices
     */
    public List<MidiDevice> getInputDevices() {
        return inputDevices;
    }

    /***
     * Output Devices
     *
     * @return The List of available output devices
     */
    public List<MidiDevice> getOutputDevices() {
        return outputDevices;
    }

    /***
     * Input Device Names
     *
     * @return The List of available input device names
     */
    public List<String> getInputDeviceNames() {
        return inputDeviceNames;
    }

    /***
     * Output Devices Names
     *
     * @return The List of available output device names
     */
    public List<String> getOutputDeviceNames() {
        return outputDeviceNames;
    }

    public void createVirtualMIDI(String name) {
        VirtualMIDIThread thread = new VirtualMIDIThread(name);
        virtualMIDIS.add(thread);
        thread.start();

    }

    public void destroyVirtualMIDI() {
        for (VirtualMIDIThread port : virtualMIDIS) {
            port.shutdown();
        }
        virtualMIDIS.clear();
    }
}
