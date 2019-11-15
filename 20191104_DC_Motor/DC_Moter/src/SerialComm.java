
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;


public class SerialComm{

    private SerialPort ubxPort;

    public void addDataListener(SerialPortPacketListener event_list){
        ubxPort.addDataListener(event_list);
    }

    public SerialComm(){

    }

    public SerialPort[] getSystemPort(){
        return SerialPort.getCommPorts();
//        System.out.println("系統可用埠列表："+ports);

    }

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

        boolean openedSuccessfully = ubxPort.openPort();
        ubxPort.setBaudRate(baudRate);
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
        ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);


        return openedSuccessfully;
    }

    public boolean SerialPort_put(String  data){ // 傳送
        if(ubxPort.bytesAvailable() == 0) {
            OutputStream os = ubxPort.getOutputStream();//獲得串列埠的輸出流
            try {
                os.write(data.getBytes());
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

    public boolean closeSerialPort() {
        ubxPort.closePort();
        ubxPort = null;
        //getSystemPort();
        return true;
    }
}
