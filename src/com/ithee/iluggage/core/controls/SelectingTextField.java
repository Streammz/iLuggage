package com.ithee.iluggage.core.controls;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries. These entries can be of any class, and
 * there can be an extra listener for when an entry is selected.
 *
 * Deze class is online gevonden op
 * https://gist.github.com/floralvikings/10290131 met modificaties door iThee
 *
 * @author Caleb Brinkman, iThee
 */
public class SelectingTextField<T> extends TextField {

    /**
     * The maximum amount of entries visible in the results contextmenu.
     */
    private static final int MAX_ENTRIES = 10;

    /**
     * The existing autocomplete entries.
     */
    private final HashSet<T> entries;

    /**
     * The popup used to select an entry.
     */
    private ContextMenu entriesPopup;

    /**
     * A function that gets executed when an entry is selected.
     */
    private Consumer<T> onSelect;

    /**
     * Construct a new AutocompleteTextField.
     */
    public SelectingTextField() {
        super();
        entries = new HashSet<>();
        entriesPopup = new ContextMenu();

        textProperty().addListener((observableValue, s1, s2) -> {
            if (getText().length() == 0) {
                entriesPopup.hide();
            } else {
                LinkedList<T> searchResult = new LinkedList<>();
                LinkedList<T> altSearchResult = new LinkedList<>();
                //searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
                entries.stream().forEach((entry) -> {
                    if (entry.toString().toLowerCase().startsWith(getText().toLowerCase())) {
                        searchResult.add(entry);
                    } else if (entry.toString().toLowerCase().contains(getText().toLowerCase())) {
                        altSearchResult.add(entry);
                    }
                });
                searchResult.addAll(altSearchResult);

                if (entries.size() > 0) {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((value, b1, b2) -> {
            entriesPopup.hide();
        });
    }

    /**
     * Sets the listener that gets executed when an entry is selected.
     * 
     * @param listener The listener.
     */
    public void setOnSelect(Consumer<T> listener) {
        this.onSelect = listener;
    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public HashSet<T> getEntries() {
        return entries;
    }

    /**
     * Populate the entries with the given search results. Display is limited to
     * 10 entries, for performance.
     *
     * @param searchResult The set of mathing strings.
     */
    private void populatePopup(List<T> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), MAX_ENTRIES);
        for (int i = 0; i < count; i++) {
            final T result = searchResult.get(i);
            Label entryLabel = new Label(result.toString());

            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction((event) -> {
                if (onSelect != null) {
                    onSelect.accept(result);
                }
                entriesPopup.hide();
            });

            menuItems.add(item);
        }

        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }

}
