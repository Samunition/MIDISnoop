import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javax.sound.midi.MidiUnavailableException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


public class MidiSnoopController {
    // GUI Elements
    @FXML private Button virtualDeviceButton;
    @FXML private Button refreshButton;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;
    @FXML private ComboBox inputComboBox;
    @FXML private ComboBox outputComboBox;
    @FXML private TextArea outputTextArea;
    @FXML private AnchorPane anchorPane;
    private Alert alert = new Alert(Alert.AlertType.ERROR);

    // Handler for all MIDI stuff
    private MidiHandler midiHandler = new MidiHandler();

    @FXML
    public void initialize() {

        // Create Text input dialog for virtual midi creation
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create a Virtual MIDI");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter a name for the virtual MIDI cable");

        // Add listeners to the GUI here
        startButton.setOnAction(event -> {
            // Get selection from combo boxes
            int inputIndex = inputComboBox.getSelectionModel().getSelectedIndex();
            int outputIndex = outputComboBox.getSelectionModel().getSelectedIndex();

            if (inputIndex == -1 || outputIndex == -1) {
                alert.setContentText("Please Select an input and an output");
                alert.showAndWait();
                return;
            }

            // Open the connections if possible
            try {
                midiHandler.start(inputIndex, outputIndex, outputTextArea);
                outputTextArea.appendText("Input: " + inputComboBox.getSelectionModel().getSelectedItem() + " -> Output: " + outputComboBox.getSelectionModel().getSelectedItem() + "\n");
            }
            catch (MidiUnavailableException e){
                alert.setContentText("One of the MIDI devices is unavailable. Please make sure that another program is not currently using it!");
                alert.showAndWait();
                return;
            }

            // If we are good to go change button/combobox states
            stopButton.setDisable(false);
            startButton.setDisable(true);
            saveButton.setDisable(true);
            inputComboBox.setDisable(true);
            outputComboBox.setDisable(true);
            virtualDeviceButton.setDisable(true);
            refreshButton.setDisable(true);
        });

        stopButton.setOnAction(event -> {
            stopButton.setDisable(true);
            startButton.setDisable(false);
            saveButton.setDisable(false);
            inputComboBox.setDisable(false);
            outputComboBox.setDisable(false);
            virtualDeviceButton.setDisable(false);
            refreshButton.setDisable(false);

            midiHandler.stop();
        });

        clearButton.setOnAction(event -> {
            outputTextArea.clear();
            saveButton.setDisable(true);
        });

        saveButton.setOnAction(event -> {
            javafx.application.Platform.runLater( () -> {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy-kmmss");
                Date date = new Date();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "\\midisnoop" + format.format(date) + ".txt"))) {
                    bw.write(outputTextArea.getText().replaceAll("\n", System.getProperty("line.separator")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Alert success = new Alert(Alert.AlertType.CONFIRMATION);
                success.setContentText("File Successfully saved: " + System.getProperty("user.home") + "\\midisnoop" + format.format(date) + ".txt");
                success.showAndWait();
            });
        });

        refreshButton.setOnAction(event -> {
            // Load a list of Midi devices
            midiHandler.updateAvailableDevices();
            inputComboBox.setItems(FXCollections.observableArrayList(midiHandler.getInputDeviceNames()));
            outputComboBox.setItems(FXCollections.observableArrayList(midiHandler.getOutputDeviceNames()));
        });

        virtualDeviceButton.setOnAction(event -> {
            Optional<String> name = dialog.showAndWait();

            if (name.isPresent()) {
                midiHandler.createVirtualMIDI(name.get());
                refreshButton.fire();
            } else {
                alert.setContentText("No Name was entered for the virtual MIDI cable");
                alert.showAndWait();
            }
        });

        // Populate the combo boxes by triggering a refresh
        refreshButton.fire();
    }

    public void shutdown() {
        midiHandler.stop();
        midiHandler.destroyVirtualMIDI();
    }

}
