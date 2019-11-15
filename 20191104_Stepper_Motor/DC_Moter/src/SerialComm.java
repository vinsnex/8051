
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;


public class SerialComm{
    Port_status port_status = null ;
    private SerialPort ubxPort;


    public void addDataListener(SerialPortPacketListener event_list){
        ubxPort.addDataListener(event_list);
    }

    //取得系統
    public SerialPort[] getSystemPort(){ return SerialPort.getCommPorts(); }

    //開啟串口
    public boolean OpenSerialPort(SerialPort port, int baudRate){
        ubxPort = null;
        SerialPort[] serialports = getSystemPort();
        for(SerialPort portS:serialports)
            if (port.getSystemPortName().equals(portS.getSystemPortName())) {
                ubxPort = portS;
                break;
            }
        if(ubxPort == null) {
            JOptionPane.showMessageDialog(null,
                    "串口"+ port.getSystemPortName() +"已遺失!",
                    "錯誤",JOptionPane.ERROR_MESSAGE);
//            System.out.println("串口"+ port.getSystemPortName() +"已遺失!");
            return false;
        }

        if(ubxPort.openPort()) {
            ubxPort.setBaudRate(baudRate);
            ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
            ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);

            if(port_status == null) {
                port_status = new Port_status();
                port_status.start();
            }
            return  true;
        }
        return false;
    }

    public boolean closeSerialPort() {
        if(ubxPort != null) {
            if(port_status != null) {
                port_status.shut();
                port_status = null;
            }
            ubxPort.closePort();
            ubxPort = null;
            return true;
        }
        return  false;
    }

    public boolean SerialPort_Put_String(byte[] data){
        if(ubxPort.bytesAvailable() == 0) {
            OutputStream os = ubxPort.getOutputStream();//獲得串列埠的輸出流
            try {
                os.write(data);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        System.out.println("SerialPort 遺失!");
        return false;
    }

    class Port_status extends Thread{
        private volatile boolean isRunning = true;

        public  void shut(){

            isRunning = false;
            interrupt();
        }

        public  void open(){
            isRunning = true;
        }

        @Override
        public void run() {
            while(isRunning) {
                if (ubxPort!=null && ubxPort.bytesAvailable() == -1) {
                    int sBaudrate = ubxPort.getBaudRate();
//                    System.out.println("SerialPort 遺失!");
                    for(SerialPort portS:getSystemPort())
                        if(ubxPort!=null && ubxPort.getSystemPortName().equals(portS.getSystemPortName()))
                            OpenSerialPort(portS, sBaudrate);
                }
            }
        }
    }
}
