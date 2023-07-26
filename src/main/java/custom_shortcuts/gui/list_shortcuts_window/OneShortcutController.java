package custom_shortcuts.gui.list_shortcuts_window;

import custom_shortcuts.database.SqlController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Optional;
import static custom_shortcuts.gui.main_window.CustomShortcuts.getIcon;

public class OneShortcutController {

	private boolean isEditOn;
	private String[] shortcut;
	private final int id;
	private final SqlController sqlController;
	private final ListShortcutsController listShortcutsController;

	@FXML
	private TextField nameTextField, parametersTextField;

	@FXML
	private TextArea bodyTextArea;

	@FXML
	private Button topButton, bottomButton;

	@FXML
	private FontAwesomeIconView topIcon, bottomIcon;

	@FXML
	private GridPane mainGridPane;

	@FXML
	private void initialize() {
		this.nameTextField.setText(this.shortcut[0]);
		this.parametersTextField.setText(this.shortcut[1]);
		this.bodyTextArea.setText(this.shortcut[2]);
		setOnFocus(this.nameTextField);
		setOnFocus(this.parametersTextField);
		setOnFocus(this.bodyTextArea);
	}

	public OneShortcutController(
			SqlController sqlController, ListShortcutsController listShortcutsController, String[] shortcut, int id) {
		this.sqlController = sqlController;
		this.listShortcutsController = listShortcutsController;
		this.isEditOn = false;
		this.shortcut = shortcut;
		this.id = id;
	}

	public void topButtonClick() {
		if (this.isEditOn) {
			if (isSame()) {
				setEditable(false);
				return;
			}
			Alert question = new Alert(Alert.AlertType.CONFIRMATION);
			question.setHeaderText("Confirmation");
			question.setContentText("Are you sure you want to save changes to shortcut '" + this.shortcut[0] + "'?");
			Stage stage = (Stage) question.getDialogPane().getScene().getWindow();
			stage.getIcons().add(getIcon());
			Optional<ButtonType> option = question.showAndWait();
			if (option.isPresent() && ButtonType.OK.equals(option.get())) {
				String[] newShortcut = new String[] {
						this.nameTextField.getText(),
						this.parametersTextField.getText(),
						this.bodyTextArea.getText()};
				try {
					this.sqlController.updateShortcut(this.shortcut[0], newShortcut);
				} catch (Exception e) {
					Alert errorAlert = new Alert(Alert.AlertType.ERROR);
					errorAlert.setHeaderText("Operation failed");
					errorAlert.setContentText(e.getMessage());
					Stage stage2 = (Stage) errorAlert.getDialogPane().getScene().getWindow();
					stage2.getIcons().add(getIcon());
					errorAlert.showAndWait();
				}
				this.shortcut = newShortcut;
				setEditable(false);
			}
		} else {
			setEditable(true);
		}
	}

	public void bottomButtonClick() {
		if (this.isEditOn) {
			if (isSame()) {
				setEditable(false);
				return;
			}
			Alert question = new Alert(Alert.AlertType.CONFIRMATION);
			question.setHeaderText("Confirmation");
			question.setContentText("Are you sure you want to discard changes to shortcut '" + this.shortcut[0] + "'?");
			Stage stage = (Stage) question.getDialogPane().getScene().getWindow();
			stage.getIcons().add(getIcon());
			Optional<ButtonType> option = question.showAndWait();
			if (option.isPresent() && ButtonType.OK.equals(option.get())) {
				this.nameTextField.setText(this.shortcut[0]);
				this.parametersTextField.setText(this.shortcut[1]);
				this.bodyTextArea.setText(this.shortcut[2]);
				setEditable(false);
			}
		} else {
			Alert question = new Alert(Alert.AlertType.CONFIRMATION);
			question.setHeaderText("Confirmation");
			question.setContentText("Are you sure you want to remove shortcut '" + this.shortcut[0] + "'?");
			Stage stage = (Stage) question.getDialogPane().getScene().getWindow();
			stage.getIcons().add(getIcon());
			Optional<ButtonType> option = question.showAndWait();
			if (option.isPresent() && ButtonType.OK.equals(option.get())) {
				try {
					this.sqlController.deleteShortcut(this.shortcut[0]);
					this.listShortcutsController.removeRowAtIndex(this.id);
				} catch (Exception e) {
					Alert errorAlert = new Alert(Alert.AlertType.ERROR);
					errorAlert.setHeaderText("Operation failed");
					errorAlert.setContentText(e.getMessage());
					Stage stage2 = (Stage) errorAlert.getDialogPane().getScene().getWindow();
					stage2.getIcons().add(getIcon());
					errorAlert.showAndWait();
				}
			}
		}
	}

	private void setEditable(boolean editable) {
		this.isEditOn = editable;
		this.nameTextField.setEditable(editable);
		this.parametersTextField.setEditable(editable);
		this.bodyTextArea.setEditable(editable);
		if (editable) {
			this.topIcon.setIcon(FontAwesomeIcon.SAVE);
			this.bottomIcon.setIcon(FontAwesomeIcon.CLOSE);
		} else {
			this.topIcon.setIcon(FontAwesomeIcon.EDIT);
			this.bottomIcon.setIcon(FontAwesomeIcon.TRASH_ALT);
		}
	}

	private boolean isSame() {
		if (!this.nameTextField.getText().equals(this.shortcut[0])) {
			return false;
		}
		if (!this.parametersTextField.getText().equals(this.shortcut[1])) {
			return false;
		}
		return this.bodyTextArea.getText().equals(this.shortcut[2]);
	}

	private void setOnFocus(Node node) {
		node.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue && !this.isEditOn) {
				this.listShortcutsController.setFocus();
			}
		});
	}
}