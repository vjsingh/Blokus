package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class PreviousMovesList extends JPanel implements ListSelectionListener {
    JList _list;
    DefaultListModel _data;
    MainWindow _gui;
    boolean _changing=false;

    public PreviousMovesList(MainWindow gui) {
        super();
        _gui = gui;

        _data = new DefaultListModel();

        _list = new JList(_data);
        _list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        _list.setCellRenderer(new PreviousMoveListCellRenderer());
        _list.setBackground(java.awt.Color.black);
        _list.addListSelectionListener(this);

        this.setBackground(java.awt.Color.black);
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, _list);
    }

    public void add(PreviousMovePanel prevMove) {
        _data.add(0, prevMove);
        //_data.addElement(prevMove);
    }

    public void removeAllElements() {
        _changing=true;
        for (int i=0;i<_data.getSize();i++)
        {
            _data.removeElementAt(i);
        }
        _changing=false;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!_changing)
        {
        PreviousMovePanel selected = (PreviousMovePanel) _data.get(_list.getSelectedIndex());
        _gui.tempRevert(selected.getBoard());
        }
    }

    public void revert(){
        for (int i=0;i<_data.getSize();i++)
        {
            ((PreviousMovePanel) _data.get(i)).setSelected(false);
        }
    }


    private class PreviousMoveListCellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            int selectedIndex = index;
            PreviousMovePanel panel = (PreviousMovePanel) _data.get(selectedIndex);
            for (int i = 0; i < _data.getSize(); i++) {
                PreviousMovePanel p = (PreviousMovePanel) _data.get(i);
                p.setSelected(false);
            }
            panel.setSelected(isSelected);
            return panel;
        }
    }

    //for testing
    protected Object[] getElements() {
        return _data.toArray();
    }
}
