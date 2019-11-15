package JSerialPortTool;/*
https://fazecast.github.io/jSerialComm/
https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
*/

//import com.fazecast.jSerialComm.SerialPort;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import SerialPort_lib.SerialComm;
import SerialPort_lib.SerialPortException;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SerialPortTool {
    private JTextField sendText;
    private JTextArea read_text;
    private JRadioButton RadioButton;
    private JPanel JPanel;
    private JButton push_Button;
    private JButton link_button;
    private JComboBox SelectbaudRate;
    private JComboBox SelectPort;
    private JScrollPane ScrollPane;
    private JButton read_text_claer;
    private com.fazecast.jSerialComm.SerialPort[] ports;

    public JRadioButton getRadioButton() { return RadioButton;}
    public JScrollPane getScrollPane() { return ScrollPane; }

    public SerialComm RXTX = new SerialComm();;
    public SerialPort serialPort = null;
    public SerialPortTool() {
        SelectPort.addPopupMenuListener(new SelectPort_click() );
        link_button.addActionListener(new link_button_click());
        push_Button.addActionListener(new push_Button_click());
        sendText.addActionListener(new sendtext_enter());
        read_text_claer.addActionListener(new read_text_clear());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JSerialPortTool");
        frame.setContentPane(new SerialPortTool().JPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(350,300);
//        frame.setAlwaysOnTop(true); //視窗至頂
        frame.setVisible(true);
    }

    private class read_text_clear implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            read_text.setText("");
        }
    }

    private class sendtext_enter implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(RXTX.SerialPort_Put_String((sendText.getText()+(char)13)))
                sendText.setText("");

        }
    }

    private class push_Button_click implements ActionListener{
        int count=0;
        @Override
        public void actionPerformed(ActionEvent e) {
            if(RXTX.SerialPort_Put_String((sendText.getText()+(char)13)))
                sendText.setText("");
        }
    }

    private class SelectPort_click implements PopupMenuListener{
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            SelectPort.removeAllItems();
            ports = RXTX.getSystemPort();
            for(int i=0;i<ports.length;i++) {
//                SelectPort.addItem(ports[i].getSystemPortName() + ": " + ports[i].getDescriptivePortName() + " - " + ports[i].getPortDescription());
                SelectPort.addItem(ports[i].getSystemPortName() );
            }
        }
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
    }

    private class link_button_click implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(SelectPort.getSelectedIndex() == -1)
                JOptionPane.showMessageDialog(null, "請選擇串口", "錯誤",JOptionPane.ERROR_MESSAGE);
            else if(link_button.getText().equals("連線"))
                OpenSerialPort(SelectPort.getSelectedIndex(), (Integer)SelectbaudRate.getSelectedIndex());
            else closeSerialPort(serialPort);
        }

        private void closeSerialPort(SerialPort serialPort) {
            RXTX.closeSerialPort();
            setUI(false);
        }

        private void OpenSerialPort(int sel, int baudRate) {
            Boolean isOpen;
            try {
                RXTX.OpenSerialPort(ports[sel].getSystemPortName(), baudRate);
                RXTX.addDataListener(new SerialPortListener());
                setUI(true);
            } catch (SerialPortException e) {
                System.out.println(e.getMessage());
//                e.printStackTrace();
            }
        }

        private void setUI(boolean Openstate){
            if(Openstate){
                link_button.setText("連線中斷");
                link_button.setBackground(new Color(0, 255, 0));
                sendText.setEnabled(true);
                push_Button.setEnabled(true);
            }else {
                link_button.setText("連線");
                link_button.setBackground(new Color(255, 0, 0));
                sendText.setEnabled(false);
                push_Button.setEnabled(false);
            }
        }
    }


    public class SerialPortListener implements SerialPortPacketListener{
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            switch (serialPortEvent.getEventType()){
                case SerialPort.LISTENING_EVENT_DATA_RECEIVED:
                    readComm(serialPortEvent);
                    break;
                default:
                    break;
            }
        }

        private void readComm(SerialPortEvent serialPortEvent){
            for(byte _get_data : serialPortEvent.getReceivedData()) // 接收資料
                read_text.append(String.valueOf((char) _get_data));

            if(RadioButton.isSelected()) { //JScrollPane 自動轉動
                int max = ScrollPane.getVerticalScrollBar().getMaximum(); // 取得JScrollPane最大值
                if(max != ScrollPane.getVerticalScrollBar().getVisibleAmount())
                    ScrollPane.getViewport().setViewPosition(new Point(0, max)); // 轉動到末端
            }
        }

        @Override
        public int getPacketSize() {
            return 0;
        }

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }
    }


}


