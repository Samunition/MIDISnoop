public class VirtualMIDIThread extends Thread {
    TeVirtualMIDI port;

    public VirtualMIDIThread(String name) {
        this.port = new TeVirtualMIDI( name );
    }

    public VirtualMIDIThread(String name, int maxSysexLength, int flags, String manuId, String prodId) {
        this.port = new TeVirtualMIDI( name, maxSysexLength, flags, manuId, prodId );
    }

    public void shutdown() {
        this.port.shutdown();
    }


    @Override
    public void run() {
        byte[] command;
        try {
            while(true) {
                command = this.port.getCommand();
                this.port.sendCommand( command );
            }
        } catch ( TeVirtualMIDIException e ) {
            e.printStackTrace();
            return;
        }

    }
}
