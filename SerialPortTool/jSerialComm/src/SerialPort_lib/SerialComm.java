package SerialPort_lib;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import java.io.IOException;
import java.io.OutputStream;


public class SerialComm{
    PortEvent portEvent = null ;
    private SerialPort ubxPort = null;
    private SerialPortPacketListener event_list = null;

    public SerialComm(){
        portEvent = new PortEvent();
        portEvent.start();
    }

    // 設定SerialPort 接收數據事件
    public boolean addDataListener(SerialPortPacketListener _event_list){
        if(_event_list != null) {
            event_list = _event_list;
            return ubxPort.addDataListener(_event_list);
        }
        return false;
    }

    //取得系統Port
    public SerialPort[] getSystemPort(){ return SerialPort.getCommPorts();  }

    //開啟SerialPort
    public boolean OpenSerialPort(String port, int baudRate) throws SerialPortException{
        if(ubxPort != null) ubxPort.closePort();

        ubxPort = Port_struct(port);

        if(ubxPort == null){ // 未搜尋到串口
            throw new SerialPortException(port, "OpenSerialPort()", "Port not found");
        }else if(ubxPort.isOpen()) { // 串口已開啟
            throw new SerialPortException(port, "openPort()", "Port already opened");
        }else  if(ubxPort.openPort()) {
            if(event_list != null){ ubxPort.addDataListener(event_list); }
            ubxPort.setBaudRate(baudRate); //設定串口包率
            ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
            ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);
            portEvent.Event_Start();
            return  true;
        }
        return false;
    }

    //關閉串口
    public boolean closeSerialPort() {
        if(ubxPort != null) {
            portEvent.Event_Pause();
            ubxPort.closePort();
            ubxPort = null;
            return true;
        }
        return  false;
    }

    // 尋找串口
    public SerialPort Port_struct(SerialPort _Port){
        if(_Port != null )
            Port_struct(_Port.getSystemPortName());
        return null;
    }

    public SerialPort Port_struct (String _port){
        if(_port != null)
            for(SerialPort portS : getSystemPort())
                if(portS.getSystemPortName().equals(_port))
                    return  portS;
        return null;
    }

    //串口輸出
    public boolean SerialPort_Put_String(String data){
        if(ubxPort.isOpen()) {
            OutputStream os = ubxPort.getOutputStream();//獲得串列埠的輸出流
            try {
                os.write(data.getBytes());// 取得String 的 Bytes 傳出
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    //監看串口狀態 串口中斷重連重連
    class PortEvent extends Thread{
        private volatile boolean isRunning = false;
        private volatile boolean noExit = true;
        private String back_Port;
        private int back_baudRate;
        public void Event_Pause(){ isRunning = false; } //暫停偵測

        public void Event_Start(){ isRunning = true; }

        public void shut(){ noExit = false; }

        @Override
        public void run() {
            while(noExit) {
                if(isRunning && (ubxPort == null || !ubxPort.isOpen())){
                    if(ubxPort != null) {
                        back_Port = ubxPort.getSystemPortName();
                        back_baudRate = ubxPort.getBaudRate();
                    }
                    if(back_Port !=null) {
                        try {
                            OpenSerialPort(back_Port, back_baudRate);
                        } catch (SerialPortException e) {
//                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        }
    }
}
