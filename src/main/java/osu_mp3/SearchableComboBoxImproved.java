package osu_mp3;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.controlsfx.control.SearchableComboBox;

// Notes: Cannot override setOnAction and setOnHidden from SearchableComboBox as the methods are "final".

public class SearchableComboBoxImproved<T> extends SearchableComboBox<T> {

    private T previousValue = null;

    public SearchableComboBoxImproved() {
        setOnHidden((event) -> handleMenuClosing()); // Default OnHidden behavior.
    }

    public void setOnActionImproved(EventHandler<ActionEvent> eventHandler) {
        setOnAction(event -> {
            // Handles issue with SearchableComboBox running event a few times on a single action.
            T value = getValue();
            if (value == null || value.equals(previousValue)) { return; }
            previousValue = value;
            eventHandler.handle(event);
        });
    }

    public void setOnHiddenImproved(EventHandler<Event> eventHandler) {
        setOnHidden(event -> {
            handleMenuClosing();
            eventHandler.handle(event);
        });
    }

    // Value is set back to previous value on closing of menu with null selection.
    private void handleMenuClosing() {
        if (getValue() == null) { setValue(previousValue); }
    }

}
