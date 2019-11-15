package J_20191104_Stepper_Motor;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import SerialPort_lib.SerialComm;
import SerialPort_lib.SerialPortException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;

public class Sterpper_Motor {
    private JComboBox SelectPort;
    private JButton link_button;
    private JPanel Jpanel1;
    private JSlider slider1;
    private JButton button1;
    private JButton 逆時針Button;
    private JTextField a0TextField;

    private SerialPort[] ports;
    private SerialComm RXTX = new SerialComm();;
    private SerialPort serialPort = null;

    public Sterpper_Motor() {
        link_button.addActionListener(new link_button_click());

        SelectPort.addPopupMenuListener(new SelectPort_click());

        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                RXTX.SerialPort_Put_String("s");
                RXTX.SerialPort_Put_String((String.valueOf(100-slider1.getValue())+(char)13));

            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RXTX.SerialPort_Put_String("r");
                RXTX.SerialPort_Put_String((String.valueOf((int)(Double.valueOf( a0TextField.getText())*1.41666666666666666666666))+(char)13));
            }
        });


        逆時針Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RXTX.SerialPort_Put_String("l");
                RXTX.SerialPort_Put_String((String.valueOf((int)(Double.valueOf( a0TextField.getText())*1.41666666666666666666666))+(char)13));
            }
        });

        slider1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseReleased(e);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DC_Moter");
        frame.setContentPane(new Sterpper_Motor().Jpanel1);
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
    private class link_button_click implements ActionListener {
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

        //開啟串口
        private void OpenSerialPort(int sel, int baudRate) {
            Boolean isOpen;
            try {
                RXTX.OpenSerialPort(ports[sel].getSystemPortName(), baudRate);
                RXTX.addDataListener(new SerialPortget());
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

    //串口收到訊息
    public class SerialPortget implements SerialPortPacketListener {

        @Override
        public int getPacketSize() {return 0;}

        @Override
        public int getListeningEvents() {return SerialPort.LISTENING_EVENT_DATA_RECEIVED;}

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            byte[] newData = serialPortEvent.getReceivedData();
                for (int i = 0; i < newData.length; i++) {
                System.out.print((char) newData[i]);
            }
//            System.out.println();
        }
    }

}
