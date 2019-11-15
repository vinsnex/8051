package J_20191104_Keypad;

import com.fazecast.jSerialComm.*;
import SerialPort_lib.SerialComm;
import SerialPort_lib.SerialPortException;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Bet_game {
    private JPanel jpanel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JButton link_button;
    private JComboBox SelectPort;
    private JButton resetButton;

    private SerialPort[] ports;

    private SerialComm RXTX = new SerialComm();;
    private SerialPort serialPort = null;
    private JTextField text_list[] = {textField1,textField2,textField3,textField4,textField5,textField6};

    public int count_wire = 0;
    public JTextField[] getText_list() {return text_list;}

    public  Bet_game(){
        SelectPort.addPopupMenuListener(new SelectPort_click() );
        link_button.addActionListener(new link_button_click());
        resetButton.addActionListener(new resetButton_click());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bet_game");
        frame.setContentPane(new Bet_game().jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }




    // 取得系統串口
    private class SelectPort_click implements PopupMenuListener {
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

    //開啟 or 關閉 串口
    private class link_button_click implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(SelectPort.getSelectedIndex() == -1)
                JOptionPane.showMessageDialog(null, "請選擇串口", "錯誤",JOptionPane.ERROR_MESSAGE);
            else if(link_button.getText().equals("連線"))
                OpenSerialPort(SelectPort.getSelectedIndex(), 9600);
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

                e.printStackTrace();
            }
        }

        private void setUI(boolean Openstate){
            if(Openstate){
                link_button.setText("連線中斷");
                link_button.setBackground(new Color(0, 255, 0));
//                sendText.setEnabled(true);
//                push_Button.setEnabled(true);
            }else {
                link_button.setText("連線");
                link_button.setBackground(new Color(255, 0, 0));
//                sendText.setEnabled(false);
//                push_Button.setEnabled(false);
            }
        }
    }

    private class  resetButton_click implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            count_wire = 0;
            for(int i=0;i<6;i++)
                text_list[i].setText("");
        }
    }

    //串口收到訊息
    public class SerialPortListener implements SerialPortPacketListener{

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
//        System.out.println(serialPortEvent.getEventType());
            switch (serialPortEvent.getEventType()){
                case SerialPort.LISTENING_EVENT_DATA_RECEIVED:
                    readComm(serialPortEvent);
                    break;
                default:
                    break;
            }
        }
        boolean is =true;
        private void readComm(SerialPortEvent serialPortEvent){

            byte[] newData = serialPortEvent.getReceivedData();
            int num = Integer.parseInt( String.valueOf((char) newData[0]))+1;
            System.out.println(num);
            if(num ==10)
                num =0;
            if(num<10) {
                text_list[count_wire].setText(text_list[count_wire].getText() + num);
                is = is == true ? false : true;
                if (is) count_wire++;
                Random ran = new Random();
                int ran_num[] = {0, 0, 0, 0, 0, 0};
                int pass = 0;
                String ss = "";
                boolean de=false;
                if (count_wire == 6) {
                    for (int i = 0; i < 6; i++) {
                        ran_num[i] = ran.nextInt(11);

                        ss += ran_num[i] + " ";
                    }
                    ss += "\n\t";
                    for (int i = 0; i < 6; i++)
                        for (int j = 0; j < 6; j++) {
                            if (Integer.parseInt(text_list[i].getText()) == ran_num[j]) {
                                pass++;
                            }

                        }

                    ss += "中獎數：" + pass;
                    JOptionPane.showMessageDialog(null, ss, "", JOptionPane.ERROR_MESSAGE);
                }
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
